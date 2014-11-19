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
package br.cic.unb.chord.util;

public class WaitingThread
{

    private boolean hasBeenWokenUp = false;

    private Thread thread;

    public WaitingThread(Thread thread)
    {
        this.thread = thread;
    }

    /**
     * Returns <code>true</code> when the thread has been woken up by invoking {@link #wakeUp()}
     * 
     * @return
     */
    public boolean hasBeenWokenUp()
    {
        return this.hasBeenWokenUp;
    }

    /**
     * Wake up the thread that is waiting for a response.
     */
    public void wakeUp()
    {
        this.hasBeenWokenUp = true;
        this.thread.interrupt();
    }

    public String toString()
    {
        return this.thread.toString() + ": Waiting? " + !this.hasBeenWokenUp();
    }
}
