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
package br.cic.unb.overlay.chord;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.LoggerFactory;

import br.cic.unb.chord.communication.CommunicationException;
import br.cic.unb.chord.communication.Entry;
import br.cic.unb.chord.communication.Node;
import br.cic.unb.chord.communication.Proxy;
import br.cic.unb.chord.communication.RefsAndEntries;
import br.cic.unb.chord.data.ID;
import br.cic.unb.chord.data.Peer;
import br.cic.unb.chord.data.URL;
import br.cic.unb.overlay.Key;
import br.cic.unb.overlay.Overlay;
import br.cic.unb.overlay.OverlayException;
import br.cic.unb.overlay.OverlayFuture;
import br.cic.unb.overlay.OverlayOperationCallback;
import br.cic.unb.overlay.OverlayRetrievalFuture;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;


/**
 * Implements all operations which can be invoked on the local node.
 */
public final class Chord implements Overlay, Report, EntryInsertedListener
{
    /**
     * Number of threads to allow concurrent invocations of asynchronous methods. e.g. {@link Chord#insertAsync(Key, Serializable)}.
     */
    private static final int ASYNC_CALL_THREADS = Integer.parseInt(System.getProperty("br.cic.unb.chord.service.impl.asyncthread.no"));

    /**
     * Time in seconds until the stabilize task is started for the first time.
     */
    private static final int STABILIZE_TASK_START = Integer.parseInt(System.getProperty("br.cic.unb.chord.service.stabilizetask.start"));

    /**
     * Time in seconds between two invocations of the stabilize task.
     */
    private static final int STABILIZE_TASK_INTERVAL = Integer.parseInt(System.getProperty("br.cic.unb.chord.service.stabilizetask.interval"));

    /**
     * Time in seconds until the fix finger task is started for the first time.
     */
    private static final int FIX_FINGER_TASK_START = Integer.parseInt(System.getProperty("br.cic.unb.chord.service.fixfingertask.start"));

    /**
     * Time in seconds between two invocations of the fix finger task.
     */
    private static final int FIX_FINGER_TASK_INTERVAL = Integer.parseInt(System.getProperty("br.cic.unb.chord.service.fixfingertask.interval"));

    /**
     * Time in seconds until the check predecessor task is started for the first time.
     */
    private static final int CHECK_PREDECESSOR_TASK_START = Integer.parseInt(System
            .getProperty("br.cic.unb.chord.service.checkpredecessortask.start"));

    /**
     * Time in seconds between two invocations of the check predecessor task.
     */
    private static final int CHECK_PREDECESSOR_TASK_INTERVAL = Integer.parseInt(System
            .getProperty("br.cic.unb.chord.service.checkpredecessortask.interval"));

    /**
     * Number of references in the successor list.
     */
    private static final int NUMBER_OF_SUCCESSORS = (Integer.parseInt(System.getProperty("br.cic.unb.chord.service.impl.successors")) < 1) ? 1
            : Integer.parseInt(System.getProperty("br.cic.unb.chord.service.impl.successors"));

    private static final boolean ENABLE_MASTER_SLAVE_TOPOLOGY = Boolean.valueOf(System.getProperty("br.cic.unb.chord.topology.master.slave.enable")
            .trim());

    /**
     * Object LOG.
     */
    protected org.slf4j.Logger logger;

    /**
     * Reference on that part of the node implementation which is accessible by other nodes; if <code>null</code>, this node is not connected
     */
    private final AtomicReference<NodeImpl> localNode = new AtomicReference<NodeImpl>();

    /**
     * The thisNodeListeners interested in the entries inserted into the overlay.
     */
    private List<EntryInsertedListener> listeners = new CopyOnWriteArrayList<EntryInsertedListener>();

    /**
     * Entries stored at this node, including replicas.
     */
    private Entries entries;

    /**
     * Executor service for local maintenance tasks.
     */
    private ScheduledExecutorService maintenanceTasks;

    /**
     * Executor for asynchronous requests.
     */
    private ExecutorService asyncExecutor;

    /***
     * Executor for asynchronous notifies.
     */
    private ExecutorService asyncNotifyExecutor;

    /**
     * References to remote nodes.
     */
    protected References references;

    /**
     * Reference on hash function (singleton instance).
     */
    private final HashFunction hashFunction;

    /**
     * This node's URL.
     */
    private URL localURL;

    /**
     * This node's ID.
     */
    private ID localID;

