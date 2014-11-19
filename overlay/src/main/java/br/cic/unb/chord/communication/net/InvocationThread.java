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
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.communication.Endpoint;
import br.cic.unb.chord.communication.Request;
import br.cic.unb.chord.communication.Response;
import br.cic.unb.chord.communication.socket.SocketEndpoint;

/**
 * This <code>Thread</code> is used to make a method invocation on a node that is accessible through net protocol over its {@link Endpoint}.
 */
public class InvocationThread implements Runnable
{
    /**
     * Name of property which defines the number of threads in pool created by {@link #createInvocationThreadPool()}.
     */
    protected static final String CORE_POOL_SIZE_PROPERTY_NAME = "br.cic.unb.chord.communication.socket.invocationthread.corepoolsize";

    /**
     * Name of property which defines the maximum number of threads in pool created by {@link #createInvocationThreadPool()}.
     */
    protected static final String MAX_POOL_SIZE_PROPERTY_NAME = "br.cic.unb.chord.communication.socket.invocationthread.maxpoolsize";

    /**
     * Name of property which defines the time the threads in pool created by {@link #createInvocationThreadPool()} can stay idle before being
     * terminated.
     */
    protected static final String KEEP_ALIVE_TIME_PROPERTY_NAME = "br.cic.unb.chord.communication.socket.invocationthread.keepalivetime";

    /**
     * The number of core threads in ThreadPool created by {@link #createInvocationThreadPool()}.
     */
    private static final int CORE_POOL_SIZE = Integer.parseInt(System.getProperty(CORE_POOL_SIZE_PROPERTY_NAME));

    /**
     * The maximum number of threads in ThreadPool created by {@link #createInvocationThreadPool()}.
     */
    private static final int MAX_POOL_SIZE = Integer.parseInt(System.getProperty(MAX_POOL_SIZE_PROPERTY_NAME));

    /**
     * The time threads in ThreadPool created by {@link #createInvocationThreadPool()} can be idle before being terminated.
     */
    private static final int KEEP_ALIVE_TIME = Integer.parseInt(System.getProperty(KEEP_ALIVE_TIME_PROPERTY_NAME));

    /**
     * The LOG for instances of this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(InvocationThread.class.getName());

    /**
     * The request that has to be handled by this InvocationThread. Represents the method to be invoked.
     */
    private Request request;

    /**
     * The {@link RequestHandler} that started this thread.
     */
    private RequestHandler handler;

    /**
     * The {@link ObjectOutputStream} to write the results of the invocation to.
     */
    private ObjectOutputStream out;

    /**
     * 
     * @param handler
     *            Reference to {@link RequestHandler} that started this.
     * @param request
     *            The {@link Request} that caused this invocation to be started.
     * @param out
     *            The stream to which to write the result of the invocation.
     */
    public InvocationThread(RequestHandler handler, Request request, ObjectOutputStream out)
    {
        this.handler = handler;
        this.request = request;
        this.out = out;
        this.handler.getEndpoint().scheduleInvocation(this);

        LOG.debug("InvocationThread scheduled for request {}", request);
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("[Invocation of ");
        sb.append(this.request.getRequestType().getMethodName());
        sb.append("] Request: ");
        sb.append(this.request);
        return sb.toString();
    }

    /**
     * This <code>run</code>-method invokes the Method that is assigned to it by {@link Request} provided in its
     * {@link #InvocationThread(RequestHandler, Request, ObjectOutputStream) constructor}.
     */
    public void run()
    {
        try
        {
            Serializable result = this.handler.invokeMethod(this.request.getRequestType(), this.request.getParameters());
            /* Send result of requested method back to requestor. */
            Response response = new Response(Response.REQUEST_SUCCESSFUL, this.request.getRequestType(), this.request.getReplyWith());
            response.setResult(result);
            synchronized (this.out)
            {
                this.out.writeObject(response);
                this.out.flush();
                this.out.reset();
            }
            LOG.debug("Method invoked and result has been sent.");
        }
        catch (IOException e)
        {
            if (this.handler.isConnected())
            {
                LOG.warn("Could not send response. Disconnecting!", e);
                this.handler.disconnect();
            }
            /* else socket has been closed */
        }
        catch (Exception t)
        {
            LOG.error("Throwable occured during execution of request {}! ", this.request.getRequestType().getMethodName(), t);
            this.handler.sendFailureResponse(t, "Could not execute request! Reason unknown! Maybe this helps: " + t.getMessage(), this.request);
        }
        
        // this.request = null;
        this.handler = null;
        this.out = null;

        LOG.debug("{} finished", this);
    }

    /**
     * Creates a ThreadPool that is used by the {@link SocketEndpoint} to execute instances of this class.
     * 
     * @return A ThreadPool that is used by the {@link SocketEndpoint} to execute instances of this class.
     */
    public final static ThreadPoolExecutor createInvocationThreadPool()
    {
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                new ThreadFactory()
                {

                    private static final String name = "InvocationExecution-";

                    public Thread newThread(Runnable r)
                    {
                        Thread newThread = new Thread(r);
                        newThread.setName(name + newThread.getName());
                        return newThread;
                    }
                });
    }
}
