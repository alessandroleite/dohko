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
package br.cic.unb.chord.communication.jxta;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.jxta.document.AdvertisementFactory;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.communication.CommunicationException;
import br.cic.unb.chord.communication.Endpoint;
import br.cic.unb.chord.communication.Entry;
import br.cic.unb.chord.communication.Node;
import br.cic.unb.chord.communication.Proxy;
import br.cic.unb.chord.communication.RefsAndEntries;
import br.cic.unb.chord.communication.Request;
import br.cic.unb.chord.communication.Response;
import br.cic.unb.chord.communication.net.RemoteMethods;
import br.cic.unb.chord.communication.net.RemoteNodeInfo;
import br.cic.unb.chord.communication.net.RemoteRefsAndEntries;
import br.cic.unb.chord.data.ID;
import br.cic.unb.chord.data.PeerInfo;
import br.cic.unb.chord.data.URL;
import br.cic.unb.chord.util.WaitingThread;
import br.cic.unb.overlay.chord.EntryInsertedEvent;
import br.cic.unb.overlay.chord.HashFunction;

public class JxtaProxy extends Proxy implements Runnable
{

    private final static Logger logger = LoggerFactory.getLogger(JxtaProxy.class.getName());

    /**
     * Map of existing proxies. Key: {@link String}, Value: {@link JxtaProxy}.
     */
    private static Map<String, JxtaProxy> proxies = new HashMap<String, JxtaProxy>();

    /**
     * The {@link URL}of the node that uses this proxy to connect to the node, which is represented by this proxy.
     * 
     */
    private URL urlOfLocalNode = null;

    /**
     * Counter for requests that have been made by this proxy. Also required to create unique identifiers for {@link Request requests}.
     */
    private long requestCounter = -1;

    /**
     * The socket that provides the connection to the node that this is the Proxy for. This is transient as a proxy can be transferred over the
     * network. After transfer this socket has to be restored by reconnecting to the node.
     */
    private transient Socket socket;

    /**
     * The {@link ObjectOutputStream}this Proxy writes objects to. This is transient as a proxy can be transferred over the network. After transfer
     * this stream has to be restored.
     */
    private transient ObjectOutputStream out;

    /**
     * The {@link ObjectInputStream}this Proxy reads objects from. This is transient as a proxy can be transferred over the network. After transfer
     * this stream has to be restored.
     */
    private transient ObjectInputStream in;

    /**
     * The {@link ObjectInputStream} this Proxy reads objects from. This is transient as a proxy can be transferred over the network. After transfer
     * this stream has to be restored.
     */
    private transient Map<String, Response> responses;

    /**
     * {@link Map} where threads are put in that are waiting for a repsonse. Key: identifier of the request (same as for the response). Value: The
     * Thread itself.
     */
    private transient Map<String, WaitingThread> waitingThreads;

    /**
     * This indicates that an exception occurred while waiting for responses and that the connection to the {@link Node node}, that this is the proxy
     * for, could not be reestablished.
     */
    private volatile boolean disconnected = false;

    private transient NetworkManager manager = null;

    private transient PeerGroup netPeerGroup = null;

    private transient PipeAdvertisement pipeAdv;

    private transient boolean waitForRendezvous = false;

    protected void startJxta() throws IOException, PeerGroupException
    {
        // ID nodeID = HashFunction.getHashFunction().createID(
        // urlOfLocalNode.toString().getBytes());

        netPeerGroup = br.cic.unb.chord.communication.jxta.NetworkManager.getInstance().getNetworkManager(urlOfLocalNode).getNetPeerGroup();
        pipeAdv = createSocketAdvertisement();
        if (waitForRendezvous)
        {
            manager.waitForRendezvousConnection(0);
        }
    }

    /**
     * @return
     */
    PipeAdvertisement createSocketAdvertisement()
    {
        PipeID socketID = net.jxta.id.IDFactory.newPipeID(netPeerGroup.getPeerGroupID(),
                HashFunction.getHashFunction().digest(this.getNodeURL()));

        PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        advertisement.setPipeID(socketID);
        advertisement.setType(PipeService.UnicastType);
        advertisement.setName(HashFunction.getHashFunction().createID(this.getNodeURL().toString().getBytes()).toHexString());
        return advertisement;
    }

