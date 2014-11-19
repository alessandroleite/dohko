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

import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;

import org.excalibur.discovery.domain.ProviderDetails;
import org.excalibur.jackson.databind.JsonJaxbObjectMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class ProviderClient extends RestClient
{
    private final static ObjectMapper RESOURCE_MAPPER;

    static
    {
        RESOURCE_MAPPER = new JsonJaxbObjectMapper();
    }

    public ProviderClient(String uri, Integer port)
    {
        super(uri, port);
    }

    public List<ProviderDetails> all()
    {
        List<?> providers = request("providers").accept(MediaType.APPLICATION_JSON_TYPE).get(List.class);

        return Lists.transform(providers, new Function<Object, ProviderDetails>()
        {
            @Override
            @Nullable
            public ProviderDetails apply(@Nullable Object input)
            {
                return RESOURCE_MAPPER.convertValue(input, ProviderDetails.class);
            }
        });
    }

    public ProviderDetails getByName(String name)
    {
        return request("providers", name).accept(MediaType.APPLICATION_XML_TYPE).get(ProviderDetails.class);
    }
}
