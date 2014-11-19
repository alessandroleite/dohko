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
package org.excalibur.service;

import org.excalibur.core.util.SystemUtils2;
import org.excalibur.service.deployment.server.WebServer;

public class Main
{
    
    private Main()
    {
        throw new UnsupportedOperationException();
    }
    
    public static void main(String[] args) throws Exception
    {
        SystemUtils2.createApplicationDataDir();
        
        // String connectionString = System.getProperty("org.excalibur.zoo.connection.url"); //"129.175.29.83:2181"
        // String host = System.getProperty("org.excalibur.host");
        String contextPath = System.getProperty("org.excalibur.context.path", "/");
        // String basePath = System.getProperty("org.excalibur.base.path", "/");
        // String serviceName = System.getProperty("org.excalibur.service.name", "/services");

        // CuratorFramework client = CuratorFrameworkFactory.newClient(connectionString, new ExponentialBackoffRetry(1000, 3));
        // client.start();

        // ApplicationServer server = new ApplicationServer(client, host, contextPath, basePath, serviceName);
        // server.start();

        WebServer server = new WebServer(contextPath);
        server.start();

        server.close();
    }
}
