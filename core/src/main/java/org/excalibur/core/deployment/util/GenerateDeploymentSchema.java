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
package org.excalibur.core.deployment.util;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.excalibur.core.deployment.domain.Deployment;

public class GenerateDeploymentSchema
{
    public static void main(String[] args) throws Exception
    {
        JAXBContext jc = JAXBContext.newInstance(Deployment.class);
        jc.generateSchema(new SchemaOutputResolver()
        {
            @Override
            public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException
            {
                return new StreamResult(suggestedFileName);
            }
        });
    }
}
