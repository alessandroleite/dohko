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
package org.excalibur.core.cloud.api.domain;

public enum SpotType
{
    /**
     * <p>
     * A one-time request remains active until one of the following conditions is met: all of the requested instances launch, the request expires, or
     * the request was canceled. For example, if you create a one-time request for three instances, the request is considered complete after all three
     * instances launch.
     * </p>
     */
    ONE_TIME(1, "one-time"),

    /**
     * <p>
     * Persistent Spot Instance requests remain active until they expire or you cancel them, even if the requests were previously satisfied. For
     * example, if you create a persistent spot instance request for one instance when the spot price is $0.300, the provider launches and keeps your
     * instance running if your maximum bid price is above $0.300. If the spot price rises above your maximum bid price and consequently your spot
     * instance is terminated, your spot instance request remains active and the provider will launch another spot instance for you when the spot
     * price falls below your maximum bid price.
     * </p>
     * 
     * <p>
     * With both one-time and persistent requests, instances continue to run until they no longer exceed the spot price, you terminate them, or the
     * instances terminate on their own. If the maximum price is exactly equal to the Spot Price, an instance might or might not continue running
     * (depending on available capacity).
     * </p>
     */
    PERSISTENT(2, "persistent");

    private final Integer id_;
    private final String name;

    private SpotType(Integer id, String name)
    {
        this.id_ = id;
        this.name = name;
    }
    
    /**
     * @return the id
     */
    public Integer getId()
    {
        return id_;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    
    public static SpotType valueOf(Integer id)
    {
        SpotType type = null;
        int i = 0;
        
        while(i < values().length && !(type = values()[i]).getId().equals(id))
        {
            type = null;
            i++;
        }
        return type;
    }
    
    public static SpotType valueOfFrom(String name)
    {
        SpotType type = null;
        int i = 0;
        
        while(i < values().length && !(type = values()[i]).getName().equals(name))
        {
            type = null;
            i++;
        }
        return type;
    }
}