    /**
     * Indicates that the nodes has joined the overlay.
     */
    private final AtomicBoolean joined = new AtomicBoolean(false);

    /**
     * 
     */
    private final AtomicBoolean createdTasksExecutors = new AtomicBoolean(false);
    
    /**
     * The reference for the bootstrap node used to join the network. A null-value means that this node was a bootstrap.
     */
    private Peer bootStrap;

    /**
     * Creates a new instance of Chord which initially is disconnected. Constructor is hidden. Only constructor.
     */
    public Chord()
    {
        this.logger = LoggerFactory.getLogger(Chord.class.getName() + ".undefined");
        createTaskExecutors();
        this.hashFunction = HashFunction.getHashFunction();
    }

    @Override
    public final URL getURL()
    {
        return this.localURL;
    }

    @Override
    public final ID getID()
    {
        return this.localID;
    }

    @Override
    public Peer[] getNeighbors()
    {
        List<Peer> neighbors = Lists.newArrayList();
        Node successor = this.references.getSuccessor();
        Node predecessor = this.references.getPredecessor();

        if (successor != null && predecessor != null)
        {
            neighbors.add(Peer.valueOf(this.references.getPredecessor().getNodeID(), this.references.getPredecessor().getNodeURL()));
            neighbors.add(Peer.valueOf(successor.getNodeID(), successor.getNodeURL()));

            Node[] successors = this.references.getFingerTable();

            for (Node node : successors)
            {
                Peer nodeInfo = new Peer(node.getNodeID(), node.getNodeURL());
                if (!neighbors.contains(nodeInfo))
                {
                    neighbors.add(nodeInfo);
                }
            }
        }
        else
        {
            logger.debug("predecessor = {}, successor = {} ", predecessor, successor);
        }

        return neighbors.toArray(new Peer[neighbors.size()]);
    }

    @Override
    public long getUptime()
    {
        return this.localNode.get().getUptime();
    }

    @Override
    public boolean isConnected()
    {
        return this.joined.get();
    }
    
    @Override
    public Peer getBootStrap()
    {
        return this.bootStrap;
    }

    @Override
    public final void create(URL localURL) throws OverlayException
    {
        checkNotNull(localURL, "localURL");
        create(localURL, this.hashFunction.createUniqueNodeID(localURL));
    }

    @Override
    public final void create(URL localURL, ID localID) throws OverlayException
    {
        checkNotNull(localURL, "localURL");
        checkNotNull(localID, "localID");

        this.localURL = localURL;
        this.setID(localID);
        this.createRing();
    }

    @Override
    public final void join(URL localURL, URL bootstrapURL) throws OverlayException
    {
        checkNotNull(localURL, "localURL");
        checkNotNull(bootstrapURL, "bootstrapURL");
        join(localURL, this.hashFunction.createUniqueNodeID(localURL), bootstrapURL);
    }

    @Override
    public final void join(URL localURL, ID localID, URL bootstrapURL) throws OverlayException
    {
        if (joined.compareAndSet(false, true))
        {
            try
            {
                checkNotNull(localURL, "localURL");
                checkNotNull(localID, "localID");
                checkNotNull(bootstrapURL, "bootstrapURL");

                this.localURL = localURL;

                // set nodeID
                this.setID(localID);

                // establish connection
                this.createRing(bootstrapURL);
                
                // set the bootstrap peer
                this.bootStrap = Peer.valueOf(this.hashFunction.createUniqueNodeID(bootstrapURL), bootstrapURL);
            }
            catch (OverlayException exception)
            {
                joined.set(false);
                this.bootStrap = null;
                
                throw exception;
            }
        }
    }

    @Override
    public final void leave()
    {
        if (this.localNode.get() == null)
        {
            return;
        }

        if (joined.compareAndSet(true, false))
        {
            shutdownExecutors();

            try
            {
                Node successor = this.references.getSuccessor();
                if (successor != null && this.references.getPredecessor() != null)
                {
                    successor.leavesNetwork(this.references.getPredecessor());
                }
            }
            catch (Exception e)
            {
                logger.debug(e.getMessage(), e);
            }

            this.localNode.get().disconnect();
            this.localNode.set(null);
            this.bootStrap = null;
        }
    }

    private void shutdownExecutors()
    {
        this.maintenanceTasks.shutdownNow();
        this.asyncExecutor.shutdownNow();
        this.asyncNotifyExecutor.shutdownNow();

        this.createdTasksExecutors.set(false);
    }

