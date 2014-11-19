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
package org.excalibur.service.xmpp.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.excalibur.core.cloud.service.xmpp.Contacts;
import org.excalibur.service.xmpp.service.XmppService;
import org.springframework.beans.factory.annotation.Autowired;

@Path(XmppResource.XMPP_PATH)
public class XmppResource
{
    public static final String XMPP_PATH = "/xmpp";
    
    @Autowired
    private XmppService xmppService_;
    
    @Context
    private UriInfo     uriInfo;

    @Context
    private Request     request;
    
    @PUT
    @Path("{jid}/{passwd}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response createAccount(@PathParam("jid") String user, @PathParam("passwd") String passwd)
    {
        xmppService_.registerAccount(user, passwd);
        return Response.ok().build();
    }
    
    @POST
    @Path("{jid}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response disconnect(@PathParam("jid") String user)
    {
        xmppService_.disconnect(user);
        return Response.ok().build();
    }
    
    @Path("contacts")
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response contacts()
    {
        return Response.ok(new Contacts().addAll(xmppService_.getUsers())).build();
    }
}
