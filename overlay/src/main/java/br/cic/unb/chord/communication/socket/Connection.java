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
package br.cic.unb.chord.communication.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.communication.CommunicationException;
import br.cic.unb.chord.communication.Node;
import br.cic.unb.chord.communication.Request;
import br.cic.unb.chord.communication.Response;
import br.cic.unb.chord.communication.net.RemoteMethods;
import br.cic.unb.chord.data.URL;
import br.cic.unb.chord.io.Closeables2;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

class Connection implements Runnable
{
    /**
     * The LOG for this connection.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Connection.class.getName());

    /**
     * List of ids (String) of Proxies using this connection. When this list is empty the connection can be closed. This is the responsibility of the
     * ConnectionPool for synchronization reasons.
     */
    private final List<String> connectedProxies = Collections.synchronizedList(new LinkedList<String>());

    private final AtomicBoolean disconnected = new AtomicBoolean();

    /**
     * The socket used by this connection.
     */
    private volatile Socket mySocket;

    /**
     * The InputStream associated with the socket of this connection.
     */
    private volatile ObjectInputStream in;

    /**
     * The OutputStream associated with the socket of this connection.
     */
    private volatile ObjectOutputStream out;

    /**
     * Counter to create unique request identifiers, so that waiting threads can be identified.
     */
    private volatile long requestCounter = 0;

    /**
     * The URL of the peer this connection provides access to.
     */
    private volatile URL nodeURL;

    /**
     * The URL of the local peer.
     */
    private volatile URL urlOflocalNode;

    /**
     * Collection of responses received for requests.
     */
    private volatile Map<String, Response> responses;

    /**
     * {@link Map} where threads are put in that are waiting for a response. Key: identifier of the request (same as for the response). Value: The
     * Thread itself.
     */
    private volatile Map<String, WaitingThread> waitingThreads;

    /**
     * Create a new Connection.
     * 
     * @param p
     *            The first SocketProxy for which this connection is created. Must not be null!
     * @throws CommunicationException
     */
    Connection(SocketProxy p) throws CommunicationException
    {
        checkNotNull(p, "socketProxy");

        this.nodeURL = p.getNodeURL();
        this.urlOflocalNode = p.getUrlOfLocalNode();

        this.newClient(p);
        this.makeSocketAvailable();

        LOG.info("Connection {} -> {} initialized!", this.urlOflocalNode, this.nodeURL);
    }

    /**
     * This method establishes the connection if not already done.
     * 
     * @throws CommunicationException
     */
    private void makeSocketAvailable() throws CommunicationException
    {
        if (this.disconnected.get())
        {
            throw new CommunicationException("Connection from " + this.urlOflocalNode + " to remote host " + this.nodeURL + " is broken down. ");
        }

        LOG.debug("makeSocketAvailable() called. Testing for socket availability");

        synchronized (this)
        {
            if (this.responses == null)
            {
                this.responses = new HashMap<String, Response>();
            }

            if (this.waitingThreads == null)
            {
                this.waitingThreads = new HashMap<String, WaitingThread>();
            }

            if (this.mySocket == null)
            {
                try
                {
                    LOG.info("Opening new socket to {}", this.nodeURL);

                    this.mySocket = new Socket(this.nodeURL.getHost(), this.nodeURL.getPort());

                    LOG.debug("Socket created: {}", this.mySocket);

                    this.mySocket.setSoTimeout(5000);

                    this.out = new ObjectOutputStream(this.mySocket.getOutputStream());
                    this.in = new ObjectInputStream(this.mySocket.getInputStream());

                    LOG.debug("Sending connection request!");
                    out.writeObject(new Request(RemoteMethods.CONNECT, "Initial Connection"));

                    try
                    {
                        // set time out, in case the other side does not answer!
                        Response resp = null;
                        boolean timedOut = false;
                        
                        try
                        {
                            LOG.debug("Waiting for connection response!");
                            resp = (Response) in.readObject();
                        }
                        catch (SocketTimeoutException e)
                        {
                            LOG.info("Connection timeout!");
                            timedOut = true;
                        }

                        this.mySocket.setSoTimeout(0);

                        if (timedOut)
                        {
                            throw new CommunicationException("Connection to remote host timed out!");
                        }

                        if (resp != null && resp.getStatus() == Response.REQUEST_SUCCESSFUL)
                        {
                            Thread t = new Thread(this, "Connection_Thread_" + this.nodeURL);
                            t.start();
                        }
                        else
                        {
                            throw new CommunicationException("Establishing connection failed!");
                        }
                    }
                    catch (ClassNotFoundException e)
                    {
                        throw new CommunicationException("Unexpected result received! " + e.getMessage(), e);
                    }
                    catch (ClassCastException e)
                    {
                        throw new CommunicationException("Unexpected result received! " + e.getMessage(), e);
                    }
                }
                catch (UnknownHostException e)
                {
                    throw new CommunicationException("Unknown host: " + this.nodeURL.getHost());
                }
                catch (IOException ioe)
                {
                    throw new CommunicationException(this.urlOflocalNode + ": Could not set up IO channel " + "to host " + this.nodeURL.getHost()
                            + " on port " + this.nodeURL.getPort(), ioe);
                }
            }
        }
        LOG.debug("makeSocketAvailable() finished. Socket {}", this.mySocket);
    }