    @Override
    public final void insert(Key key, Serializable s)
    {
        checkNotNull(key, "key");
        checkNotNull(s, "value");

        ID id = this.hashFunction.getHashKey(key);
        Entry entryToInsert = new Entry(id, s);

        boolean inserted = false;
        while (!inserted)
        {
            Node responsibleNode = this.findSuccessor(id);

            try
            {
                // Node thisNode = null;
                //
                // if (responsibleNode.getNodeID().equals(this.getID()))
                // {
                // thisNode = responsibleNode;
                // }else
                // {
                // thisNode = SocketProxy.createConnection(this.getURL(), responsibleNode.getNodeURL());
                // }
                //
                // thisNode.registerEntryListener(this.getID(), this);
                //
                // responsibleNode.notifyOnEntryAdded(thisNode);
                responsibleNode.insertEntry(entryToInsert);
                inserted = true;
            }
            catch (CommunicationException exception)
            {
                continue;
            }
        }
        this.logger.debug("A new entry was inserted!");
    }

    @Override
    public final Set<Serializable> retrieve(Key key)
    {
        checkNotNull(key, "key");
        ID id = this.hashFunction.getHashKey(key);
        Set<Entry> result = null;

        boolean retrieved = false;
        while (!retrieved)
        {
            // find successor of id
            Node responsibleNode = findSuccessor(id);

            try
            {
                result = responsibleNode.retrieveEntries(id);
                retrieved = true;
            }
            catch (CommunicationException e1)
            {
                this.logger.debug("An error occured while invoking the retrieveEntries method on the appropriate node! Retrieve operation failed!", e1);
                continue;
            }
        }

        Set<Serializable> values = new HashSet<Serializable>();

        if (result != null)
        {
            for (Entry entry : result)
            {
                values.add(entry.getValue());
            }
        }

        return values;
    }

    @Override
    public final void remove(Key key, Serializable value)
    {
        checkNotNull(key, "key");
        checkNotNull(value, "value");

        // determine ID for key
        ID id = this.hashFunction.getHashKey(key);

        Entry entryToRemove = new Entry(id, value);

        boolean removed = false;
        while (!removed)
        {

            this.logger.debug("Removing entry with id {} and value {}", id, value);

            // find successor of the key (id)
            Node responsibleNode = findSuccessor(id);
            this.logger.debug("Invoking removeEntry method on node {}", responsibleNode.getNodeID());

            try
            {
                responsibleNode.removeEntry(entryToRemove);
                removed = true;
            }
            catch (CommunicationException exception)
            {
                this.logger.debug("An error occured while invoking the removeEntry method on the appropriate node! Remove operation failed! Error message: {}",
                        exception.getMessage(), exception);
                continue;
            }
        }
        this.logger.info("The entry [{}] was removed!", key);
    }

    /**
     * Returns a human-readable string representation containing this node's node ID and URL.
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString()
    {
        return "Chord node: id = " + (this.localID == null ? "null" : this.localID.toString()) + ", url = "
                + (this.localURL == null ? "null" : this.localURL.toString() + "\n");
    }

    /**
     * Returns the {@link Node} which is responsible for the given key.
     * 
     * @param key
     *            Key for which the successor is searched for.
     * @return Responsible node.
     * @throws NullPointerException
     *             If the given {@link ID} is <code>null</code>.
     */
    final Node findSuccessor(ID key)
    {
        checkNotNull(key, "key");

        // check if the local node is the only node in the network
        Node successor = this.references.getSuccessor();

        if (successor == null)
        {
            this.logger.info("I appear to be alone in the network, so I am my own successor {}.", this.getID());
            this.joined.set(false);
            return this.localNode.get();
        }
        // check if the key to look up lies between this node and its successor
        else if (key.isInInterval(this.getID(), successor.getNodeID()) || key.equals(successor.getNodeID()))
        {
            this.joined.compareAndSet(false, true);
            this.logger.debug("The requested key lies between my own and my successor's node id; therefore return my successor.");

            try
            {
                // ping ensures that only valid references are passed to other peers
                successor.ping();
                this.logger.debug("Returning my successor {} of type {}", successor.getNodeID(), successor.getClass());

                return successor;
            }
            catch (Exception e)
            {
                // not successful, delete node from successor list and finger table, and set new successor, if available
                this.logger.warn("Successor did not respond! Removing it from all lists and retrying...");
                this.references.removeReference(successor);
                return findSuccessor(key);
            }
        }
        // ask closest preceding node found in local references for closest preceding node concerning the key to look up
        else
        {
            this.joined.compareAndSet(false, true);
            Node closestPrecedingNode = this.references.getClosestPrecedingNode(key);
            try
            {
                return closestPrecedingNode.findSuccessor(key);
            }
            catch (CommunicationException e)
            {
                this.logger.error(
                        "Communication failure while requesting successor for key {} from node {} - looking up successor for failed node {}", key,
                        closestPrecedingNode.toString(), closestPrecedingNode.toString());

                this.references.removeReference(closestPrecedingNode);
                return findSuccessor(key);
            }
        }
    }

