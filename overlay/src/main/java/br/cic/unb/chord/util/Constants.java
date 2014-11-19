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

public final class Constants
{
    /**
     * Constant class.
     */
    private Constants()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Name of property which has to be set for this node address.
     */
    public final static String PROPERTY_WHERE_TO_FIND_LOCAL_NODE_ADDRESS = "node.address";

    /**
     * Name of property which has to be set for this node port.
     */
    public final static String PROPERTY_WHERE_TO_FIND_LOCAL_NODE_PORT = "node.port";
    
    public final static String PROPERTY_WITH_DEFAULT_PORT_NUMBER = "br.cic.unb.chord.communication.proxy.default.socket.port";

    /**
     * Name of property which has to be set for this node bootstrap address.
     */
    public final static String PROPERTY_WHERE_TO_BOOTSTRAP_ADDRESS = "bootstrap.address";

    /**
     * Name of property which has to be set for this node bootstrap port number.
     */
    public final static String PROPERTY_WHERE_TO_FIND_BOOTSTRAP_PORT = "bootstrap.port";

    /**
     * Name of property which has to be set for loading a specific property file.
     */
    public final static String PROPERTY_WHERE_TO_FIND_PROPERTY_FILE = "app.config.properties.file";

    /**
     * File name of property file which is loaded, if no other file is specified.
     */
    public final static String STANDARD_PROPERTY_FILE = "chord.properties";

    public final static Integer DEFAULT_PORT_NUMBER = 4242;

    public static final String OVERLAY_SYSTEM_PROPERTY_CLASS_NAME = "br.cic.unb.chord.service.impl.class.name";
    
}