    void newClient(SocketProxy p)
    {
        checkArgument(this.nodeURL.equals(p.getNodeURL()));
        checkArgument(this.urlOflocalNode.equals(p.getUrlOfLocalNode()));

        synchronized (this.connectedProxies)
        {
            if (!this.connectedProxies.contains(p.getProxyID()))
            {
                this.connectedProxies.add(p.getProxyID());
                LOG.debug("Added proxy {}", p.getProxyID());
            }
        }
    }

    /**
     * Must only be called by ConnectionPool!
     * 
     * @param p
     */
    boolean releaseConnection(SocketProxy p)
    {
        if (!this.nodeURL.equals(p.getNodeURL()))
        {
            throw new IllegalArgumentException();
        }

        if (!this.urlOflocalNode.equals(p.getUrlOfLocalNode()))
        {
            throw new IllegalArgumentException();
        }

        synchronized (this.connectedProxies)
        {
            this.connectedProxies.remove(p.getProxyID());
            LOG.debug("Removed proxy {}", p.getProxyID());
        }

        return (this.connectedProxies.size() == 0);
    }

    /**
     * Send a request to the remote peer.
     * 
     * @param p
     *            The SocketProxy sending the Request. Must have been registered before with {@link #newClient(SocketProxy)}.
     * @param methodIdentifier
     *            The method to invoke on the remote peer. See {@link RemoteMethods}.
     * @param parameters
     *            The parameters of the method.
     * @return The created Request, that has been send to the remote peer.
     * @throws CommunicationException
     */
    Request send(SocketProxy p, RemoteMethods method, Serializable[] parameters) throws CommunicationException
    {
        LOG.debug("Proxy {} tries to send. ProxyList {}", p.getProxyID(), this.connectedProxies);

        synchronized (connectedProxies)
        {
            if (p == null || !this.connectedProxies.contains(p.getProxyID()))
            {
                String message = String.format("Node disconnected before finish the request %s. Proxy = %s, %s", method, p == null ? "null" : p,
                        this.connectedProxies.contains(p.getProxyID()));

                throw new IllegalArgumentException(message);
            }
        }

        if (this.disconnected.get())
        {
            throw new CommunicationException("Connection has been lost!");
        }

        Request request = this.createRequest(method, parameters);
        try
        {
            LOG.debug("Sending request " + request.getReplyWith());
            synchronized (this.out)
            {
                this.out.writeObject(request);
                this.out.flush();
                this.out.reset();
            }
            return request;
        }
        catch (IOException e)
        {
            throw new CommunicationException("Could not connect to node! " + e.getMessage(), e);
        }
    }

    /**
     * Creates a request for the method identified by <code>methodIdentifier</code> with the parameters <code>parameters</code>. Sets also field
     * {@link Request#getReplyWith()}of created {@link Request request}.
     * 
     * @param methodIdentifier
     *            The identifier of the method to request.
     * @param parameters
     *            The parameters for the request.
     * @return The {@link Request request}created.
     */
    private Request createRequest(RemoteMethods method, Serializable[] parameters)
    {
        LOG.debug("Creating request for method {} with parameters {}", method.getMethodName(), java.util.Arrays.deepToString(parameters));

        String responseIdentifier = this.createIdentifier(method);
        Request request = new Request(method, responseIdentifier);
        request.setParameters(parameters);

        LOG.debug("Request {} created.", request);
        return request;
    }