    /* Implementation of Report interface */
    @Override
    public final String printEntries()
    {
        return this.entries.toString();
    }

    @Override
    public final String printFingerTable()
    {
        return this.references.printFingerTable();
    }

    @Override
    public final String printSuccessorList()
    {
        return this.references.printSuccessorList();
    }

    @Override
    public final String printReferences()
    {
        return this.references.toString();
    }

    @Override
    public final String printPredecessor()
    {
        Node pre = this.references.getPredecessor();
        if (pre == null)
        {
            return "Predecessor: null";
        }
        else
        {
            return "Predecessor: " + pre.toString();
        }
    }

    @Override
    public void retrieve(final Key key, final OverlayOperationCallback callback)
    {
        final Overlay chord = this;
        this.asyncExecutor.execute(new Runnable()
        {
            public void run()
            {
                Throwable t = null;
                Set<Serializable> result = null;
                try
                {
                    result = chord.retrieve(key);
                }
                catch (OverlayException e)
                {
                    t = e;
                }
                catch (Throwable th)
                {
                    t = th;
                }
                callback.retrieved(key, result, t);
            }
        });
    }

    @Override
    public Peer find(ID id)
    {
        checkNotNull(id, "id");
        checkState(isConnected(), "Node is disconnected!");

        if (id.equals(this.getID()))
        {
            return Peer.valueOf(this.getID(), this.getURL());
        }

        Node responsibleFor = this.findSuccessor(id);

        return responsibleFor != null ? Peer.valueOf(responsibleFor.getNodeID(), responsibleFor.getNodeURL()) : null;
    }

    @Override
    public void registerEntryListener(EntryInsertedListener listener)
    {
        checkNotNull(listener);
        this.listeners.add(listener);
    }

    @Override
    public void insert(final Key key, final Serializable entry, final OverlayOperationCallback callback)
    {
        final Overlay chord = this;
        this.asyncExecutor.execute(new Runnable()
        {
            public void run()
            {
                Throwable t = null;
                try
                {
                    chord.insert(key, entry);
                }
                catch (OverlayException e)
                {
                    t = e;
                }
                catch (Throwable th)
                {
                    t = th;
                }
                callback.inserted(key, entry, t);
            }
        });
    }

    @Override
    public void remove(final Key key, final Serializable entry, final OverlayOperationCallback callback)
    {
        final Overlay chord = this;
        this.asyncExecutor.execute(new Runnable()
        {
            public void run()
            {
                Throwable t = null;
                try
                {
                    chord.remove(key, entry);
                }
                catch (OverlayException e)
                {
                    t = e;
                }
                catch (Throwable th)
                {
                    t = th;
                }
                callback.removed(key, entry, t);
            }
        });
    }

    @Override
    public OverlayRetrievalFuture retrieveAsync(Key key)
    {
        return ChordRetrievalFutureImpl.create(this.asyncExecutor, this, key);
    }

    @Override
    public OverlayFuture insertAsync(Key key, Serializable entry)
    {
        return ChordInsertFuture.create(this.asyncExecutor, this, key, entry);
    }

    @Override
    public OverlayFuture removeAsync(Key key, Serializable entry)
    {
        return ChordRemoveFuture.create(this.asyncExecutor, this, key, entry);
    }

    /**
     * @return The Executor executing asynchronous request.
     */
    final Executor getAsyncExecutor()
    {
        return checkNotNull(this.asyncExecutor, "Chord.asyncExecutor is null!");
    }

    final Executor getAsyncNotifyExecutor()
    {
        return checkNotNull(this.asyncNotifyExecutor);
    }

    private final void setID(ID nodeID)
    {
        checkNotNull(nodeID, "Cannot set ID to null!");
        checkState(this.localNode.get() == null, "ID cannot be set after creating or joining a network!");

        this.localID = nodeID;
        this.logger = LoggerFactory.getLogger(this.getClass().getName() + "." + this.localID);
    }

