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
package org.excalibur.discovery.service;

import java.util.List;

import org.excalibur.discovery.domain.ResourceDetails;

public interface DiscoveryService
{
    /**
     * /providers
     */
    String PROVIDERS_PATH = "/providers";
    
    /**
     * Format: providers/&lt;provider's name&gt;/clusters
     */
    String CLUSTERS_PATH = PROVIDERS_PATH + "/%s/clusters";

    /**
     * /providers/provider's name/clusters/cluster's name 
     */
    String CLUSTER_NAME_PATH = CLUSTERS_PATH + "/%s";

    /**
     * /providers/provider's name/clusters/cluster's name/members 
     */
    String CLUSTER_MEMBERS_NAME_PATH = CLUSTER_NAME_PATH + "/members";
    
    

    <T> List<T> queryForResource(String name) throws Exception;

    <T> void registerResource(String name, T resource) throws Exception;

    <T> void registerResources(String name, Iterable<T> resources) throws Exception;

    void registerResource(ResourceDetails resource) throws Exception;

    void unregisterResource(ResourceDetails resource) throws Exception;
    
}