    /**
     * Private method to create an identifier that enables this to associate a {@link Response response}with a {@link Request request}made before.
     * This method is synchronized to protect {@link #requestCounter}from race conditions.
     * 
     * @param methodIdentifier
     *            Integer identifying the method this method is called from.
     * @return Unique Identifier for the request.
     */
    private synchronized String createIdentifier(RemoteMethods method)
    {
        /* Create unique identifier from */
        StringBuilder uid = new StringBuilder();
        /* Time stamp */
        uid.append(System.currentTimeMillis());
        uid.append("-");
        /* counter and */
        uid.append(this.requestCounter++);
        /* methodIdentifier */
        uid.append("-");
        uid.append(method);
        return uid.toString();
    }

    /**
     * This method has to be called by a thread that sent a Request before in order to receive the response. The thread is blocked until a response is
     * received or the connection to the remote peer has been destroyed.
     * 
     * @param request
     *            The Request for which the thread awaits the Response. This must be the return-value of a previous invocation of
     *            {@link #send(SocketProxy, int, Serializable[])}.
     * @return The Response to the provided Request.
     * @throws CommunicationException
     */
    Response waitForResponse(Request request) throws CommunicationException
    {
        if (request == null)
        {
            throw new IllegalArgumentException();
        }

        String responseIdentifier = request.getReplyWith();
        Response response = null;

        LOG.debug("Trying to wait for response with identifier {} for method {}", responseIdentifier, request.getRequestType().getMethodName());

        synchronized (this.responses)
        {
            LOG.debug("No of responses {}", this.responses.size());

            /* Test if we got disconnected while waiting for lock on object */
            if (this.disconnected.get())
            {
                throw new CommunicationException("Connection to remote host is broken down. ");
            }
            /*
             * Test if response is already available (Maybe response arrived before we reached this point).
             */
            response = this.responses.remove(responseIdentifier);
            if (response != null)
            {
                return response;
            }

            /* WAIT FOR RESPONSE */
            /* add current thread to map of threads waiting for a response */
            WaitingThread wt = new WaitingThread(Thread.currentThread());
            this.waitingThreads.put(responseIdentifier, wt);
            while (!wt.hasBeenWokenUp())
            {
                try
                {
                    /*
                     * Wait until notified or time out is reached.
                     */
                    LOG.debug("Waiting for response to arrive.");
                    this.responses.wait();
                }
                catch (InterruptedException e)
                {
                    /*
                     * does not matter as this is intended Thread is interrupted if response arrives
                     */
                }
            }
            LOG.debug("Have been woken up from waiting for response.");

            /* remove thread from map of threads waiting for a response */
            this.waitingThreads.remove(responseIdentifier);

            /* try to get the response if available */
            response = this.responses.remove(responseIdentifier);

            LOG.debug("Response for request with identifier {} for method {} received.", responseIdentifier, request.getRequestType());

            /* if no response available */
            if (response == null)
            {
                LOG.debug("No response received.");
                /* we have been disconnected */
                if (this.disconnected.get())
                {
                    LOG.info("Connection to remote host lost.");
                    throw new CommunicationException("Connection to remote host is broken down. ");
                }
                /* or time out has elapsed */
                else
                {
                    LOG.error("There is no result, but we have not been disconnected. Something went seriously wrong!");
                    throw new CommunicationException("Did not receive a response!");
                }
            }
        }
        return response;
    }