    /**
     * Creates the tasks that must be executed periodically to maintain the Chord overlay network and schedules them with help of a
     * {@link ScheduledExecutorService}. Different tasks are executed concurrently but, not the same task. In other words, if any execution of the
     * tasks takes longer than its period, then subsequent executions may start late, but will not concurrently.
     * 
     * @see StabilizeTask
     * @see FixFingerTask
     * @see CheckPredecessorTask
     */
    private final void createTasks()
    {
        createTaskExecutors();

        // start thread which periodically stabilizes with successor
        this.maintenanceTasks.scheduleAtFixedRate(new StabilizeTask(this.localNode.get(), this.references, this.entries), Chord.STABILIZE_TASK_START,
                Chord.STABILIZE_TASK_INTERVAL, TimeUnit.SECONDS);

        // // start thread which periodically checks whether predecessor has failed
        // this.maintenanceTasks.scheduleAtFixedRate(new CheckFingerTask(this.references), Chord.FIX_FINGER_TASK_INTERVAL,
        // Chord.FIX_FINGER_TASK_START,
        // TimeUnit.SECONDS);

        // start thread which periodically attempts to fix finger table
        this.maintenanceTasks.scheduleAtFixedRate(new FixFingerTask(this.localNode.get(), this.getID(), this.references),
                Chord.FIX_FINGER_TASK_START, Chord.FIX_FINGER_TASK_INTERVAL, TimeUnit.SECONDS);

        // start thread which periodically checks whether predecessor has failed
        this.maintenanceTasks.scheduleAtFixedRate(new CheckPredecessorTask(this.references), Chord.CHECK_PREDECESSOR_TASK_START,
                Chord.CHECK_PREDECESSOR_TASK_INTERVAL, TimeUnit.SECONDS);

    }

    private synchronized void createTaskExecutors()
    {
        if (this.createdTasksExecutors.compareAndSet(false, true))
        {
            this.maintenanceTasks = new ScheduledThreadPoolExecutor(3, new ThreadFactoryBuilder().setNameFormat("maintenance-task-execution-%d")
                    .build());

            this.asyncExecutor = Executors.newFixedThreadPool(Chord.ASYNC_CALL_THREADS,
                    new ThreadFactoryBuilder().setNameFormat("asynchronous-execution-%d").build());

            this.asyncNotifyExecutor = Executors.newFixedThreadPool(Chord.ASYNC_CALL_THREADS,
                    new ThreadFactoryBuilder().setNameFormat("asynchronous-entry-notify-%d").build());
        }
    }

