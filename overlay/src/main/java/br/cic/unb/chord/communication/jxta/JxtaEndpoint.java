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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

import net.jxta.document.AdvertisementFactory;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaServerSocket;
import br.cic.unb.chord.communication.CommunicationException;
import br.cic.unb.chord.communication.Endpoint;
import br.cic.unb.chord.communication.Node;
import br.cic.unb.chord.communication.net.InvocationThread;
import br.cic.unb.chord.communication.net.RequestHandler;
import br.cic.unb.chord.communication.socket.SocketProxy;
import br.cic.unb.chord.data.URL;
import br.cic.unb.overlay.chord.HashFunction;

/**
 * This class represents an {@link Endpoint} for communication over Jxta protocol. It provides a {@link JxtaServerSocket} to that clients can connect
 * and starts for each incoming connection a {@link br.cic.unb.chord.communication.jxta.RequestHandler} that handles
 * {@link br.cic.unb.chord.communication.Request}s for method invocations from remote nodes. These {@link br.cic.unb.chord.communication.Request}s are
 * sent by one {@link JxtaProxy} representing the node, that this is the endpoint for, at another node.
 */
public class JxtaEndpoint extends Endpoint implements Runnable
{

    private final static Logger logger = Logger.getLogger(JxtaEndpoint.class.getName());

    public final static String SOCKET_ID = "urn:jxta:uuid-%s";

    private transient PeerGroup netPeerGroup = null;

    private ServerSocket mySocket;

    /**
     * {@link Set} containing all {@link RequestHandler}s created by this endpoint.
     */
    private Set<RequestHandler> handlers = new HashSet<RequestHandler>();

    /**
     * The {@link java.util.concurrent.Executor} responsible for carrying out executions of methods with help of an instance of
     * {@link InvocationThread}.
     */
    private final ThreadPoolExecutor invocationExecutor = InvocationThread.createInvocationThreadPool();

    /**
     * 
     * @return
     */
    PipeAdvertisement createSocketAdvertisement()
    {
        PipeID socketID = net.jxta.id.IDFactory.newPipeID(netPeerGroup.getPeerGroupID(),
                HashFunction.getHashFunction().digest(this.getNode().getNodeURL()));

        PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        advertisement.setPipeID(socketID);
        advertisement.setType(PipeService.UnicastType);
        advertisement.setName(this.node.getNodeID().toHexString());
        return advertisement;
    }

    protected JxtaEndpoint(Node node, URL url)
    {
        super(node, url);
    }

    /**
     * Start Jxta Node group.
     * 
     * @throws IOException
     * @throws PeerGroupException
     */
    protected void start() throws IOException, PeerGroupException
    {
        netPeerGroup = br.cic.unb.chord.communication.jxta.NetworkManager.getInstance().getNetworkManager(url).getNetPeerGroup();
    }

    @Override
    protected void openConnections() throws CommunicationException
    {
        try
        {
            this.start();
            this.mySocket = new JxtaServerSocket(netPeerGroup, createSocketAdvertisement(), 10);
            // mySocket.setSoTimeout(0);
            this.setState(EndpointState.LISTENING);
            new Thread(this, "JxtaEndpoint_" + this.url + "_Thread").start();
        }
        catch (IOException exception)
        {
            throw new CommunicationException();
        }
        catch (PeerGroupException peerGroupException)
        {
            peerGroupException.printStackTrace();
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
        try
        {
            this.mySocket.close();
        }
        catch (IOException exception)
        {
            logger.info("Error in close connections of the node: " + this.getNode());
        }
        this.invocationExecutor.shutdownNow();
        SocketProxy.shutDownAll();
    }

    @Override
    public void run()
    {
        while (this.getState().getCode() > EndpointState.STARTED.getCode())
        {
            Socket incomingConnection = null;
            try
            {
                incomingConnection = this.mySocket.accept();
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
    }

    /**
     * Schedule an invocation of a local method to be executed.
     * 
     * @param invocationThread
     */
    public void scheduleInvocation(Runnable invocationThread)
    {
        this.invocationExecutor.execute(invocationThread);
    }
}
