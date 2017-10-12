/**
 *     Copyright (C) 2013-2014  the original author or authors.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License,
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.excalibur.core.ssh.jsch;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.IOUtils;
import org.excalibur.core.LoginCredentials;
import org.excalibur.core.exception.authorization.AuthorizationException;
import org.excalibur.core.exec.ExecutableChannel;
import org.excalibur.core.exec.ExecutableResponse;
import org.excalibur.core.exec.OnlineChannel;
import org.excalibur.core.io.Payload;
import org.excalibur.core.io.Payloads;
import org.excalibur.core.ssh.SshClient;
import org.excalibur.core.ssh.SshException;
import org.excalibur.core.util.BackoffLimitedRetryHandler;
import org.excalibur.core.util.Strings2;
import org.excalibur.core.util.SystemUtils2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.io.ByteSource;
import com.google.common.net.HostAndPort;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.agentproxy.Connector;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.base.Predicates.or;
import static com.google.common.base.Throwables.getCausalChain;
import static com.google.common.collect.Iterables.any;

public class JschSshClient implements SshClient
{
    private static final Logger logger = LoggerFactory.getLogger(JschSshClient.class.getName());
    private final int SSH_MAX_RETRIES = SystemUtils2.getIntegerProperty("org.excalibur.ssh.max.retries", 100);
    private static final boolean RETRY_AUTHENTICATION = SystemUtils2.getBooleanProperty("org.excalibur.ssh.retry.authentication", false);
    private static final String retryableMessages = "failed to send channel request,channel is not opened,invalid data,End of IO Stream Read,Connection reset,connection is closed by foreign host,socket is not established";

    public interface Connection<T>
    {
        void clear();

        T create() throws Exception;
    }

    public interface ConnectionWithStreams<T> extends Connection<T>
    {
        InputStream getInputStream();

        InputStream getErrStream();
    }

    Connection<ChannelSftp> sftpConnection = new Connection<ChannelSftp>()
    {
        private ChannelSftp sftp;

        @Override
        public void clear()
        {
            if (sftp != null)
            {
                sftp.disconnect();
            }
        }

        @Override
        public ChannelSftp create() throws JSchException
        {
            checkConnected();
            sftp = (ChannelSftp) connection.getSession().openChannel("sftp");
            sftp.connect();
            return sftp;
        }

        @Override
        public String toString()
        {
            return "ChannelSftp()";
        }
    };

    private static final class CloseFtpChannelOnCloseInputStream extends FilterInputStream
    {
        private final ChannelSftp sftp;

        private CloseFtpChannelOnCloseInputStream(InputStream proxy, ChannelSftp sftp)
        {
            super(proxy);
            this.sftp = sftp;
        }

        @Override
        public void close() throws IOException
        {
            super.close();
            if (sftp != null)
            {
                sftp.disconnect();
            }
        }
    }

    class GetConnection implements Connection<Payload>
    {
        private final String path;
        private ChannelSftp sftp;

        GetConnection(String path)
        {
            this.path = checkNotNull(path, "path");
        }

        @Override
        public void clear()
        {
            if (sftp != null)
                sftp.disconnect();
        }

        @Override
        public Payload create() throws Exception
        {
            sftp = acquire(sftpConnection);
            return Payloads.newInputStreamPayload(new CloseFtpChannelOnCloseInputStream(sftp.get(path), sftp));
        }

        @Override
        public String toString()
        {
            return "Payload(path=[" + path + "])";
        }
    };

    class ExecConnection implements Connection<ExecutableResponse>
    {
        private final String command;
        private ChannelExec executor;

        ExecConnection(String command)
        {
            this.command = checkNotNull(command, "command");
        }

        @Override
        public void clear()
        {
            if (executor != null)
            {
                executor.disconnect();
            }
        }

        @Override
        public ExecutableResponse create() throws Exception
        {
            try
            {
                ConnectionWithStreams<ChannelExec> connection = execConnection(command);
                executor = acquire(connection);

                String outputString = Strings2.toStringAndClose(connection.getInputStream());
                String errorString = Strings2.toStringAndClose(connection.getErrStream());

                int errorStatus = executor.getExitStatus();
                int i = 0;

                String message = String.format("bad status -1 %s", toString());
                while ((errorStatus = executor.getExitStatus()) == -1 && i < SSH_MAX_RETRIES)
                {
                    logger.warn("<< " + message);
                    backoffForAttempt(++i, message);
                }
                
                if (errorStatus == -1)
                {
                    throw new SshException(message);
                }

                return new ExecutableResponse(outputString, errorString, errorStatus);
            }
            finally
            {
                clear();
            }
        }

        @Override
        public String toString()
        {
            return "ExecConnection(command=[" + command + "])";
        }
    }

    class ExecChannelConnection implements Connection<ExecutableChannel>
    {
        private final String command;
        private ChannelExec executor;
        private Session session;

        ExecChannelConnection(String command)
        {
            this.command = checkNotNull(command, "command");
        }

        @Override
        public void clear()
        {
            if (executor != null)
            {
                executor.disconnect();
            }

            if (session != null)
            {
                session.disconnect();
            }
        }

        @Override
        public ExecutableChannel create() throws Exception
        {
            // http://stackoverflow.com/questions/6265278/whats-the-exact-differences-between-jsch-channelexec-and-channelshell
            this.session = acquire(SshConnection.builder().from(JschSshClient.this.connection).sessionTimeout(0).build());
            executor = (ChannelExec) session.openChannel("exec");
            executor.setCommand(command);

            executor.setErrStream(new ByteArrayOutputStream());
            
            InputStream inputStream = executor.getInputStream();
            InputStream errStream = executor.getErrStream();
            OutputStream outStream = executor.getOutputStream();
            
            executor.connect();

            return new ExecutableChannel(outStream, inputStream, errStream, new Supplier<Integer>()
            {
                @Override
                public Integer get()
                {
                    int exitStatus = executor.getExitStatus();
                    return exitStatus != -1 ? exitStatus : null;
                }

            }, new Closeable()
            {
                @Override
                public void close() throws IOException
                {
                    clear();
                }
            });
        }

        @Override
        public String toString()
        {
            return "ExecChannel(command=[" + command + "])";
        }
    };

    class ShellChannelConnection implements Connection<OnlineChannel>
    {
        private final AtomicBoolean connected_ = new AtomicBoolean(false);

        private Channel executor;
        private Session session;
        private OnlineChannel channel;

        @Override
        public void clear()
        {
            if (connected_.compareAndSet(true, false))
            {
                this.executor.disconnect();
                this.session.disconnect();
            }
        }

        @Override
        public OnlineChannel create() throws Exception
        {
            if (connected_.compareAndSet(false, true))
            {
                this.session = acquire(SshConnection.builder().from(JschSshClient.this.connection).sessionTimeout(0).build());
                executor = session.openChannel("shell");

                InputStream inputStream = executor.getInputStream();
                OutputStream outStream = executor.getOutputStream();
                
                executor.connect();

                channel = new OnlineChannel(outStream, inputStream, new Closeable()
                {
                    @Override
                    public void close() throws IOException
                    {
                        clear();
                    }
                });
            }
            return channel;
        }

    }

    class PutConnection implements Connection<Void>
    {
        private final String path;
        private final Payload contents;
        private ChannelSftp sftp;

        PutConnection(String path, Payload contents)
        {
            this.path = checkNotNull(path, "path");
            this.contents = checkNotNull(contents, "contents");
        }

        @Override
        public void clear()
        {
            if (sftp != null)
            {
                sftp.disconnect();
            }
        }

        @Override
        public Void create() throws Exception
        {
            sftp = acquire(sftpConnection);
            InputStream is = checkNotNull(contents.openStream(), "inputstream for path %s", path);

            try
            {
                sftp.put(is, path);
            }
            finally
            {
                IOUtils.closeQuietly(is);
            }
            return null;
        }

        @Override
        public String toString()
        {
            return "Put(path=[" + path + "])";
        }
    };

    private final SshConnection connection;
    private final String user;
    private final String host;
    private final BackoffLimitedRetryHandler backoffLimitedRetryHandler;

    public JschSshClient(HostAndPort socket, LoginCredentials loginCredentials, int timeout, Optional<Connector> agentConnector,
            BackoffLimitedRetryHandler backoffLimitedRetryHandler)
    {
        this.host = checkNotNull(socket, "host").getHost();
        this.user = checkNotNull(loginCredentials, "credentials for %s", host).getUser();
        checkArgument(socket.getPort() > 0, "ssh port must be greater than zero" + socket.getPort());
        checkArgument(socket.getPort() < 65535, "ssh port must be less than 65535" + socket.getPort());
        this.backoffLimitedRetryHandler = checkNotNull(backoffLimitedRetryHandler, "backoffLimitedRetryHandler");

        connection = SshConnection.builder()
                .hostAndPort(socket)
                .loginCredentials(loginCredentials)
                .connectionTimeout(timeout)
                .agentConnector(agentConnector)
                .build();
        
        checkNotNull(connection);
    }

    @Override
    public String getUsername()
    {
        return this.user;
    }

    @Override
    public String getHostAddress()
    {
        return this.host;
    }

    @Override
    public Payload get(String path)
    {
        return acquire(new GetConnection(path));
    }

    @Override
    public ExecutableResponse execute(String command)
    {
        return acquire(new ExecConnection(command));
    }

    @Override
    public ExecutableChannel executableChannel(String command)
    {
        return acquire(new ExecChannelConnection(command));
    }

    @Override
    public OnlineChannel shell()
    {
        return acquire(new ShellChannelConnection());
    }

    @Override
    public void connect()
    {
        acquire(this.connection);
    }

    @Override
    public void disconnect()
    {
        connection.clear();
    }

    @Override
    public void put(String path, Payload contents)
    {
        acquire(new PutConnection(path, contents));
    }

    @Override
    public void put(String path, String contents)
    {
        put(path, Payloads.newByteSourcePayload(ByteSource.wrap(checkNotNull(contents, "contents").getBytes())));
    }

    protected <T, C extends Connection<T>> T acquire(C connection)
    {
        connection.clear();
        String errorMessage = String.format("(%s) error acquiring %s", toString(), connection);

        for (int i = 0; i < SSH_MAX_RETRIES; i++)
        {
            try
            {
                logger.debug(">> [{}] acquiring [{}]", toString(), connection);
                T returnVal = connection.create();
                logger.debug("<< [{}] acquired [{}]", toString(), returnVal);
                return returnVal;
            }
            catch (Exception from)
            {
                connection.clear();

                if (i + 1 == SSH_MAX_RETRIES)
                {
                    throw propagate(from, errorMessage);
                }
                else if (shouldRetry(from))
                {
                    logger.warn("[{}] : [{}]", errorMessage , from.getMessage(), from);
                    backoffForAttempt(i + 1, errorMessage + ": " + from.getMessage());
                    continue;
                }
            }
        }
        assert false : "should not reach here!";
        return null;
    }

    protected ConnectionWithStreams<ChannelExec> execConnection(final String command)
    {
        checkNotNull(command, "command");

        return new ConnectionWithStreams<ChannelExec>()
        {
            private ChannelExec executor = null;
            private InputStream inputStream;
            private InputStream errStream;

            @Override
            public void clear()
            {
                if (inputStream != null)
                {
                    IOUtils.closeQuietly(inputStream);
                }

                if (errStream != null)
                {
                    IOUtils.closeQuietly(errStream);
                }

                if (executor != null)
                {
                    executor.disconnect();
                }
            }

            @Override
            public ChannelExec create() throws Exception
            {
                checkConnected();
                executor = (ChannelExec) connection.getSession().openChannel("exec");
                executor.setPty(true);

                executor.setCommand(command);
                inputStream = executor.getInputStream();
                errStream = executor.getErrStream();
                executor.connect();

                return executor;
            }

            @Override
            public InputStream getInputStream()
            {
                return inputStream;
            }

            @Override
            public InputStream getErrStream()
            {
                return errStream;
            }

            @Override
            public String toString()
            {
                return "ChannelExec()";
            }
        };
    }

    private final Predicate<Throwable> retryPredicate = or(instanceOf(ConnectException.class), instanceOf(IOException.class));

    boolean shouldRetry(Exception from)
    {
        Predicate<Throwable> predicate = RETRY_AUTHENTICATION ? Predicates.<Throwable> or(retryPredicate, instanceOf(AuthorizationException.class))
                : retryPredicate;
        if (any(getCausalChain(from), predicate))
        {
            return true;
        }

        if (!Strings.isNullOrEmpty(retryableMessages))
        {
            return any(Splitter.on(",").split(retryableMessages), causalChainHasMessageContaining(from));
        }
        return false;
    }

    Predicate<String> causalChainHasMessageContaining(final Exception from)
    {
        return new Predicate<String>()
        {

            @Override
            public boolean apply(final String input)
            {
                return any(getCausalChain(from), new Predicate<Throwable>()
                {

                    @Override
                    public boolean apply(Throwable arg0)
                    {
                        return (arg0.toString().indexOf(input) != -1) || (arg0.getMessage() != null && arg0.getMessage().indexOf(input) != -1);
                    }

                });
            }

        };
    }

    private void backoffForAttempt(int retryAttempt, String message)
    {
        backoffLimitedRetryHandler.imposeBackoffExponentialDelay(10000L, 2, retryAttempt, SSH_MAX_RETRIES, message);
    }

    private SshException propagate(Exception e, String message)
    {
        message += ": " + e.getMessage();

        if (e.getMessage() != null && e.getMessage().indexOf("Auth fail") != -1)
        {
            throw new AuthorizationException(String.format("(user:%s host:%s) %s", user, host, message), e);
        }

        throw e instanceof SshException ? SshException.class.cast(e) : new SshException("(" + toString() + ") " + message, e);
    }

    private void checkConnected()
    {
        checkState(this.connection.isConnected(),
                String.format("Session (host:%s user:%s) not connected! Please call connect() first.", this.host, this.user));
    }
    
    @Override
    protected void finalize() throws Throwable
    {
        close();
        super.finalize();
    }

    @Override
    public void close() throws IOException
    {
        if (connection != null && this.connection.isConnected())
        {
            disconnect();
        }
    }
}
