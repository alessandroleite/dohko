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
package org.excalibur.service.aws;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;

import ch.ethz.ssh2.ConnectionMonitor;
import ch.ethz.ssh2.ServerHostKeyVerifier;
import ch.ethz.ssh2.StreamGobbler;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class CmsearchCommand implements ShellCommand
{
    private final File cmfile_;
    private final File seqdb_;

    private File output_;
    private File tblout_;

    public CmsearchCommand(File cmFile, File database)
    {
        this.cmfile_ = cmFile;
        this.seqdb_ = database;
    }

    public void execute() throws ShellException, IOException
    {
        CommandLine command = new CommandLine("cmsearch");
        if (output_ != null)
        {
            command.addArguments("-o " + output_.getAbsolutePath());
        }

        if (tblout_ != null)
        {
            command.addArgument(" --tblout " + tblout_.getAbsolutePath());
        }

        command.addArgument(cmfile_.getAbsolutePath());
        command.addArgument(seqdb_.getAbsolutePath());

        getShell().execute(command);
    }

    @Override
    public Shell getShell()
    {
        return new RemoteBashShell("ec2-54-205-141-11.compute-1.amazonaws.com", "ubuntu", System.getProperty("user.home") + "/.ec2/leite.pem");
    }

    public static class LocalBashShell implements Shell
    {
        @Override
        public void execute(CommandLine command) throws ShellException, IOException
        {
            // ProcessBuilder pb = new ProcessBuilder("bash", "c", instruction);
            // Process start = pb.start();
            //
            // try
            // {
            // int exitValue = start.waitFor();
            // }
            // catch (InterruptedException exception)
            // {
            // throw new ShellException();
            // }

            final DefaultExecutor executor = new DefaultExecutor();
            executor.execute(command, new ExecuteResultHandler()
            {
                @Override
                public void onProcessFailed(ExecuteException e)
                {
                }

                @Override
                public void onProcessComplete(int exitValue)
                {
                }
            });
        }
    }

    public static class RemoteBashShell implements Shell
    {
        private final String host_;
        private final String username_;
        private String privateKey_;

        private final Properties config_ = new Properties();

        public RemoteBashShell(String host, String username, String privateKey)
        {
            this.host_ = host;
            this.username_ = username;
            this.privateKey_ = privateKey;

            config_.put("StrictHostKeyChecking", "no");
        }

        @Override
        public void execute(final CommandLine command) throws ShellException, IOException
        {
            Session session = null;
            ChannelExec channel = null;
            BufferedReader reader = null;

            try
            {
                ch.ethz.ssh2.Connection connection = new ch.ethz.ssh2.Connection(host_);

                connection.addConnectionMonitor(new ConnectionMonitor()
                {
                    @Override
                    public void connectionLost(Throwable reason)
                    {
                        System.err.println("Disconnected!");
                        if (reason != null)
                        {
                            reason.printStackTrace();
                        }
                    }
                });

                connection.connect(new ServerHostKeyVerifier()
                {
                    @Override
                    public boolean verifyServerHostKey(String hostname, int port, String serverHostKeyAlgorithm, byte[] serverHostKey)
                            throws Exception
                    {
                        return true;
                    }
                });

                boolean isAuthenticated = connection.authenticateWithPublicKey(username_, new File(privateKey_), null);

                if (!isAuthenticated)
                {
                    throw new RuntimeException("Invalid key!");
                }

                ch.ethz.ssh2.Session sess = connection.openSession();
                // sess.execCommand("uname -a && date && uptime && who");
                sess.execCommand(command.toString());

                InputStream stdout = new StreamGobbler(sess.getStdout());

                BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

                while (true)
                {
                    String line = br.readLine();
                    if (line == null)
                    {
                        br.close();
                        break;
                    }
                    System.out.println(line);
                }

                /* Show exit status, if available (otherwise "null") */

                System.out.println("ExitCode: " + sess.getExitStatus());

                /* Close this session */

                sess.close();

                /* Close the connection */

                connection.close();

                final JSch shell = new JSch();
                shell.addIdentity(privateKey_);
                session = shell.getSession(username_, host_);
                session.setConfig(config_);
                session.connect();

                channel = (ChannelExec) session.openChannel("exec");
                reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                channel.setCommand(command.toString());
                channel.connect();

                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null)
                {
                    result.append(line).append('\n');
                }

                System.out.println(result.toString());
            }
            catch (JSchException e)
            {
                throw new ShellException(e);
            }
            finally
            {
                if (reader != null)
                {
                    reader.close();
                }

                if (channel != null)
                {
                    channel.disconnect();
                }

                if (session != null)
                {
                    session.disconnect();
                }
            }
        }
    }

    public static void main(String[] args) throws ShellException, IOException
    {
        File cm = new File("/home/ubuntu/biocloud/Rfam/11.0/1_1/Rfam.cm.1_1");
        File query = new File("/home/ubuntu/biocloud/Spombe/seqstest.fa");
        File output = new File("/home/ubuntu/biocloud/", "remote-exec.txt");

        CmsearchCommand cmsearch = new CmsearchCommand(cm, query);
        cmsearch.output_ = output;
        cmsearch.execute();
    }
}