    protected JxtaProxy(final URL url, URL urlOfLocalNode) throws CommunicationException
    {
        super(url);
        checkNotNull(urlOfLocalNode);

        this.urlOfLocalNode = urlOfLocalNode;

        try
        {
            this.startJxta();
        }
        catch (Exception exception)
        {
            throw new CommunicationException(exception);
        }
        this.initializeNodeID();
    }

    /**
     * Corresponding constructor to factory method {@link #create(URL, URL, ID)} .
     * 
     * @see #create(URL, URL, ID)
     * @param url
     * @param urlOfLocalNode
     * @param nodeID
     */
    protected JxtaProxy(URL url, URL urlOfLocalNode, ID nodeID) throws CommunicationException
    {
        super(nodeID, url);
        checkNotNull(urlOfLocalNode);

        this.urlOfLocalNode = urlOfLocalNode;
        try
        {
            this.startJxta();
        }
        catch (Exception exception)
        {
            throw new CommunicationException(exception);
        }

    }

//    @Override
//    public void disconnect()
//    {
//        synchronized (proxies)
//        {
//            String proxyKey = JxtaProxy.createProxyKey(this.urlOfLocalNode, this.nodeURL);
//            proxies.remove(proxyKey);
//        }
//        this.disconnected = true;
//        try
//        {
//            if (this.out != null)
//            {
//                try
//                {
//                    Request request = this.createRequest(RemoteMethods.SHUTDOWN, new Serializable[0]);
//                    this.out.writeObject(request);
//                    this.out.flush();
//                    this.out.close();
//                    this.out = null;
//                }
//                catch (IOException e)
//                {
//                    LOG.debug(this + ": Exception during closing of output stream " + this.out, e);
//                }
//            }
//            if (this.in != null)
//            {
//                try
//                {
//                    this.in.close();
//                    this.in = null;
//                }
//                catch (IOException e)
//                {
//                    LOG.debug("Exception during closing of input stream" + this.in);
//                }
//            }
//            if (this.socket != null)
//            {
//                try
//                {
//                    this.socket.close();
//                }
//                catch (IOException e)
//                {
//                    LOG.debug("Exception during closing of socket " + this.socket);
//                }
//                this.socket = null;
//            }
//        }
//        catch (Throwable t)
//        {
//            LOG.warn("Unexpected exception during disconnection of JxtaProxy", t);
//        }
//        this.connectionBrokenDown();
//    }

    @Override
    public Node findSuccessor(ID key) throws CommunicationException
    {
        this.makeSocketAvailable();
        Request request = this.createRequest(RemoteMethods.FIND_SUCCESSOR, new Serializable[] { key });
        try
        {
            this.send(request);
        }
        catch (CommunicationException ce)
        {
            throw ce;
        }
        Response response = this.waitForResponse(request);
        if (response.isFailureResponse())
        {
            throw new CommunicationException(response.getFailureReason());
        }
        else
        {
            try
            {
                RemoteNodeInfo nodeInfo = (RemoteNodeInfo) response.getResult();
                if (nodeInfo.getNodeURL().equals(this.urlOfLocalNode))
                {
                    return Endpoint.getEndpoint(this.urlOfLocalNode).getNode();
                }
                else
                {
                    return create(nodeInfo.getNodeURL(), this.urlOfLocalNode, nodeInfo.getNodeID());
                }
            }
            catch (ClassCastException e)
            {
                String message = "Could not understand result! " + response.getResult();
                throw new CommunicationException(message, e);
            }
        }
    }

    @Override
    public void insertEntry(Entry entryToInsert) throws CommunicationException
    {
        this.makeSocketAvailable();

        Request request = this.createRequest(RemoteMethods.INSERT_ENTRY, new Serializable[] { entryToInsert });
        try
        {
            this.send(request);
        }
        catch (CommunicationException ce)
        {
            throw ce;
        }
        Response response = this.waitForResponse(request);
        if (response.isFailureResponse())
        {
            throw new CommunicationException(response.getFailureReason());
        }
    }

    @Override
    public void insertReplicas(Set<Entry> replicas) throws CommunicationException
    {
        this.makeSocketAvailable();

        Request request = this.createRequest(RemoteMethods.INSERT_REPLICAS, new Serializable[] { (Serializable) replicas });
        try
        {
            this.send(request);
        }
        catch (CommunicationException ce)
        {
            throw ce;
        }
        Response response = this.waitForResponse(request);
        if (response.isFailureResponse())
        {
            throw new CommunicationException(response.getFailureReason());
        }
    }

