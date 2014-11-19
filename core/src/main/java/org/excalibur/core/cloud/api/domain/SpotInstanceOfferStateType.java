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


public enum SpotInstanceOfferStateType
{
    /**
     * The request is not fulfilled.
     */
    OPEN(1, "Open"),

    /**
     * The request is currently active (fulfilled) and has an associated spot instance.
     */
    ACTIVE(2, "Active"),

    /**
     * The request failed because bad parameters were specified.
     */
    FAILED(3, "Failed"),

    /**
     * The request either completed (a Spot Instance was launched and subsequently was interrupted or terminated), or was not fulfilled within the
     * period specified.
     */
    CLOSED(4, "Closed"),
    
    /**
     * The request is canceled because one of two events took place: You canceled the request, or the bid request went past its expiration date.
     */
    CANCELED(5, "Canceled");

    private final Integer id_;
    private final String name_;

    private SpotInstanceOfferStateType(Integer id, String name)
    {
        this.id_ = id;
        this.name_ = name;
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
        return name_;
    }
    
    public static SpotInstanceOfferStateType valueOf(Integer id)
    {
        SpotInstanceOfferStateType state = null;
        int i = 0;
        
        while(i < values().length && !(state = values()[i]).getId().equals(id))
        {
            state = null;
            i++;
        }
        return state;
    }
    
    public static SpotInstanceOfferStateType valueOfFrom(String name)
    {
        SpotInstanceOfferStateType state = null;
        int i = 0;
        
        while(i < values().length && !(state = values()[i]).getName().equals(name))
        {
            state = null;
            i++;
        }
        return state;
    }
}
