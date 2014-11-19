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
package br.cic.unb.chord.communication.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import br.cic.unb.chord.communication.CommunicationException;
import br.cic.unb.chord.communication.Endpoint;
import br.cic.unb.chord.communication.Endpoint.EndpointState;
import br.cic.unb.chord.communication.EndpointStateListener;
import br.cic.unb.chord.communication.Entry;
import br.cic.unb.chord.communication.Node;
import br.cic.unb.chord.communication.ProtocolType;
import br.cic.unb.chord.communication.Proxy;
import br.cic.unb.chord.communication.RefsAndEntries;
import br.cic.unb.chord.communication.Request;
import br.cic.unb.chord.communication.Response;
import br.cic.unb.chord.communication.jxta.JxtaProxy;
import br.cic.unb.chord.communication.socket.SocketProxy;
import br.cic.unb.chord.data.ID;
import br.cic.unb.chord.data.URL;
import br.cic.unb.chord.io.Closeables2;
import br.cic.unb.overlay.chord.EntryInsertedEvent;

/**
 * This class handles {@link Request requests} for a single incoming connection from another node sent through a {@link Proxy} that represents the
 * local node at the remote node.
 */
@SuppressWarnings("unchecked")
public final class RequestHandler extends Thread implements EndpointStateListener
{
    /**
     * The node this RequestHandler invokes methods on.
     */
    private Node node;

    /**
     * The socket over that this RequestHandler receives requests.
     */
    private Socket connection;

    /**
     * {@link ObjectOutputStream}to write answers to.
     */
    private ObjectOutputStream out;

    /**
     * {@link ObjectInputStream}to read {@link Request requests}from.
     */
    private ObjectInputStream in;

    /**
     * Indicates if this RequestHandler is connected. Used in {@link #run()} to determine if this is still listening for requests.
     */
    protected boolean connected = true;

    /**
     * The state that the {@link Endpoint}, that started this request handler, is currently in. See constants of class
     * {@link br.cic.unb.chord.communication.Endpoint}.
     */
    private EndpointState state;

    /**
     * The {@link Endpoint endpoint}that started this handler.
     */
    private Endpoint endpoint;

    /**
     * This {@link Vector}contains {@link Thread threads}waiting for a state of the {@link Endpoint endpoint}that permits the execution of the methods
     * the threads are about to execute. This is also used as synchronization variable for these threads.
     */
    private Set<Thread> waitingThreads = new HashSet<Thread>();

    /**
     * Creates a new instance of RequestHandler
     * 
     * @param node_
     *            The {@link Node node}to delegate requested methods to.
     * @param connection_
     *            The {@link Socket}over which this receives requests.
     * @param ep
     * @throws IOException
     * 
     * @throws IOException
     *             Thrown if the establishment of a connection over the provided socket fails.
     */
    public RequestHandler(Node node_, Socket connection_, Endpoint ep) throws IOException
    {
        super("RequestHandler_" + ep.getURL());

        this.node = node_;
        this.connection = connection_;
        this.out = new ObjectOutputStream(this.connection.getOutputStream());

        try
        {
            this.in = new ObjectInputStream(this.connection.getInputStream());
        }
        catch (IOException exception)
        {
            Closeables2.closeQuietly(out);
            throw exception;
        }
        try
        {
            Request r = (Request) this.in.readObject();
            if (r.getRequestType() != RemoteMethods.CONNECT)
            {
                Response resp = new Response(Response.REQUEST_FAILED, r.getRequestType(), r.getReplyWith());
                try
                {
                    out.writeObject(resp);
                }
                catch (IOException e)
                {
                }
                finally
                {
                    Closeables2.closeQuietly(out, in);
                }

                throw new IOException("Unexpected Message received! " + r);
            }
            else
            {
                Response resp = new Response(Response.REQUEST_SUCCESSFUL, r.getRequestType(), r.getReplyWith());
                out.writeObject(resp);
                out.flush();
            }
        }
        catch (ClassNotFoundException e)
        {
            throw new IOException("Unexpected class type received! " + e.getMessage());
        }

        this.endpoint = ep;
        this.state = this.endpoint.getState();
        this.endpoint.register(this);
    }

