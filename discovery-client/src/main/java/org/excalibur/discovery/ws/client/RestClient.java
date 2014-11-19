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
package org.excalibur.discovery.ws.client;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

import org.excalibur.discovery.ws.ext.ObjectMapperProvider;
import org.glassfish.jersey.jackson.JacksonFeature;

public abstract class RestClient
{
    private final String  uri_;
    private final Integer port_;
    
    public RestClient(String uri, Integer port)
    {
        checkArgument(!isNullOrEmpty(uri));
        checkArgument(port != null && port > 0);
        
        this.uri_ = uri;
        this.port_ = port;
    }
    
    protected WebTarget target()
    {
        Client client = ClientBuilder.newClient().register(ObjectMapperProvider.class).register(JacksonFeature.class);
        return client.target(String.format("http://%s:%s/discovery", uri_, port_));
    }
    
    protected Invocation.Builder request(String ... paths)
    {
        WebTarget target = target();
        
        for (String path: paths)
        {
            target.path(path);    
        }
        
        return target.request();
    }
}
