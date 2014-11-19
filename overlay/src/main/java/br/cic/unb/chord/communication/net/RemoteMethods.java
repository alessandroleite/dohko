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

import java.io.Serializable;

/**
 * This class contains constants regarding methods that can be invoked on {@link br.cic.unb.chord.communication.Node}.
 */
public enum RemoteMethods implements Serializable
{

    /**
	 * 
	 */
    CONNECT(-1, null),

    /**
	 * 
	 */
    FIND_SUCCESSOR(0, "findSuccessor"),

    /**
	 * 
	 */
    GET_NODE_ID(1, "getNodeID"),

    /**
	 * 
	 */
    INSERT_ENTRY(2, "insertEntry"),

    /**
	 * 
	 */
    INSERT_REPLICAS(3, "insertReplicas"),

    /**
	 * 
	 */
    LEAVES_NETWORK(4, "leavesNetwork"),

    /**
	 * 
	 */
    NOTIFY(5, "notify"),

    /**
	 * 
	 */
    NOTIFY_AND_COPY(6, "notifyAndCopyEntries"),

    /**
	 * 
	 */
    PING(7, "ping"),

    /**
	 * 
	 */
    REMOVE_ENTRY(8, "removeEntry"),

    /**
	 * 
	 */
    REMOVE_REPLICAS(9, "removeReplicas"),

    /**
	 * 
	 */
    RETRIEVE_ENTRIES(10, "retrieveEntries"),

    /**
	 * 
	 */
    SHUTDOWN(11, "shutdown"),
    
    /**
     * 
     */
    REGISTER_ENTRY_LISTENER(12, "registerEntryListener"),
    
    /**
     * 
     */
    NOTIFY_ENTRY_EVENT(13, "handleEntryAdded"),
    
    /**
     * 
     */
    GET_NODE_UPTIME(14,"getUptime");
    
    ;

    /**
     * Remote method name.
     */
    private final String methodName;

    /**
     * Remote method code.
     */
    private final Integer methodCode;

    private RemoteMethods(final Integer code, final String methodName)
    {
        this.methodName = methodName;
        this.methodCode = code;

    }

    public Integer getMethodCode()
    {
        return methodCode;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public static RemoteMethods valueOf(Integer id)
    {
        for (RemoteMethods method: values())
        {
            if (method.getMethodCode().equals(id))
            {
                return method;
            }
        }
        
        throw new IllegalArgumentException("Invalid method id:" + id);
    }
}
