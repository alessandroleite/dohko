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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

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
import br.cic.unb.overlay.chord.EntryInsertedEvent;

/**
 * This is the implementation of {@link Proxy} for the socket protocol. This connects to the {@link SocketEndpoint endpoint} of the node it represents
 * by means of <code>Sockets</code>.
 */
public final class SocketProxy extends Proxy
{
    /**
     * The LOG for instances of this class.
     */
    private final static Logger logger = LoggerFactory.getLogger(SocketProxy.class.getName());

    /**
     * The connection pool that manages the outgoing connections for the local peer.
     */
    private static final ConnectionPool CONNECTION_POOL = ConnectionPool.getInstance();

    /**
     * The socket that provides the connection to the node that this is the Proxy for. This is transient as a proxy can be transferred over the
     * network. After transfer this socket has to be restored by reconnecting to the node.
     */
    private final AtomicReference<Connection> connection = new AtomicReference<Connection>();

    /**
     * The {@link URL}of the node that uses this proxy to connect to the node, which is represented by this proxy.
     * 
     */
    private final URL urlOfLocalNode;

    /**
     * Locally unique id of this proxy.
     */
    private volatile String proxyID;

    /**
     * Establishes a connection from <code>urlOfLocalNode</code> to <code>url</code>. The connection is represented by the returned
     * <code>SocketProxy</code>.
     * 
     * @param url
     *            The {@link URL} to connect to.
     * @param urlOfLocalNode
     *            {@link URL} of local node that establishes the connection.
     * @return <code>SocketProxy</code> representing the established connection.
     * @throws CommunicationException
     *             Thrown if establishment of connection to <code>url</code> failed.
     */
    public static SocketProxy create(URL urlOfLocalNode, URL url) throws CommunicationException
    {
        return new SocketProxy(url, urlOfLocalNode);
    }

    /**
     * Creates a <code>SocketProxy</code> representing the connection from <code>urlOfLocalNode</code> to <code>url</code>. The connection is
     * established when the first (remote) invocation with help of the <code>SocketProxy</code> occurs.
     * 
     * @param url
     *            The {@link URL} of the remote node.
     * @param urlOfLocalNode
     *            The {@link URL} of local node.
     * @param nodeID
     *            The {@link ID} of the remote node.
     * @return SocketProxy
     */
    public static SocketProxy create(URL url, URL urlOfLocalNode, ID nodeID)
    {
        return new SocketProxy(url, urlOfLocalNode, nodeID);
    }

    /**
     * Closes all outgoing connections to other peers. Allows the local peer to shutdown cleanly.
     */
    public static void shutDownAll()
    {
        
        CONNECTION_POOL.shutDownAll();
    }

    /**
     * Corresponding constructor to factory method {@link #create(URL, URL, ID)} .
     * 
     * @see #create(URL, URL, ID)
     * @param url
     * @param urlOfLocalNode
     * @param nodeID
     */
    protected SocketProxy(URL url, URL urlOfLocalNode, ID nodeID)
    {
        super(nodeID, url);

        checkNotNull(urlOfLocalNode, "urlOfLocalNode");
        this.urlOfLocalNode = urlOfLocalNode;
        CONNECTION_POOL.createProxyID(this);
    }

    /**
     * Corresponding constructor to factory method {@link #create(URL, URL)}.
     * 
     * @see #create(URL, URL)
     * @param url
     * @param localNodeUrl
     * @throws CommunicationException
     */
    private SocketProxy(URL url, URL localNodeUrl) throws CommunicationException
    {
        super(url);
        
        checkNotNull(localNodeUrl);
        
        this.urlOfLocalNode = localNodeUrl;
        CONNECTION_POOL.createProxyID(this);
        this.initializeNodeID();
        logger.info("SocketProxy for " + url + " has been created.");
    }