    @Override
    public void leavesNetwork(Node predecessor) throws CommunicationException
    {
        this.makeSocketAvailable();
        RemoteNodeInfo nodeInfo = new RemoteNodeInfo(predecessor.getNodeURL(), predecessor.getNodeID());
        Request request = this.createRequest(RemoteMethods.LEAVES_NETWORK, new Serializable[] { nodeInfo });
        try
        {
            this.send(request);
        }
        catch (CommunicationException ce)
        {
            throw ce;
        }

        Response response = this.waitForResponse(request);
        if (response.isFailureResponse())
        {
            throw new CommunicationException(response.getFailureReason());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Node> notify(Node potentialPredecessor) throws CommunicationException
    {
        this.makeSocketAvailable();

        RemoteNodeInfo nodeInfoToSend = new RemoteNodeInfo(potentialPredecessor.getNodeURL(), potentialPredecessor.getNodeID());

        Request request = this.createRequest(RemoteMethods.NOTIFY, new Serializable[] { nodeInfoToSend });

        try
        {
            this.send(request);
        }
        catch (CommunicationException e)
        {
            throw e;
        }

        Response response = this.waitForResponse(request);
        if (response.isFailureResponse())
        {
            throw new CommunicationException(response.getFailureReason());
        }
        else
        {
            try
            {
                List<RemoteNodeInfo> references = (List<RemoteNodeInfo>) response.getResult();
                List<Node> nodes = new LinkedList<Node>();
                for (RemoteNodeInfo nodeInfo : references)
                {
                    if (nodeInfo.getNodeURL().equals(this.urlOfLocalNode))
                    {
                        nodes.add(Endpoint.getEndpoint(this.urlOfLocalNode).getNode());
                    }
                    else
                    {
                        nodes.add(create(nodeInfo.getNodeURL(), this.urlOfLocalNode, nodeInfo.getNodeID()));
                    }
                }
                return nodes;
            }
            catch (ClassCastException cce)
            {
                throw new CommunicationException("Could not understand result! " + response.getResult(), cce);
            }
        }

    }

    @Override
    public RefsAndEntries notifyAndCopyEntries(Node potentialPredecessor) throws CommunicationException
    {
        this.makeSocketAvailable();

        RemoteNodeInfo nodeInfoToSend = new RemoteNodeInfo(potentialPredecessor.getNodeURL(), potentialPredecessor.getNodeID());

        Request request = this.createRequest(RemoteMethods.NOTIFY_AND_COPY, new Serializable[] { nodeInfoToSend });
        try
        {
            this.send(request);
        }
        catch (CommunicationException ce)
        {
            throw ce;
        }
        Response response = this.waitForResponse(request);
        if (response.isFailureResponse())
        {
            throw new CommunicationException(response.getFailureReason(), response.getThrowable());
        }
        else
        {
            try
            {
                RemoteRefsAndEntries result = (RemoteRefsAndEntries) response.getResult();
                List<Node> newReferences = new LinkedList<Node>();
                List<RemoteNodeInfo> references = result.getNodeInfos();
                for (RemoteNodeInfo nodeInfo : references)
                {
                    if (nodeInfo.getNodeURL().equals(this.urlOfLocalNode))
                    {
                        newReferences.add(Endpoint.getEndpoint(this.urlOfLocalNode).getNode());
                    }
                    else
                    {
                        newReferences.add(create(nodeInfo.getNodeURL(), this.urlOfLocalNode, nodeInfo.getNodeID()));
                    }
                }
                return new RefsAndEntries(newReferences, result.getEntries());
            }
            catch (ClassCastException cce)
            {
                throw new CommunicationException("Could not understand result! " + response.getResult());
            }
        }
    }

    @Override
    public PeerInfo ping() throws CommunicationException
    {
        this.makeSocketAvailable();
        Request request = this.createRequest(RemoteMethods.PING, new Serializable[0]);
        try
        {
            this.send(request);
        }
        catch (CommunicationException ce)
        {
            throw ce;
        }

        Response response = this.waitForResponse(request);
        
        if (response.isFailureResponse())
        {
            throw new CommunicationException(response.getFailureReason());
        }
        
        return (PeerInfo) response.getResult();
        
    }

//    @Override
//    public void receiveEvent(TaskEvent event) throws CommunicationException
//    {
//        this.makeSocketAvailable();
//
//        Map<ID, URL> m = event.getTask().getAllocators();
//        m.put(event.getTask().getOwnerID(), event.getTask().getOwner());
//
//        for (ID nextNodeID : m.keySet())
//        {
//
//            if (event.getSource().equals(m.get(nextNodeID)))
//                continue;
//
//            RemoteNodeInfo nodeInfoToSend = new RemoteNodeInfo(m.get(nextNodeID), nextNodeID);
//
//            Request request = this.createRequest(RemoteMethods.RAISE_TASK_EVENT, new Serializable[] { nodeInfoToSend, event });
//
//            try
//            {
//                this.send(request);
//            }
//            catch (CommunicationException exception)
//            {
//                LOG.error("Error in notify node " + nextNodeID.toHexString() + " about task event " + event.getType());
//            }
//
//            Response response = this.waitForResponse(request);
//            if (response.isFailureResponse())
//            {
//                LOG.debug("Erro in notify method about task event. " + response.getFailureReason());
//            }
//        }
//
//    }

    @Override
    public void removeEntry(Entry entryToRemove) throws CommunicationException
    {
        this.makeSocketAvailable();
        Request request = this.createRequest(RemoteMethods.REMOVE_ENTRY, new Serializable[] { entryToRemove });
        try
        {
            this.send(request);
        }
        catch (CommunicationException ce)
        {
            throw ce;
        }
        Response response = this.waitForResponse(request);
        if (response.isFailureResponse())
        {
            throw new CommunicationException(response.getFailureReason());
        }
    }

    @Override
    public void removeReplicas(ID sendingNode, Set<Entry> replicasToRemove) throws CommunicationException
    {
        this.makeSocketAvailable();
        Request request = this.createRequest(RemoteMethods.REMOVE_REPLICAS, new Serializable[] { sendingNode, (Serializable) replicasToRemove });
        try
        {
            this.send(request);
        }
        catch (CommunicationException ce)
        {
            throw ce;
        }
        Response response = this.waitForResponse(request);
        if (response.isFailureResponse())
        {
            throw new CommunicationException(response.getFailureReason());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Entry> retrieveEntries(ID id) throws CommunicationException
    {
        this.makeSocketAvailable();

        Request request = this.createRequest(RemoteMethods.RETRIEVE_ENTRIES, new Serializable[] { id });
        try
        {
            this.send(request);
        }
        catch (CommunicationException ce)
        {
            throw ce;
        }

        Response response = this.waitForResponse(request);
        if (response.isFailureResponse())
        {
            throw new CommunicationException(response.getFailureReason(), response.getThrowable());
        }
        else
        {
            try
            {
                Set<Entry> result = (Set<Entry>) response.getResult();
                return result;
            }
            catch (ClassCastException cce)
            {
                throw new CommunicationException("Could not understand result! " + response.getResult());
            }
        }
    }

    @Override
    public void run()
    {
        while (!this.disconnected)
        {
            try
            {
                Response response = (Response) this.in.readObject();
                this.responseReceived(response);
            }
            catch (ClassNotFoundException cnfe)
            {
                System.err.println("ClassNotFoundException occured during deserialization " + "of response. There is something seriously wrong "
                        + " here! " + cnfe.toString());
            }
            catch (IOException e)
            {
                this.connectionBrokenDown();
            }
        }
    }

    /**
     * This method is called by {@link #run()}when it receives a {@link Response}. The {@link Thread thread}waiting for response is woken up and the
     * response is put into {@link Map responses}.
     * 
     * @param response
     */
    private void responseReceived(Response response)
    {
        synchronized (this.responses)
        {
            WaitingThread waitingThread = this.waitingThreads.get(response.getInReplyTo());
            this.responses.put(response.getInReplyTo(), response);
            if (waitingThread != null)
            {
                waitingThread.wakeUp();
            }
        }
    }

    /**
     * Method to indicate that connection to remote {@link Node node} is broken down.
     */
    private void connectionBrokenDown()
    {
        if (this.responses == null)
        {
            return;
        }
        synchronized (this.responses)
        {
            this.disconnected = true;
            for (WaitingThread thread : this.waitingThreads.values())
            {
                thread.wakeUp();
            }
        }
    }

    /**
     * Establishes a connection from <code>urlOfLocalNode</code> to <code>url</code>. The connection is represented by the returned {@link JxtaProxy}
     * 
     * @param url
     *            The {@link URL} to connect to.
     * @param urlOfLocalNode
     *            {@link URL} of local node that establishes the connection.
     * @return <code>JxtaProxy</code> representing the established connection.
     * @throws CommunicationException
     *             Thrown if establishment of connection to <code>url</code> failed.
     */
    public static Node create(URL urlOfLocalNode, URL url) throws CommunicationException
    {
        synchronized (proxies)
        {
            final String proxyKey = JxtaProxy.createProxyKey(urlOfLocalNode, url);
            if (proxies.containsKey(proxyKey))
            {
                return proxies.get(proxyKey);
            }
            else
            {
                JxtaProxy newProxy = new JxtaProxy(url, urlOfLocalNode);
                proxies.put(proxyKey, newProxy);
                return newProxy;
            }
        }
    }

    /**
     * Creates a <code>JxtaProxy</code> representing the connection from <code>urlOfLocalNode</code> to <code>url</code>. The connection is
     * established when the first (remote) invocation with help of the <code>JxtaProxy</code> occurs.
     * 
     * @param url
     *            The {@link URL} of the remote node.
     * @param urlOfLocalNode
     *            The {@link URL} of local node.
     * @param nodeID
     *            The {@link ID} of the remote node.
     * @return JxtaProxy
     */
    public static JxtaProxy create(URL url, URL urlOfLocalNode, ID nodeID) throws CommunicationException
    {
        synchronized (proxies)
        {
            String proxyKey = JxtaProxy.createProxyKey(urlOfLocalNode, url);
            logger.debug("Known proxies " + JxtaProxy.proxies.keySet());
            if (proxies.containsKey(proxyKey))
            {
                logger.debug("Returning existing proxy for " + url);
                return proxies.get(proxyKey);
            }
            else
            {
                logger.debug("Creating new proxy for " + url);
                JxtaProxy proxy = new JxtaProxy(url, urlOfLocalNode, nodeID);
                proxies.put(proxyKey, proxy);
                return proxy;
            }
        }
    }

    /**
     * Method that creates a unique key for a {@link JxtaProxy} to be stored in {@link #proxies}.
     * 
     * This is important for the methods {@link #create(URL, URL)}, {@link #create(URL, URL, ID)}, and {@link #disconnect()}, so that socket
     * communication also works when it is used within one JVM.
     * 
     * @param localURL
     * @param remoteURL
     * @return The key to store the {@link JxtaProxy}
     */
    private static String createProxyKey(URL localURL, URL remoteURL)
    {
        return localURL.toString() + "->" + remoteURL.toString();
    }

    /**
     * @return The id of the node represented by this proxy.
     * @throws CommunicationException
     */
    private void initializeNodeID() throws CommunicationException
    {
        if (this.getNodeID() == null)
        {
            this.makeSocketAvailable();

            Request request = this.createRequest(RemoteMethods.GET_NODE_ID, new Serializable[0]);
            try
            {
                this.send(request);
            }
            catch (CommunicationException ce)
            {
                throw ce;
            }
            Response response = this.waitForResponse(request);
            if (response.isFailureResponse())
            {
                throw new CommunicationException(response.getFailureReason());
            }
            else
            {
//                try
//                {
//                    this.nodeID = (ID) response.getResult();
//                }
//                catch (ClassCastException e)
//                {
//                    throw new CommunicationException("Could not understand result! " + response.getResult());
//                }
            }
        }
    }

    /**
     * Called in a method that is delegated to the {@link Node node}, that this is the proxy for. This method blocks the thread that calls the
     * particular method until a {@link Response response} is received.
     * 
     * @param request
     * @return The {@link Response} for <code>request</code>.
     * @throws CommunicationException
     */
    private Response waitForResponse(Request request) throws CommunicationException
    {

        String responseIdentifier = request.getReplyWith();
        Response response = null;

        synchronized (this.responses)
        {
            if (this.disconnected)
            {
                throw new CommunicationException("Connection to remote host " + " is broken down. ");
            }

            response = this.responses.remove(responseIdentifier);
            if (response != null)
            {
                return response;
            }

            WaitingThread wt = new WaitingThread(Thread.currentThread());
            this.waitingThreads.put(responseIdentifier, wt);
            while (!wt.hasBeenWokenUp())
            {
                try
                {
                    this.responses.wait();
                }
                catch (InterruptedException e)
                {
                }
            }

            this.waitingThreads.remove(responseIdentifier);
            response = this.responses.remove(responseIdentifier);
            if (response == null)
            {
                if (this.disconnected)
                {
                    throw new CommunicationException("Connection to remote host " + " is broken down. ");
                }
                else
                {
                    throw new CommunicationException("Did not receive a response!");
                }
            }
        }
        return response;
    }

    /**
     * Creates a request for the method identified by <code>methodIdentifier</code> with the parameters <code>parameters</code> . Sets also field
     * {@link Request#getReplyWith()}of created {@link Request request}.
     * 
     * @param methodIdentifier
     *            The identifier of the method to request.
     * @param parameters
     *            The parameters for the request.
     * @return The {@link Request request}created.
     */
    private Request createRequest(RemoteMethods methodIdentifier, Serializable[] parameters)
    {

        String responseIdentifier = this.createIdentifier(methodIdentifier);
        Request request = new Request(methodIdentifier, responseIdentifier);
        request.setParameters(parameters);
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
    private synchronized String createIdentifier(RemoteMethods methodIdentifier)
    {
        StringBuilder uid = new StringBuilder();
        uid.append(System.currentTimeMillis());
        uid.append("-");
        uid.append(this.requestCounter++);
        uid.append("-");
        uid.append(methodIdentifier);
        return uid.toString();
    }

    /**
     * Send request over the {@link JxtaSocket}. This method is synchronized to ensure that no other thread concurrently accesses the
     * {@link ObjectOutputStream output stream}<code>out</code> while sending {@link Request request}.
     * 
     * @param request
     *            Request to be sent
     * @throws CommunicationException
     */
    private synchronized void send(Request request) throws CommunicationException
    {
        try
        {
            this.out.writeObject(request);
            this.out.flush();
            this.out.reset();
        }
        catch (IOException e)
        {
            throw new CommunicationException("Could not connect to node " + this.getNodeURL(), e);
        }
    }

    /**
     * This method has to be called at first in every method that uses the socket to connect to the node this is the proxy for. This method
     * establishes the connection if not already done. This method has to be called as this proxy can be serialized and the reference to the socket is
     * transient. So by calling this method after a transfer the connection to the node is reestablished. The same applies for {@link #LOG}and
     * {@link #responses}.
     * 
     * @throws CommunicationException
     */
    private void makeSocketAvailable() throws CommunicationException
    {
        if (this.disconnected)
        {
            throw new CommunicationException("Connection from " + this.urlOfLocalNode + " to remote host " + this.getNodeURL() + " is broken down. ");
        }

        if (this.responses == null)
        {
            this.responses = new HashMap<String, Response>();
        }
        if (this.waitingThreads == null)
        {
            this.waitingThreads = new HashMap<String, WaitingThread>();
        }
        if (this.socket == null)
        {
            try
            {
                this.socket = new JxtaSocket(netPeerGroup, pipeAdv);
                // this.socket.setSoTimeout(5000);
                this.out = new ObjectOutputStream(this.socket.getOutputStream());
                out.writeObject(new Request(RemoteMethods.CONNECT, "Initial Connection"));
                out.flush();
                this.in = new ObjectInputStream(this.socket.getInputStream());
                try
                {
                    Response resp = null;
                    boolean timedOut = false;
                    try
                    {
                        resp = (Response) in.readObject();
                    }
                    catch (SocketTimeoutException e)
                    {
                        timedOut = true;
                    }
                    this.socket.setSoTimeout(0);
                    if (timedOut)
                    {
                        throw new CommunicationException("Connection to remote host timed out!");
                    }
                    if (resp != null && resp.getStatus() == Response.REQUEST_SUCCESSFUL)
                    {
                        Thread t = new Thread(this, "JxtaProxy_Thread_" + this.getNodeURL());
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
                throw new CommunicationException("Unknown host: " + this.getNodeURL().getHost());
            }
            catch (IOException ioe)
            {
                throw new CommunicationException("Could not set up IO channel " + "to host " + this.getNodeURL().getHost(), ioe);
            }
        }
    }

    @Override
    public void notifyEntryListeners(EntryInsertedEvent event) throws CommunicationException
    {
    }

    @Override
    public void notifyOnEntryAdded(Node listenerNode) throws CommunicationException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onEntryAdded(EntryInsertedEvent event)
    {
        // TODO Auto-generated method stub
        
    }
}