    /**
     * Returns a reference to the endpoint this {@link RequestHandler} belongs to.
     * 
     * @return Reference to the endpoint this {@link RequestHandler} belongs to.
     */
    public Endpoint getEndpoint()
    {
        return this.endpoint;
    }

    /**
     * The task of this Thread. Listens for incoming requests send over the {@link #connection}of this thread. The thread can be stopped by invoking
     * {@link #disconnect()}.
     */
    @Override
    public void run()
    {
        while (this.connected)
        {
            Request request = null;
            try
            {
                request = (Request) this.in.readObject();
                if (request.getRequestType() == RemoteMethods.SHUTDOWN)
                {
                    this.disconnect();
                }
                else
                {
                    new InvocationThread(this, request, this.out);
                }
            }
            catch (Exception t)
            {
                this.disconnect();
            }
        }
    }

    /**
     * Method to create failure responses and send them to the requestor.
     * 
     * @param t
     * @param failure
     * @param request
     */
    public void sendFailureResponse(Throwable t, String failure, Request request)
    {
        if (!this.connected)
        {
            return;
        }
        Response failureResponse = new Response(Response.REQUEST_FAILED, request.getRequestType(), request.getReplyWith());
        failureResponse.setFailureReason(failure);
        failureResponse.setThrowable(t);
        try
        {
            synchronized (this.out)
            {
                this.out.writeObject(failureResponse);
                this.out.flush();
                this.out.reset();
            }
        }
        catch (IOException e)
        {
            if (this.connected)
            {
                this.disconnect();
            }
        }
    }

    /**
     * Invokes methods on {@link #node}.
     * 
     * @param methodType
     *            The type of the method to invoke. See {@link RemoteMethods}.
     * @param parameters
     *            The parameters to pass to the method.
     * @return The result of the invoked method. May be <code>null</code> if method is void.
     * @throws Exception
     */
    public Serializable invokeMethod(RemoteMethods methodType, Serializable[] parameters) throws Exception
    {
        this.waitForMethod(methodType);

        if (!this.connected)
        {
            throw new CommunicationException("Connection closed.");
        }

        Serializable result = null;
        switch (methodType)
        {

        case FIND_SUCCESSOR: {
            Node chordNode = this.node.findSuccessor((ID) parameters[0]);
            result = new RemoteNodeInfo(chordNode.getNodeURL(), chordNode.getNodeID());
            break;
        }
        case GET_NODE_ID: {
            result = this.node.getNodeID();
            break;
        }
        case GET_NODE_UPTIME: {
            result = this.node.getUptime();
            break;
        }
        case INSERT_ENTRY: {
            this.node.insertEntry((Entry) parameters[0]);
            break;
        }

        case INSERT_REPLICAS: {
            this.node.insertReplicas((Set<Entry>) parameters[0]);
            break;
        }
        case LEAVES_NETWORK: {
            RemoteNodeInfo nodeInfo = (RemoteNodeInfo) parameters[0];
            this.node.leavesNetwork(create(nodeInfo.getNodeURL(), this.node.getNodeURL(), nodeInfo.getNodeID()));
            break;
        }
        case NOTIFY: {
            RemoteNodeInfo nodeInfo = (RemoteNodeInfo) parameters[0];
            List<Node> l = this.node.notify(create(nodeInfo.getNodeURL(), this.node.getNodeURL(), nodeInfo.getNodeID()));

            List<RemoteNodeInfo> nodeInfos = new LinkedList<RemoteNodeInfo>();

            for (Node current : l)
            {
                nodeInfos.add(new RemoteNodeInfo(current.getNodeURL(), current.getNodeID()));
            }
            result = (Serializable) nodeInfos;
            break;
        }
        case NOTIFY_AND_COPY: {
            RemoteNodeInfo nodeInfo = (RemoteNodeInfo) parameters[0];
            RefsAndEntries refs = this.node.notifyAndCopyEntries(create(nodeInfo.getNodeURL(), this.node.getNodeURL(), nodeInfo.getNodeID()));
            List<Node> l = refs.getRefs();
            List<RemoteNodeInfo> nodeInfos = new LinkedList<RemoteNodeInfo>();

            for (Node current : l)
            {
                nodeInfos.add(new RemoteNodeInfo(current.getNodeURL(), current.getNodeID()));
            }

            RemoteRefsAndEntries rRefs = new RemoteRefsAndEntries(refs.getEntries(), nodeInfos);
            result = rRefs;
            break;
        }
        case REGISTER_ENTRY_LISTENER: {
            RemoteNodeInfo nodeInfo = (RemoteNodeInfo) parameters[0];
            Proxy nodeListener = create(nodeInfo.getNodeURL(), this.node.getNodeURL(), nodeInfo.getNodeID());
            // this.node.registerEntryListener(new RemoteNodeEntryListener(create));
            this.node.notifyOnEntryAdded(nodeListener);
            break;
        }
        case NOTIFY_ENTRY_EVENT: {
            EntryInsertedEvent event = (EntryInsertedEvent) parameters[0];
            this.node.onEntryAdded(event);
            break;
        }
        case PING: {
            result = this.node.ping();
            break;
        }
        case REMOVE_ENTRY: {
            this.node.removeEntry((Entry) parameters[0]);
            break;
        }
        case REMOVE_REPLICAS: {
            this.node.removeReplicas((ID) parameters[0], (Set<Entry>) parameters[1]);
            break;
        }
        case RETRIEVE_ENTRIES: {
            result = (Serializable) this.node.retrieveEntries((ID) parameters[0]);
            break;
        }
        default: {
            throw new Exception("Unknown method " + methodType.name());
        }
        }
        return result;
    }