    /**
     * @param key
     * @return The successor of <code>key</code>.
     * @throws CommunicationException
     */
    public Node findSuccessor(ID key) throws CommunicationException
    {
        this.makeConnectionAvailable();
        logger.debug("Trying to find successor for ID {}", key);

        Request request = null;
        /* send request */
        try
        {
            logger.debug("Trying to send request " + request);
            request = this.connection.get().send(this, RemoteMethods.FIND_SUCCESSOR, new Serializable[] { key });
        }
        catch (CommunicationException ce)
        {
            logger.debug("Connection failed!");
            throw ce;
        }

        /* wait for response */
        logger.debug("Waiting for response for request {}", request);
        Response response = this.connection.get().waitForResponse(request);
        logger.debug("Response {} arrived.", response);

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
                /*
                 * This should not occur as all nodes should have the same classes!
                 */
                String message = "Could not understand the response! " + response.getResult();
                logger.error(message);
                throw new CommunicationException(message, e);
            }
        }
    }

    /**
     * @return The id of the node represented by this proxy.
     * @throws CommunicationException
     */
    private void initializeNodeID() throws CommunicationException
    {
        if (this.getNodeID() == null)
        {
            this.makeConnectionAvailable();

            logger.debug("Trying to get node ID ");

            final Request request;
            try
            {
                request = this.connection.get().send(this, RemoteMethods.GET_NODE_ID, new Serializable[0]);
            }
            catch (CommunicationException ce)
            {
                logger.debug("Connection failed!");
                throw ce;
            }

            /* wait for response */
            logger.debug("Waiting for response for request {}", request);
            Response response = this.connection.get().waitForResponse(request);
            logger.debug("Response {} arrived", response);

            if (response.isFailureResponse())
            {
                throw new CommunicationException(response.getFailureReason());
            }
            else
            {
                try
                {
                    this.setNodeID((ID) response.getResult());
                }
                catch (ClassCastException e)
                {
                    throw new CommunicationException("Could not understand result! " + response.getResult());
                }
            }
        }
    }

    /**
     * @param potentialPredecessor
     * @return List of references for the node invoking this method. See {@link Node#notify(Node)}.
     */
    @SuppressWarnings("unchecked")
    public List<Node> notify(Node potentialPredecessor) throws CommunicationException
    {
        this.makeConnectionAvailable();

        RemoteNodeInfo nodeInfoToSend = new RemoteNodeInfo(potentialPredecessor.getNodeURL(), potentialPredecessor.getNodeID());

        final Request request;

        try
        {
            request = this.connection.get().send(this, RemoteMethods.NOTIFY, new Serializable[] { nodeInfoToSend });
        }
        catch (CommunicationException e)
        {
            throw e;
        }

        /* wait for response to arrive */
        Response response = this.connection.get().waitForResponse(request);
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
    public PeerInfo ping() throws CommunicationException
    {
        this.makeConnectionAvailable();

        logger.debug("Trying to ping remote node {}" , this.getNodeURL());

        final Request request;

        /* send request */
        try
        {
            request = this.connection.get().send(this, RemoteMethods.PING, new Serializable[0]);
        }
        catch (CommunicationException ce)
        {
            logger.debug("Connection failed!");
            throw ce;
        }

        logger.debug("Waiting for response for request {}", request);
        
        Response response = this.connection.get().waitForResponse(request);
        
        logger.debug("Response {} arrived", response);

        if (response.isFailureResponse())
        {
            throw new CommunicationException(response.getFailureReason());
        }
        
        return (PeerInfo) response.getResult();
    }

    /**
     * @param entry
     * @throws CommunicationException
     */
    public void insertEntry(Entry entry) throws CommunicationException
    {
        this.makeConnectionAvailable();

        logger.debug("Trying to insert an entry {}.", entry);

        final Request request;
        /* send request */
        try
        {
            request = this.connection.get().send(this, RemoteMethods.INSERT_ENTRY, new Serializable[] { entry });
        }
        catch (CommunicationException ce)
        {
            logger.debug("Connection failed!");
            throw ce;
        }
        
        /* wait for response */
        logger.debug("Waiting for response for request {}", request);
        
        Response response = this.connection.get().waitForResponse(request);
        logger.debug("Response {} arrived", response);
        
        if (response.isFailureResponse())
        {
            throw new CommunicationException(response.getFailureReason());
        }
    }
    
    public void notifyOnEntryAdded(Node node) throws CommunicationException 
    {
        this.makeConnectionAvailable();
        
        final Request request;
        
        RemoteNodeInfo nodeToNotify = new RemoteNodeInfo(node.getNodeURL(), node.getNodeID());
        
        try
        {
            request = this.connection.get()
                    .send(this, RemoteMethods.REGISTER_ENTRY_LISTENER, new Serializable[] { nodeToNotify });
        }
        catch (CommunicationException ce)
        {
            throw ce;
        }
        
        /* wait for response */
        logger.debug("Waiting for response for request {}", request);
        Response response = this.connection.get().waitForResponse(request);
        logger.debug("Response {} arrived", response);
        
    }
    
    @Override
    public void notifyEntryListeners(EntryInsertedEvent event) throws CommunicationException
    {
        this.makeConnectionAvailable();

        final Request request;

        try
        {
            request = this.connection.get().send(this, RemoteMethods.NOTIFY_ENTRY_EVENT, new Serializable[] { event });
        }
        catch (CommunicationException ce)
        {
            throw ce;
        }

        /* wait for response */
        logger.debug("Waiting for response for request {}", request);
        Response response = this.connection.get().waitForResponse(request);
        logger.debug("Response {} arrived", response);

    }

    /**
     * @param replicas
     * @throws CommunicationException
     */
    public void insertReplicas(Set<Entry> replicas) throws CommunicationException
    {
        this.makeConnectionAvailable();

        logger.debug("Trying to insert replicas {}", replicas);

        Request request;

        try
        {
            request = this.connection.get().send(this, RemoteMethods.INSERT_REPLICAS, new Serializable[] { (Serializable) replicas });
        }
        catch (CommunicationException ce)
        {
            logger.debug("Connection failed!");
            throw ce;
        }

        logger.debug("Waiting for response for request {}", request);
        Response response = this.connection.get().waitForResponse(request);
        logger.debug("Response {} arrived.", response);

        if (response.isFailureResponse())
        {
            throw new CommunicationException(response.getFailureReason());
        }
    }

    /**
     * @param predecessor
     * @throws CommunicationException
     */
    public void leavesNetwork(Node predecessor) throws CommunicationException
    {
        this.makeConnectionAvailable();

        logger.debug("Trying to notify node that {} leaves network.", predecessor);

        RemoteNodeInfo nodeInfo = new RemoteNodeInfo(predecessor.getNodeURL(), predecessor.getNodeID());

        Request request;
        /* send request */
        try
        {
            request = this.connection.get().send(this, RemoteMethods.LEAVES_NETWORK, new Serializable[] { nodeInfo });
        }
        catch (CommunicationException ce)
        {
            logger.debug("Connection failed!");
            throw ce;
        }

        /* wait for response */
        logger.debug("Waiting for response for request {}", request);
        Response response = this.connection.get().waitForResponse(request);
        logger.debug("Response {} arrived.", response);
        
        if (response.isFailureResponse())
        {
            throw new CommunicationException(response.getFailureReason());
        }
    }

    /**
     * @param entry
     * @throws CommunicationException
     */
    public void removeEntry(Entry entry) throws CommunicationException
    {
        this.makeConnectionAvailable();

        logger.debug("Trying to remove entry {}.", entry);

        Request request;

        try
        {
            request = this.connection.get().send(this, RemoteMethods.REMOVE_ENTRY, new Serializable[] { entry });
        }
        catch (CommunicationException ce)
        {
            logger.debug("Connection failed!");
            throw ce;
        }
        
        /* wait for response */
        logger.debug("Waiting for response for request {}", request);
        Response response = this.connection.get().waitForResponse(request);
        logger.debug("Response {} arrived.", response);
        
        if (response.isFailureResponse())
        {
            throw new CommunicationException(response.getFailureReason());
        }
    }

    /**
     * @param sendingNodeID
     * @param replicas
     * @throws CommunicationException
     */
    public void removeReplicas(ID sendingNodeID, Set<Entry> replicas) throws CommunicationException
    {
        this.makeConnectionAvailable();

        logger.debug("Trying to remove replicas {}.", replicas);

        final Request request;

        /* send request */
        try
        {
            request = this.connection.get().send(this, RemoteMethods.REMOVE_REPLICAS, new Serializable[] { sendingNodeID, (Serializable) replicas });
            
            /* wait for response */
            logger.debug("Waiting for response for request {}", request);
            Response response = this.connection.get().waitForResponse(request);
            logger.debug("Response {} arrived.", response);
            
            if (response.isFailureResponse())
            {
                logger.error(response.getFailureReason());
//              throw new CommunicationException(response.getFailureReason());
            }
            
        }
        catch (CommunicationException ce)
        {
            logger.debug("Connection failed!");
//            throw ce;
        }
    }

    @SuppressWarnings("unchecked")
    public Set<Entry> retrieveEntries(ID id) throws CommunicationException
    {
        this.makeConnectionAvailable();

        logger.debug("Trying to retrieve entries for ID {}", id);

        Request request;

        /* send request */
        try
        {
            request = this.connection.get().send(this, RemoteMethods.RETRIEVE_ENTRIES, new Serializable[] { id });
        }
        catch (CommunicationException ce)
        {
            logger.debug("Connection failed!");
            throw ce;
        }
        /* wait for response */
        logger.debug("Waiting for response for request {}", request);
        Response response = this.connection.get().waitForResponse(request);
        logger.debug("Response {} arrived.", response);
        
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

    /**
     * This method has to be called at first in every method that uses the socket to connect to the node this is the proxy for. This method
     * establishes the connection if not already done. This method has to be called as this proxy can be serialized and the reference to the socket is
     * transient. So by calling this method after a transfer the connection to the node is reestablished. The same applies for {@link #LOG}and
     * {@link #responses}.
     * 
     * @throws CommunicationException
     */
    private void makeConnectionAvailable() throws CommunicationException
    {
        if (connection.compareAndSet(null, CONNECTION_POOL.getConnectionFor(this)))
        {
            logger.debug("Configured the connection for the proxy ID: {}, local node: {}", this.proxyID, this.urlOfLocalNode);
        }
    }

    /**
     * Finalization ensures that the socket is closed if this proxy is not needed anymore.
     * 
     * @throws Throwable
     */
    public void finalize() throws Throwable
    {
        if (connection.get() != null)
        {
            CONNECTION_POOL.releaseConnection(this);
        }
        
        super.finalize();
    }

    /**
     * @param potentialPredecessor
     * @return See {@link Node#notifyAndCopyEntries(Node)}.
     * @throws CommunicationException
     */
    public RefsAndEntries notifyAndCopyEntries(Node potentialPredecessor) throws CommunicationException
    {
        this.makeConnectionAvailable();

        RemoteNodeInfo nodeInfoToSend = new RemoteNodeInfo(potentialPredecessor.getNodeURL(), potentialPredecessor.getNodeID());

        Request request;
        /* send request */
        try
        {
            request = this.connection.get().send(this, RemoteMethods.NOTIFY_AND_COPY, new Serializable[] { nodeInfoToSend });
        }
        catch (CommunicationException ce)
        {
            logger.debug("Connection failed!");
            throw ce;
        }
        /* wait for response */
        logger.debug("Waiting for response for request {}", request);
        Response response = this.connection.get().waitForResponse(request);
        logger.debug("Response {} arrived.", response);
        
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

    /**
     * The string representation of this proxy. Created when {@link #toString()} is invoked for the first time.
     */
    private String stringRepresentation;

    /**
     * @return String representation of this.
     */
    @Override
    public String toString()
    {
        if (this.getNodeID() == null || this.connection.get() == null)
        {
            return "Unconnected SocketProxy from " + this.urlOfLocalNode + " to " + this.getNodeURL();
        }

        if (this.stringRepresentation == null)
        {
            StringBuilder builder = new StringBuilder();
            builder.append("SocketProxy from Node[conn.=").append(this.getProxyID());
            builder.append("] to Node[id=").append(this.getNodeID());
            builder.append(", url=").append(this.getNodeURL());
            builder.append("]");

            this.stringRepresentation = builder.toString();
        }
        return this.stringRepresentation;
    }

    /**
     * @return the urlOfLocalNode
     */
    public final URL getUrlOfLocalNode()
    {
        return urlOfLocalNode;
    }

    String getProxyID()
    {
        return proxyID;
    }

    void setProxyID(String id)
    {
        this.proxyID = id;
    }

    @Override
    public void onEntryAdded(EntryInsertedEvent event)
    {
        throw new UnsupportedOperationException();
    }
}
