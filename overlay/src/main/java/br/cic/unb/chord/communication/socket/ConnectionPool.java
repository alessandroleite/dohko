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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import br.cic.unb.chord.communication.CommunicationException;
import br.cic.unb.chord.data.ID;
import br.cic.unb.chord.data.URL;

/**
 * This class pools connections to remote peers, so that they can be reused by several SocketProxies, that represent the same remote peer locally.
 * 
 * Connections are destroyed when no SocketProxy that uses them exists.
 * 
 * @author sven
 */
final class ConnectionPool
{
    /**
     * Singleton instance of this peer. Eager instantiation, as this is required anyway.
     */
    private static final ConnectionPool instance = new ConnectionPool();

//    private static final Logger LOG = LoggerFactory.getLogger(ConnectionPool.class.getName());

    /**
     * The connections currently active.
     */
    private final ConcurrentMap<String, Connection> connections;

    /**
     * Counter to create unique ids for proxies.
     */
    private AtomicLong idCounter = new AtomicLong(-1L);

    /**
     * Private constructor to prevent instantiation by others.
     * 
     */
    private ConnectionPool()
    {
        this.connections = new ConcurrentHashMap<String, Connection>();
    }

    /**
     * Create an id for a SocketProxy. The id is immediately set on the proxy with help of {@link SocketProxy#setProxyID(String)}
     * 
     * @param p
     *            The proxy to create the id for.
     */
    void createProxyID(SocketProxy p)
    {
        p.setProxyID(ConnectionPool.getConnectionKeyOf(p) + idCounter.addAndGet(1L));
    }

    /**
     * Returns the connection that can be used by the provided SocketProxy. If no connection exists it is created. If a connection exists than the
     * existing connection is returned.
     * 
     * @param p
     *            A SocketProxy. Must not be <code>null</code>!
     * @return The connection used by the SocketProxy p.
     * @throws CommunicationException
     */
    Connection getConnectionFor(SocketProxy p) throws CommunicationException
    {
        checkNotNull(p);

        synchronized (this.connections)
        {
            String key = ConnectionPool.getConnectionKeyOf(p);
            Connection conn = this.connections.get(key);

            if (conn == null)
            {
                conn = new Connection(p);
                this.connections.put(key, conn);
            }
            else
            {
//                try
//                {
                    conn.newClient(p);
//                    testConnection(conn);
//                    conn.send(p, RemoteMethods.PING, new Serializable[0]);
//
//                }
//                catch (CommunicationException exception)
//                {
//                    logger.debug("Invalid connection for {}!", key);
//                    // conn = getConnectionFor(p);
//                    conn.disconnect();
//                    this.releaseConnection(p);
//                    conn = getConnectionFor(p);
//                }
            }
            return conn;
        }
    }

    /**
     * Informs the ConnectionPool that {@code p} does not need its connection anymore. This method is responsible to destroy connections that are no
     * longer required.
     * 
     * Should only be called by the SocketProxy itself!
     * 
     * @param p
     *            The SocketProxy that does not need its connection anymore. Must not be <code>null</code>.
     * @throws NullPointerException
     *             if the given connection is <code>null</code>.
     */
    void releaseConnection(SocketProxy p)
    {
        checkNotNull(p);

        synchronized (this.connections)
        {
            String key = ConnectionPool.getConnectionKeyOf(p);
            Connection conn = this.connections.get(key);
            if (conn != null && conn.releaseConnection(p))
            {
                this.connections.remove(key);
                conn.disconnect();
            }
        }
    }

    /**
     * Method that creates a unique key for a Connection to be stored in {@link #proxies}.
     * 
     * This is important for the methods {@link #create(URL, URL)}, {@link #create(URL, URL, ID)}, and {@link #disconnect()}, so that socket
     * communication also works when it is used within one JVM.
     * 
     * 
     * @param p
     *            SocketProxy to create a connection key for. Must not be null.
     * @return The key to store the SocketProxy
     */
    private static String getConnectionKeyOf(SocketProxy p)
    {
        checkNotNull(p);
        return p.getUrlOfLocalNode().toString() + "->" + p.getNodeURL().toString();
    }

    /**
     * Shutdown all outgoing connections. Pending requests are terminated.
     */
    void shutDownAll()
    {
        synchronized (this.connections)
        {
            List<Connection> connectionList = new LinkedList<Connection>(this.connections.values());
            for (Connection conn : connectionList)
            {
                conn.disconnect();
            }
        }
    }

    /**
     * Get the singleton instance of this ConnectionPool.
     * 
     * @return
     */
    static final ConnectionPool getInstance()
    {
        return instance;
    }
}