    /**
     * Performs all necessary tasks for joining an existing Chord ring.
     * 
     * @param bootstrapURL
     *            URL of bootstrap node. Must not be <code>null</code>.
     * @throws OverlayException
     *             If anything goes wrong during the join process.
     * @throws RuntimeException
     *             Length of successor list has not been initialized correctly.
     * @throws IllegalArgumentException
     *             <code>boostrapURL</code> is null!
     */
    private final void createRing(URL bootstrapURL) throws OverlayException
    {
        this.entries = new Entries();

        if (NUMBER_OF_SUCCESSORS >= 1)
        {
            this.references = new References(this.getID(), this.getURL(), NUMBER_OF_SUCCESSORS, this.entries);
        }
        else
        {
            throw new OverlayException("NUMBER_OF_SUCCESSORS intialized must be greather or equal 1 and was " + NUMBER_OF_SUCCESSORS);
        }

        if (!this.localNode.compareAndSet(null, new NodeImpl(this, this.getID(), this.localURL, this.references, this.entries)))
        {
            throw new OverlayException("Cannot join network; node is connected!");
        }

        Node bootstrapNode;
        try
        {
            bootstrapNode = Proxy.createConnection(this.localURL, bootstrapURL);
        }
        catch (CommunicationException e)
        {
            throw new OverlayException("An error occured when creating a proxy for outgoing connection to bootstrap node! Join operation failed!", e);
        }

        this.references.addReference(checkNotNull(bootstrapNode));

        if (ENABLE_MASTER_SLAVE_TOPOLOGY)
        {
            this.localNode.get().setMaster(bootstrapNode);
        }

        // Asking for my successor at node bootstrapNode.nodeID

        // find my successor
        Node mySuccessor;
        try
        {
            mySuccessor = bootstrapNode.findSuccessor(this.getID());
        }
        catch (CommunicationException e1)
        {
            throw new OverlayException("An error occured when trying to find the successor of this node using bootstrap node with url "
                    + bootstrapURL.toString() + "! Join operation failed!", e1);
        }

        // store reference of my successor
        this.logger.info("The node: {} has successor {}", this.localURL, mySuccessor.getNodeURL());

        // bug fix: 6558 (05/05/2014)
        if (this.references.addReference(mySuccessor))
        {
            // notify successor for the first time and copy keys from successor
            RefsAndEntries copyOfRefsAndEntries;
            try
            {
                copyOfRefsAndEntries = mySuccessor.notifyAndCopyEntries(this.localNode.get());
            }
            catch (CommunicationException e2)
            {
                throw new OverlayException("An error occured when contacting " + "the successor of this node in order to "
                        + "obtain its references and entries! Join operation failed!", e2);
            }

            List<Node> refs = copyOfRefsAndEntries.getRefs();

            boolean predecessorSet = false;
            while (!predecessorSet)
            {
                logger.debug("Size of refs: {}", refs.size());
                // there is only one other peer in the network
                if (refs.size() == 1)
                {
                    logger.info("Adding successor as predecessor as there are only two peers in the network! {}", mySuccessor);
                    this.references.addReferenceAsPredecessor(mySuccessor);

                    predecessorSet = true;
                    logger.debug("Actual predecessor: {}", this.references.getPredecessor());
                }
                else
                {
                    // we got the right predecessor and successor
                    if (this.getID().isInInterval(refs.get(0).getNodeID(), mySuccessor.getNodeID()))
                    {
                        this.references.addReferenceAsPredecessor(refs.get(0));
                        predecessorSet = true;

                        try
                        {
                            refs.get(0).notify(this.localNode.get());
                        }
                        catch (CommunicationException e)
                        {
                            throw new OverlayException("An error occured when notifying the predecessor", e);
                        }
                    }
                    else
                    {
                        this.references.addReference(refs.get(0));
                        try
                        {
                            copyOfRefsAndEntries = refs.get(0).notifyAndCopyEntries(this.localNode.get());
                            refs = copyOfRefsAndEntries.getRefs();
                        }
                        catch (CommunicationException e)
                        {
                            throw new OverlayException("An error occured when contacting the successor of this node in order to "
                                    + "obtain its references and entries! Join operation failed!", e);
                        }
                    }
                }
            }

            for (Node newReference : copyOfRefsAndEntries.getRefs())
            {
                if (newReference != null && !newReference.equals(this.localNode) && !this.references.containsReference(newReference))
                {
                    this.references.addReference(newReference);
                    this.logger.debug("Added reference on {} which responded to ping request", newReference.getNodeID());
                }
            }

            // add copied entries of successor
            this.entries.addAll(copyOfRefsAndEntries.getEntries());
        }

        // accept content requests from outside
        this.localNode.get().acceptEntries();

        // create tasks for fixing finger table, checking predecessor and stabilizing
        this.createTasks();
    }

    /**
     * Performs all necessary tasks for creating a new Chord ring.
     * 
     * @throws OverlayException
     */
    private final void createRing() throws OverlayException
    {
        this.entries = new Entries();

        if (NUMBER_OF_SUCCESSORS >= 1)
        {
            this.references = new References(this.getID(), this.getURL(), NUMBER_OF_SUCCESSORS, this.entries);
        }
        else
        {
            throw new OverlayException("NUMBER_OF_SUCCESSORS intialized with wrong value! " + NUMBER_OF_SUCCESSORS);
        }

        if (!this.localNode.compareAndSet(null, new NodeImpl(this, this.getID(), this.localURL, this.references, this.entries)))
        {
            throw new OverlayException("Cannot create network; node is already connected!");
        }

        try
        {
            // create tasks for fixing finger table, checking predecessor and stabilizing
            this.createTasks();

            // accept content requests from outside
            this.localNode.get().acceptEntries();
        }
        catch (Throwable exception)
        {
            if (this.localNode.get() != null)
            {
                this.localNode.get().disconnect();
                this.localNode.set(null);
            }

            shutdownExecutors();
            throw new OverlayException(exception.getMessage(), exception);
        }
    }

    @Override
    public void onEntryInserted(final EntryInsertedEvent event)
    {
        for (final EntryInsertedListener listener : listeners)
        {
            this.asyncNotifyExecutor.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        listener.onEntryInserted(event);
                    }
                    catch (Throwable t)
                    {
                        logger.debug("Exception raised by an entry listener: {}", t.getMessage());
                    }
                }
            });
        }
    }
}
