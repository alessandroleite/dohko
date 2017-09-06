package org.excalibur.service.compute.executor;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.Executor;
import org.excalibur.core.compute.monitoring.domain.ProcessState;
import org.excalibur.core.compute.monitoring.monitors.process.ProcessMonitor;

import com.google.common.collect.ImmutableMap;

import net.vidageek.mirror.dsl.Mirror;

public class MonitoredDefaultExecutor extends DefaultExecutor 
{
	private volatile ScheduledExecutorService monitoringScheduler_;
	private final List<ProcessState> processStats_ = new CopyOnWriteArrayList<>();
	
	public MonitoredDefaultExecutor()
	{
		super();
	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(final CommandLine command, final Map environment, final ExecuteResultHandler handler) throws ExecuteException, IOException
	{
		if (getWorkingDirectory() != null && !getWorkingDirectory().exists()) 
		{
            throw new IOException(getWorkingDirectory() + " doesn't exist.");
        }

        if (getWatchdog() != null) 
        {
            new Mirror().on(getWatchdog()).invoke().method("setProcessNotStarted").withoutArgs();
        }

        final Runnable runnable = new Runnable()
        {
            public void run()
            {
                int exitValue = Executor.INVALID_EXITVALUE;
                
                try 
                {
                    exitValue = executeInternal(command, environment, getWorkingDirectory(), getStreamHandler());
                    handler.onProcessComplete(exitValue);
                } 
                catch (final ExecuteException e) 
                {
                    handler.onProcessFailed(e);
                } 
                catch (final Exception e) 
                {
                    handler.onProcessFailed(new ExecuteException("Execution failed", exitValue, e));
                }
            }
        };
        
        new Mirror().on(this).set().field("executorThread").withValue(createThread(runnable, "Exec Default Executor"));
        getExecutorThread().start();
        monitoringScheduler_ = new ScheduledThreadPoolExecutor(1);
	}
	
	/**
     * Execute an internal process. If the executing thread is interrupted while waiting for the
     * child process to return the child process will be killed.
     *
     * @param command the command to execute
     * @param environment the execution environment
     * @param dir the working directory
     * @param streams process the streams (in, out, err) of the process
     * @return the exit code of the process
     * @throws IOException executing the process failed
     */
    private int executeInternal(final CommandLine command, @SuppressWarnings("rawtypes") final Map environment, final File dir, final ExecuteStreamHandler streams) throws IOException 
    {

        new Mirror().on(getWatchdog()).invoke().method("setExceptionCaught").withArgs();
        
        final Process process = this.launch(command, environment, dir);
        final long pid = getProcessId(process);
        final ProcessMonitor monitor = new ProcessMonitor();

        try 
        {
            streams.setProcessInputStream(process.getOutputStream());
            streams.setProcessOutputStream(process.getInputStream());
            streams.setProcessErrorStream(process.getErrorStream());
        } 
        catch (final IOException e)
        {
            process.destroy();
            throw e;
        }

        streams.start();
        monitoringScheduler_.scheduleAtFixedRate(new Runnable() 
        {
			@Override
			public void run() 
			{
				ImmutableMap<Long, ProcessState> probe = monitor.probe(pid);
				processStats_.add(probe.get(pid));
			}
		}, 1, 1, TimeUnit.SECONDS);
        

        try 
        {
            // add the process to the list of those to destroy if the VM exits
            if (this.getProcessDestroyer() != null) 
            {
              this.getProcessDestroyer().add(process);
            }

            // associate the watchdog with the newly created process
            if (getWatchdog() != null) 
            {
                getWatchdog().start(process);
            }

            int exitValue = Executor.INVALID_EXITVALUE;

            try 
            {
                exitValue = process.waitFor();
            } 
            catch (final InterruptedException e) 
            {
                process.destroy();
            }
            finally 
            {
                // see http://bugs.sun.com/view_bug.do?bug_id=6420270
                // see https://issues.apache.org/jira/browse/EXEC-46
                // Process.waitFor should clear interrupt status when throwing InterruptedException
                // but we have to do that manually
                Thread.interrupted();
            }            

            if (getWatchdog() != null) 
            {
                getWatchdog().stop();
            }

            try 
            {
                streams.stop();
            }
            catch (final IOException e) 
            {
            	new Mirror().on(getWatchdog()).invoke().method("setExceptionCaught").withArgs(e);
            }
            
            new Mirror().on(this).invoke().method("closeProcessStreams").withArgs(process);
            
            IOException exception = (IOException) new Mirror().on(this).invoke().method("getExceptionCaught").withoutArgs();

            if (exception != null)
            {
            	throw exception;
            }

            if (getWatchdog() != null) 
            {
                try 
                {
                    getWatchdog().checkException();
                } 
                catch (final IOException e) 
                {
                    throw e;
                } 
                catch (final Exception e) 
                {
                    throw new IOException(e.getMessage());
                }
            }

            if (isFailure(exitValue)) 
            {
                throw new ExecuteException("Process exited with an error: " + exitValue, exitValue);
            }

            return exitValue;
        } 
        finally 
        {
            // remove the process to the list of those to destroy if the VM exits
            if (this.getProcessDestroyer() != null) 
            {
              this.getProcessDestroyer().remove(process);
            }
            
            monitoringScheduler_.shutdownNow();
        }
    }


	private long getProcessId(final Process process) 
	{
		return (int) new Mirror().on(process).get().field("pid");
	}
	
	/**
	 * Returns a read-only view of the computing resources used by the process.
	 * @return a non-<code>null</code> {@link List} with a read-only view of the resourced usage of the process.
	 */
	public List<ProcessState> getProcessStats()
	{
		return Collections.unmodifiableList(processStats_);
	}
}
