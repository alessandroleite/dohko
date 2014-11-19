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

import java.io.Serializable;

import org.excalibur.core.workflow.definition.Activity.Transitions;

public class StateDetails implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization. 
     */
    private static final long serialVersionUID = 1100659531047254073L;
    
    private final State<Transitions> state_;
    private String                   failureReason_;
    private Throwable                failureException_;

    public StateDetails(State<Transitions> state)
    {
        this.state_ = state;
    }

    /**
     * @return the failureReason
     */
    public String getFailureReason()
    {
        return failureReason_;
    }

    /**
     * @param failureReason the failureReason to set
     */
    public void setFailureReason(String failureReason)
    {
        this.failureReason_ = failureReason;
    }

    /**
     * @return the failureException
     */
    public Throwable getFailureException()
    {
        return failureException_;
    }

    /**
     * @param failureException the failureException to set
     */
    public void setFailureException(Throwable failureException)
    {
        this.failureException_ = failureException;
    }

    /**
     * @return the state
     */
    public State<Transitions> getState()
    {
        return state_;
    }

    public void setFailure(String reason, Throwable cause)
    {
        this.setFailureReason(reason);
        this.setFailureException(cause);
    }
}
