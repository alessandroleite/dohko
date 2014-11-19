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
package br.cic.unb.chord.communication;

import java.io.Serializable;

import br.cic.unb.chord.communication.net.RemoteMethods;

public final class Response extends Message
{
    private static final long serialVersionUID = -3635544762985447437L;

    /**
     * Constant holding the value that indicates that the {@link Request} that caused this response has been executed successfully.
     */
    public static final int REQUEST_SUCCESSFUL = 1;

    /**
     * Constant holding the value that indicates that the {@link Request} that caused this response failed.
     */
    public static final int REQUEST_FAILED = 0;

    /**
     * A String describing the failure if this is a failure response.
     * 
     */
    private String failureReason;

    /**
     * The result of the invocation if successful and the invocation has a result.
     * 
     */
    private Serializable result;

    /**
     * The method to invoke. Must be one of the constants defined in {@link RemoteMethods} .
     * 
     */
    private RemoteMethods methodIdentifier = RemoteMethods.CONNECT;

    /**
     * Status of the request {@link #REQUEST_FAILED} or {@link #REQUEST_SUCCESSFUL}.
     */
    private int status = REQUEST_SUCCESSFUL;

    /**
     * String defining the request that this is the response for.
     */
    private String inReplyTo;

    /**
     * If this is a failure response and the failure has been caused by any {@link Throwable} this can be set to the <code>Throwable</code>.
     */
    private Throwable throwable = null;

    /**
     * Creates a new instance of Response
     * 
     * @param status1
     * @param methodIdentifier
     * @param inReplyTo1
     */
    public Response(int status1, RemoteMethods methodIdentifier, String inReplyTo1)
    {
        super();
        this.status = status1;
        this.methodIdentifier = methodIdentifier;
        this.inReplyTo = inReplyTo1;
    }

    /**
     * @return The identifier of the method that was requested by the request, for which this is the response.
     * 
     * @see {@link RemoteMethods}.
     */
    public RemoteMethods getMethodIdentifier()
    {
        return this.methodIdentifier;
    }

    /**
     * @return Integer representing the state of this response. See {@link Response#REQUEST_FAILED}, {@link Response#REQUEST_SUCCESSFUL}.
     */
    public int getStatus()
    {
        return this.status;
    }

    /**
     * @return <code>true</code> if the request, for which this is a response, caused a failure on the remote node.
     */
    public boolean isFailureResponse()
    {
        return (this.status == REQUEST_FAILED);
    }

    /**
     * If this a failure reponse, this method returns the Throwable that caused the failure. Otherwise <code>null</code>.
     * 
     * @return If this a failure reponse, this method returns the Throwable that caused the failure. Otherwise <code>null</code>.
     */
    public Throwable getThrowable()
    {
        return this.throwable;
    }

    /**
     * @return The reason for failure of the request, for which this is the response.
     */
    public String getFailureReason()
    {
        return this.failureReason;
    }

    /**
     * @param t
     *            The throwable to set.
     */
    public void setThrowable(Throwable t)
    {
        this.throwable = t;
    }

    /**
     * @param reason
     */
    public void setFailureReason(String reason)
    {
        this.status = REQUEST_FAILED;
        this.failureReason = reason;
    }

    /**
     * @return The result of the request for which this is the response.
     */
    public Serializable getResult()
    {
        return this.result;
    }

    /**
     * @param result1
     */
    public void setResult(Serializable result1)
    {
        this.result = result1;
    }

    /**
     * @return String that identifies the request for that this is the response.
     */
    public String getInReplyTo()
    {
        return this.inReplyTo;
    }

}
