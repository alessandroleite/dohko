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
package org.excalibur.core.compute.monitoring.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ProcessState implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 6212351840675644484L;

    public static class Builder
    {
        private long processId;
        private long activeThreads;
        private int kernelSchedulingPriority;
        private int tty;
        private int processor;
        private int priority;
        private int nice;
        private long fd;

        public Builder process(long processId)
        {
            this.processId = processId;
            return this;
        }

        public Builder activeThread(int threads)
        {
            this.activeThreads = threads;
            return this;
        }

        public Builder schedulingPriority(int priority)
        {
            this.kernelSchedulingPriority = priority;
            return this;
        }

        public ProcessState build()
        {
            return null;
        }
    }

    public static enum ProcState
    {
        /**
         * for sleeping (idle).
         */
        S,

        /**
         * Process running.
         */
        R,

        /**
         * for disk sleep (uninterruptible).
         */
        D,

        /**
         * for zombie (waiting for parent to read it's exit status).
         */
        Z,

        /**
         * for traced or suspended (e.g by SIGTSTP).
         */
        T,

        /**
         * for paging.
         */
        W, ;

        public static ProcState valueOf(char state)
        {
            return valueOf(String.valueOf(state));
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * The process that is monitored.
     */
    private final long processId;

    /**
     * Number of active threads.
     */
    private final long numberOfActiveThreads;

    private final int kernelSchedulingPriority;

    /**
     * The controlling terminal of the process.
     */
    private final int tty;

    /**
     * The percentage of the CPU time that the process is currently using.
     */
    private final int processor;

    private final int priority;

    /**
     * The nice value of a process, from 19 (low priority) to -20 (high priority). A high value means the process is being nice, letting others have a
     * higher relative priority. Only root can lower the value/
     */
    private final int nice;

    /**
     * 
     */
    private final long fd;

    /**
     * 
     */
    private final ProcessCpuState cpuState;

    /**
     * 
     */
    private final ProcessMemoryState memoryState;

    /**
     * 
     */
    private final ProcessTime processTime;

    /**
     * 
     */
    private final ProcState state;

    public ProcessState(long processId, long threads, int tty, int processor, int priority, int nice, long fd, int kernelSchedulingPriority,
            ProcState state, ProcessCpuState cpuState, ProcessMemoryState memoryState, ProcessTime processTime)
    {

        this.processId = processId;
        this.numberOfActiveThreads = threads;
        this.tty = tty;
        this.processor = processor;
        this.priority = priority;
        this.nice = nice;
        this.fd = fd;
        this.kernelSchedulingPriority = kernelSchedulingPriority;
        this.state = state;
        this.cpuState = cpuState;
        this.memoryState = memoryState;
        this.processTime = processTime;

    }

    /**
     * @return the processId
     */
    public long getProcessId()
    {
        return processId;
    }

    /**
     * @return the numberOfActiveThreads
     */
    public long getNumberOfActiveThreads()
    {
        return numberOfActiveThreads;
    }

    /**
     * @return the tty
     */
    public int getTty()
    {
        return tty;
    }

    /**
     * @return the processor
     */
    public int getProcessor()
    {
        return processor;
    }

    /**
     * @return the priority
     */
    public int getPriority()
    {
        return priority;
    }

    /**
     * @return the nice
     */
    public int getNice()
    {
        return nice;
    }

    /**
     * @return the fd
     */
    public long getFd()
    {
        return fd;
    }

    /**
     * @return the state
     */
    public ProcState getState()
    {
        return state;
    }

    /**
     * @return the kernelSchedulingPriority
     */
    public int getKernelSchedulingPriority()
    {
        return kernelSchedulingPriority;
    }

    /**
     * @return the cpuState
     */
    public ProcessCpuState getCpuState()
    {
        return cpuState;
    }

    /**
     * @return the memoryState
     */
    public ProcessMemoryState getMemoryState()
    {
        return memoryState;
    }

    /**
     * @return the processTime
     */
    public ProcessTime getProcessTime()
    {
        return processTime;
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
