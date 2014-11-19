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
package br.cic.unb.chord.communication;


import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.data.URL;
import br.cic.unb.chord.util.ReflectionUtil;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * <p>
 * This class represents an endpoint, which wraps a {@link Node}, so that other nodes can connect to the node using a protocol.
 * </p>
 * <p>
 * This is the abstract class that has to be implemented by all Endpoints. An Endpoint enables other peers to connect to a {@link Node} with help of a
 * given protocol. Each node in a network has to have exactly one endpoint.
 * </p>
 * <p>
 * For each protocol that shall be supported a separate endpoint has to be implemented. To initialize ENDPOINTS for a {@link Node} an {@link URL} has
 * to be provided to the {@link #createEndpoint(Node, URL)} endpoint factory method. This methods tries to determine the endpoint with help of the
 * protocol names defined by the {@link URL}.
 * </p>
 * An Endpoint can be in three states:
 * <ul>
 * <li><code>{@link EndpointState#STARTED}</code>,</li>
 * <li><code>{@link EndpointState#LISTENING}</code>, and</li>
 * <li><code>{@link EndpointState#ACCEPT_ENTRIES}</code>.</li>
 * </ul>
 * <p>
 * In state <code>{@link EndpointState##STARTED}</code> the endpoint has been initialized but does not listen to (possibly) incoming messages from the
 * peer in the network. An endpoint gets into this state if it is created with help of its constructor. <br/>
 * <br/>
 * In state <code>{@link EndpointState#LISTENING}</code> the endpoint accepts messages that are received from the network to update the finger table
 * or request the predecessor or successor of the node of this endpoint. The transition to this state is made by invocation of {@link #listen()}. <br/>
 * <br/>
 * In state <code>{@link EndpointState#ACCEPT_ENTRIES}</code>. This endpoint accepts messages from the network, that request storage or removal of
 * entries from the DHT. The transition to this state is made by invocation of {@link #acceptEntries()}.
 * </p>
 */
public abstract class Endpoint
{
    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Endpoint.class.getName());
    

    public enum EndpointState
    {
        NOTSTARTED(-1), STARTED(0), LISTENING(1), ACCEPT_ENTRIES(2), DISCONNECTED(3);

        private Integer code;

        private EndpointState(Integer id)
        {
            this.code = id;
        }

        public Integer getCode()
        {
            return code;
        }
    }


    /**
     * Map containing all ENDPOINTS.
     */
    protected static final ConcurrentMap<URL, Endpoint> ENDPOINTS = Maps.newConcurrentMap();

    /**
     * Array containing names of methods allowed to be invoked in the state {@link EndpointState#ACCEPT_ENTRIES}. Remember to eventually edit this
     * array if you change the methods in interface {@link Node}. The method names contained in this array must be sorted!
     */
    public static final List<String> METHODS_ALLOWED_IN_ACCEPT_ENTRIES;

    static
    {
        // String[] temp = new String[] { "insertEntry", "removeEntry", "retrieveEntries" };
        // Arrays.sort(temp);
        // List<String> list = new ArrayList<String>(Arrays.asList(temp));
        METHODS_ALLOWED_IN_ACCEPT_ENTRIES = ImmutableList.of("insertEntry", "removeEntry", "retrieveEntries");
    }

    /**
     * The current state of this endpoint.
     */
    private EndpointState state = EndpointState.NOTSTARTED;

    /**
     * The {@link URL}that can be used to connect to this endpoint.
     */
    protected final URL url;

    /**
     * The {@link Node} on which this endpoint invokes methods.
     */
    protected final Node node;

    /**
     * {@link EndpointStateListener thisNodeListeners} interested in state changes of this endpoint.
     */
    private Set<EndpointStateListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<EndpointStateListener, Boolean>());

    /**
     * 
     * @param node
     *            The {@link Node} this is the Endpoint for.
     * @param url
     *            The {@link URL} that describes the location of this endpoint.
     */
    protected Endpoint(final Node node, final URL url)
    {
        LOG.info("Endpoint for {} with url {}", node, url);
        
        this.node = node;
        this.url = url;
        this.state = EndpointState.STARTED;
    }

    /**
     * @return Returns the node.
     */
    public final Node getNode()
    {
        return this.node;
    }

    /**
     * Register a listener that is notified when the state of this endpoint changes.
     * 
     * @param listener
     *            The listener to register.
     */
    public final void register(EndpointStateListener listener)
    {
        if (listener != null)
        {
            this.listeners.add(listener);
        }
    }

    /**
     * Remove a listener that listened for state changes of this endpoint.
     * 
     * @param listener
     *            The listener instance to be removed.
     */
    public final void unregister(EndpointStateListener listener)
    {
        this.listeners.remove(listener);
    }

    /**
     * Method to notify thisNodeListeners about a change in state of this endpoint.
     * 
     * @param state
     *            The integer identifying the state to that the endpoint switched. See {@link Endpoint#ACCEPT_ENTRIES}, {@link Endpoint#DISCONNECTED},
     *            {@link Endpoint#LISTENING}, and {@link Endpoint#STARTED}.
     */
    protected void notifyListeners(EndpointState state)
    {
        synchronized (this.listeners)
        {
            LOG.debug("Number of thisNodeListeners: {}", this.listeners.size());
            
            for (EndpointStateListener listener : this.listeners)
            {
                listener.notify(state);
            }
        }
    }

    /**
     * Get the {@link URL}of this endpoint.
     * 
     * @return The {@link URL}that can be used to connect to this endpoint.
     */
    public URL getURL()
    {
        return this.url;
    }

    /**
     * @return Returns the state.
     */
    public final EndpointState getState()
    {
        return this.state;
    }

    /**
     * @param state
     *            The state to set.
     */
    protected final void setState(EndpointState state)
    {
        this.state = state;
        this.notifyListeners(state);
    }

    /**
     * Tells this endpoint that it can listen for incoming messages from other nodes.
     */
    public final void listen() throws CommunicationException
    {
        this.state = EndpointState.LISTENING;
        this.notifyListeners(this.state);
        this.openConnections();
    }

    /**
     * To implemented by sub classes. This method is called by {@link #listen()} to make it possible for other chord nodes to connect to the node on
     * that this endpoint invocates methods.
     * 
     * @throws CommunicationException
     *             Throw if a
     */
    protected abstract void openConnections() throws CommunicationException;

    /**
     * Tell this endpoint that the node is now able to receive messages that request the storage and removal of entries.
     * 
     */
    public final void acceptEntries()
    {
        this.state = EndpointState.ACCEPT_ENTRIES;
        this.notifyListeners(this.state);
        this.entriesAcceptable();
    }

    /**
     * This method has to be overwritten by subclasses. It is called from {@link #acceptEntries()}to indicate that entries can now be accepted. So
     * maybe if an endpoint queues incoming requests for storage or removal of entries this requests can be answered when endpoint changes it state to
     * <code>ACCEPT_ENTRIES</code>.
     */
    protected abstract void entriesAcceptable();

    /**
     * Tell this endpoint to disconnect and close all connections. If this method has been invoked the endpoint must be not reused!!!
     */
    public final void disconnect()
    {
        this.state = EndpointState.STARTED;
        
        LOG.info("Disconnecting.");
        this.notifyListeners(this.state);
        this.closeConnections();

        synchronized (ENDPOINTS)
        {
            ENDPOINTS.remove(this.node.getNodeURL());
        }
    }

    /**
     * This method has to be overwritten by sub classes and is invoked by {@link #disconnect()}to close all connections from the chord network.
     * 
     */
    protected abstract void closeConnections();

    /**
     * Schedule an invocation of a local method to be executed.
     * 
     * @param invocationThread
     */
    public abstract void scheduleInvocation(Runnable invocationThread);

    /**
     * Create the ENDPOINTS for the protocol given by <code>url</code>. An URL must have a known protocol. An endpoint for an {@link URL} can only be
     * create once and then be obtained with help of {@link Endpoint#getEndpoint(URL)}. An endpoint for an url must again be created if the
     * {@link Endpoint#disconnect()} has been invoked.
     * 
     * @param node
     *            The node to which this endpoint delegates incoming requests.
     * @param url
     *            The URL under which <code>node</code> will be reachable by other nodes.
     * @return The endpoint created for <code>node</code> for the protocol specified in <code>url</code>.
     * @throws RuntimeException
     *             This can occur if any error that cannot be handled by this method occurs during endpoint creation.
     */
    public static Endpoint createEndpoint(Node node, URL url)
    {
        synchronized (ENDPOINTS)
        {
            checkNotNull(url);
            checkState(!ENDPOINTS.containsKey(url), "Endpoint already exists!");

            Endpoint endpoint = null;

            String endpointClassName = System.getProperty("br.cic.unb.chord.communication.endpoint." + url.getProtocol().toLowerCase().trim()
                    + ".class");

            if (endpointClassName == null || endpointClassName.trim().isEmpty())
            {
                throw new IllegalArgumentException("Url does not contain a supported protocol (" + url.getProtocol()
                        + ")! Please check the chord.properties file");
            }

            endpoint = (Endpoint) ReflectionUtil.newInstance(endpointClassName, node, url);
            ENDPOINTS.put(url, endpoint);
            return endpoint;
        }
    }

    /**
     * Get the <code>Endpoint</code> for the given <code>url</code>.
     * 
     * @param url
     * @return The endpoint for provided <code>url</code>.
     */
    public static Endpoint getEndpoint(URL url)
    {
        synchronized (ENDPOINTS)
        {
            Endpoint ep = ENDPOINTS.get(url);
            LOG.debug("Endpoint for URL {}: {}" , url, ep);
            return ep;
        }
    }

    /**
     * Overwritten from {@link java.lang.Object}.
     * 
     * @return String representation of this endpoint.
     */
    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[Endpoint for ");
        buffer.append(this.node);
        buffer.append(" with URL ");
        buffer.append(this.url);
        return buffer.toString();
    }

}
