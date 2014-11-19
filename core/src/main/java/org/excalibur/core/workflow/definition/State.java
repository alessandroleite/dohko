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
package org.excalibur.core.workflow.definition;

import org.excalibur.core.workflow.flow.ActivityExecutionContext;
import org.excalibur.core.workflow.flow.WorkflowContext;
import org.excalibur.core.workflow.listener.WorkflowActivityStateListener;

public interface State<T>
{
    /**
     * Returns the current value of the state.
     * 
     * @return The current value of this {@link State}.
     */
    T get();

    /**
     * Sets the current value.
     * 
     * @param value
     *            The value to set.
     */
    void set(T value);
    
    /**
     * Sets the current execution context.
     * 
     * @param context The {@link WorkflowContext} to set. Might not be <code>null</code>.
     */
    void setContext(ActivityExecutionContext context);

    /**
     * Sets the state to a new value and return the old value.
     * 
     * @param value
     *            The new value to set.
     * @return The old value.
     */
    T getAndSet(T value);

    /**
     * If the current state is the expected value then set it to the given new value and return <code>true</code> otherwise return <code>false</code>.
     * 
     * @param expected
     *            The expected value.
     * @param newValue
     *            The new value to set.
     * @return <code>true</code> if the state was updated. <code>true</code> means that the state was the expected.
     */
    boolean compareAndSet(T expected, T newValue);

    /**
     * Returns <code>true</code> if the current value is equal to the given value.
     * 
     * @param state
     *            The state value to compare to.
     * @return <code>true</code> if the state is equal to the given value.
     */
    boolean is(T state);

    /**
     * Returns <code>true</code> if the current value is equal to any of the given values.
     * 
     * @param values
     *            The values to compare if the current state is equals to any of them.
     * @return <code>true</code> if the current state is equal to any of the given values.
     */
    public boolean isAny(T... values);

    /**
     * Adds the listeners to notify when the state changes.
     * 
     * @param listeners
     *            The {@link WorkflowActivityStateListener} to be notify when the state changes.
     */
    void notifyOnStateChanges(WorkflowActivityStateListener... listeners);
}