    /**
     * The run methods waits for incoming {@link Response}s and puts them into a data structure from where they can be collected by the associated
     * method call that sent a {@link Request} to the remote peer to that this connection provides access.
     */
    @Override
    public void run()
    {
        while (!this.disconnected.get())
        {
            try
            {
                Response response = (Response) this.in.readObject();
                LOG.debug("Response {} received!", response);

                if (RemoteMethods.SHUTDOWN.equals(response.getMethodIdentifier()))
                {
                    // the other side is shutting down
                    disconnected.set(true);
                    this.connectionClosed();

                    Closeables2.closeQuietly(this.mySocket);

                    this.mySocket = null;
                    this.responses = null;
                    this.waitingThreads = null;
                }
                else
                {
                    this.responseReceived(response);
                }
            }
            catch (ClassNotFoundException cnfe)
            {
                /* should not occur, as all classes must be locally available */
                LOG.error("ClassNotFoundException occured during deserialization of response. There is something seriously wrong here! ", cnfe);
            }
            catch (IOException e)
            {
                if (!this.disconnected.get())
                {
                    LOG.warn("Could not read response from stream!", e);
                }
                else
                {
                    LOG.debug("{}: Connection has been closed!", this);
                }
                this.connectionClosed();
            }
        }
    }

    /**
     * This method is called by {@link #run()}when it receives a {@link Response}. The {@link Thread thread} waiting for the response is woken up and
     * the response is put into {@link Map responses}.
     * 
     * @param response
     */
    private void responseReceived(Response response)
    {
        synchronized (this.responses)
        {
            /* Try to fetch thread waiting for this response */
            LOG.debug("No of waiting threads " + this.waitingThreads);
            WaitingThread waitingThread = this.waitingThreads.get(response.getInReplyTo());

            LOG.debug("Response with id {} received.", response.getInReplyTo());

            /* save response */
            this.responses.put(response.getInReplyTo(), response);
            /* if there is a thread waiting for this response */
            if (waitingThread != null)
            {
                /* wake up the thread */
                LOG.debug("Waking up thread!");
                waitingThread.wakeUp();
            }
        }
    }

    /**
     * Tells this connection that it is not needed anymore. Must only be invoked by ConnectionPool!
     */
    void disconnect()
    {
        LOG.info("Destroying connection");

        this.disconnected.set(true);
        this.connectedProxies.clear();

        try
        {
            if (this.out != null)
            {
                try
                {
                    /*
                     * notify endpoint this is connected to, about shut down of this connection
                     */
                    LOG.debug("Sending shutdown notification to endpoint.");
                    Request request = this.createRequest(RemoteMethods.SHUTDOWN, new Serializable[0]);
                    LOG.debug("Shutdown notification sent.");
                    this.out.writeObject(request);
                }
                catch (IOException e)
                {
                    LOG.debug("{}: Exception during the shutdown notification", this, e);
                }
            }

            Closeables2.closeQuietly(this.in, this.mySocket);
            this.in = null;
            this.mySocket = null;
        }
        catch (Throwable t)
        {
            LOG.warn("Unexpected exception during disconnection.", t);
        }

        this.connectionClosed();
    }

    /**
     * Method to indicate that connection to remote {@link Node node} has been closed. Necessary to wake up threads possibly waiting for a response.
     */
    private void connectionClosed()
    {
        if (this.responses == null)
        {
            return;
        }

        /* synchronize on responses, as all threads accessing this proxy do so */
        synchronized (this.responses)
        {
            LOG.info("Connection broken down!");
            this.disconnected.set(true);

            /* wake up all threads */
            for (WaitingThread thread : this.waitingThreads.values())
            {
                LOG.debug("Interrupting waiting thread {}", thread);
                thread.wakeUp();
            }
        }
    }

    @Override
    public String toString()
    {
        return this.urlOflocalNode.toString() + "->" + nodeURL.toString();
    }

    /**
     * Wraps a thread, which is waiting for a response.
     * 
     * @author sven
     * 
     */
    private static class WaitingThread
    {

        private boolean hasBeenWokenUp = false;

        private Thread thread;

        private WaitingThread(Thread thread)
        {
            this.thread = thread;
        }

        /**
         * Returns <code>true</code> when the thread has been woken up by invoking {@link #wakeUp()}
         * 
         * @return
         */
        boolean hasBeenWokenUp()
        {
            return this.hasBeenWokenUp;
        }

        /**
         * Wake up the thread that is waiting for a response.
         * 
         */
        void wakeUp()
        {
            this.hasBeenWokenUp = true;
            this.thread.interrupt();
        }

        public String toString()
        {
            return this.thread.toString() + ": Waiting? " + !this.hasBeenWokenUp();
        }
    }
}