    /**
     * This method is used to block threads that want to make a method call until the method invocation is permitted by the endpoint. Invocation of a
     * method depends on the state of the endpoint.
     * 
     * @param method
     *            The name of the method to invoke.
     */
    private void waitForMethod(RemoteMethods method)
    {
        synchronized (this.waitingThreads)
        {
            while ((!(this.state.equals(EndpointState.ACCEPT_ENTRIES)) && (this.connected) && ((Collections.binarySearch(
                    Endpoint.METHODS_ALLOWED_IN_ACCEPT_ENTRIES, method.getMethodName()) >= 0))))
            {
                Thread currentThread = Thread.currentThread();
                this.waitingThreads.add(currentThread);
                try
                {
                    this.waitingThreads.wait();
                }
                catch (InterruptedException e)
                {
                }
                this.waitingThreads.remove(currentThread);
            }
        }
    }

    /**
     * Disconnect this RequestHandler. Forces the socket, which this RequestHandler is bound to, to be closed and {@link #run()}to be stopped.
     */
    public void disconnect()
    {
        if (this.connected)
        {
            synchronized (this.waitingThreads)
            {
                this.connected = false;
                this.waitingThreads.notifyAll();
            }

            this.node = null;

            synchronized (this.out)
            {
                Closeables2.closeQuietly(this.connection, out, in);

                this.out = null;
                this.in = null;
                this.connection = null;
            }
            this.endpoint.unregister(this);
        }
    }

    /**
     * Test if this RequestHandler is disconnected
     * 
     * @return <code>true</code> if this is still connected to its remote end.
     */
    public boolean isConnected()
    {
        return this.connected;
    }

    public void notify(EndpointState newState)
    {
        this.state = newState;
        synchronized (this.waitingThreads)
        {
            this.waitingThreads.notifyAll();
        }
    }

    /**
     * Creates a {@link SocketProxy} ou a {@link JxtaProxy} representing the connection from <code>urlOfLocalNode</code> to <code>url</code>. The
     * connection is established when the first (remote) invocation with help of the {@link Proxy} occurs.
     * 
     * @param url
     *            The {@link URL} of the remote node.
     * @param urlOfLocalNode
     *            The {@link URL} of local node.
     * @param nodeID
     *            The {@link ID} of the remote node.
     * @return An instance of {@link Proxy}
     */
    protected Proxy create(URL url, URL urlOfLocalNode, ID nodeID) throws CommunicationException
    {
        if (ProtocolType.JXTA.equals(ProtocolType.valueOfFromProtocol(url.getProtocol())))
        {
            return JxtaProxy.create(url, urlOfLocalNode, nodeID);
        }
        else if (ProtocolType.SOCKET.equals(ProtocolType.valueOfFromProtocol(url.getProtocol())))
        {
            return SocketProxy.create(url, urlOfLocalNode, nodeID);
        }

        throw new CommunicationException("Unknown protocol!");
    }
}
