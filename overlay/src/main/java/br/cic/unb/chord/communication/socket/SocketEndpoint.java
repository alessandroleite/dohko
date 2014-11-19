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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.communication.CommunicationException;
import br.cic.unb.chord.communication.Endpoint;
import br.cic.unb.chord.communication.Node;
import br.cic.unb.chord.communication.net.InvocationThread;
import br.cic.unb.chord.communication.net.RequestHandler;
import br.cic.unb.chord.data.URL;

/**
 * This class represents an {@link Endpoint} for communication over socket protocol. It provides a <code>ServerSocket</code> to that clients can
 * connect and starts for each incoming connection a {@link br.cic.unb.chord.communication.socket.RequestHandler} that handles
 * {@link br.cic.unb.chord.communication.Request}s for method invocations from remote nodes. These {@link br.cic.unb.chord.communication.Request}s are
 * sent by one {@link SocketProxy} representing the node, that this is the endpoint for, at another node.
 */
public final class SocketEndpoint extends Endpoint implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(SocketEndpoint.class.getName());

    /**
     * {@link Set} containing all {@link RequestHandler}s created by this endpoint.
     */
    private Set<RequestHandler> handlers = new HashSet<RequestHandler>();

    /**
     * The Socket this endpoint listens to for connections.
     */
//    private ServerSocket socketEndpoint;
    private final AtomicReference<ServerSocket> socketEndpoint = new AtomicReference<ServerSocket>();

    /**
     * The {@link java.util.concurrent.Executor} responsible for carrying out executions of methods with help of an instance of
     * {@link InvocationThread}.
     */
    private final ThreadPoolExecutor invocationExecutor = InvocationThread.createInvocationThreadPool();

//    private final AtomicReference<Thread> listenerThread = new AtomicReference<Thread>();
     

    /**
     * Creates a new <code>SocketEndpoint</code> for the given {@link Node} with {@link URL url}.
     * 
     * @param node
     *            The {@link Node} node this endpoint provides connections to.
     * @param url
     *            The {@link URL} of this endpoint.
     */
    public SocketEndpoint(final Node node, URL url)
    {
        super(node, url);
    }

    @Override
    protected void openConnections() throws CommunicationException
    {
        try
        {
            if (socketEndpoint.compareAndSet(null, new ServerSocket(this.url.getPort())))
            {
                    this.setState(EndpointState.LISTENING);
                    new Thread(this, "SocketEndpoint_" + this.url + "_Thread").start();
            }
            else
            {
                LOG.warn("Connection already opened!");
            }
        }
        catch (IOException e)
        {
            throw new CommunicationException(String.format("SocketEndpoint could not listen on port %s %s", this.url.getPort(), e.getMessage()));
        }
    }

    @Override
    protected void entriesAcceptable()
    {
        this.setState(EndpointState.ACCEPT_ENTRIES);
    }

    @Override
    protected void closeConnections()
    {
        this.setState(EndpointState.STARTED);

        // To unblock a thread blocked on ServerSocket.accept() it's necessary to close the socket from another thread.
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    if (socketEndpoint.get() != null)
                    {
                        socketEndpoint.get().close();
                    }
                }
                catch (IOException e)
                {
                }
            }
        }.start();

        this.invocationExecutor.shutdownNow();
        SocketProxy.shutDownAll();
    }

    /**
     * Run method from {@link Runnable} to accept connections from clients. This method runs until {@link #closeConnections()} is called. It creates
     * threads responsible for the handling of requests from other nodes.
     */
    @Override
    public void run()
    {
        while (this.getState().getCode() > EndpointState.STARTED.getCode())
        {
            Socket incomingConnection = null;
            try
            {
                incomingConnection = this.socketEndpoint.get().accept();
                RequestHandler handler = new RequestHandler(this.node, incomingConnection, this);
                
                this.handlers.add(handler);
                handler.start();
            }
            catch (IOException e)
            {
                if ((this.getState().getCode() > EndpointState.STARTED.getCode()))
                {
                    if (incomingConnection != null)
                    {
                        try
                        {
                            incomingConnection.close();
                        }
                        catch (IOException e1)
                        {
                        }
                        incomingConnection = null;
                    }
                }
            }
        }

        for (RequestHandler handler : this.handlers)
        {
            handler.disconnect();
        }
        this.handlers.clear();
    }

    /**
     * Schedule an invocation of a local method.
     * 
     * @param invocationThread
     */
    @Override
    public void scheduleInvocation(Runnable invocationThread)
    {
        this.invocationExecutor.execute(invocationThread);
    }
}
