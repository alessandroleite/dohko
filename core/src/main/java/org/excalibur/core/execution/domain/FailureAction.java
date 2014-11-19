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
package org.excalibur.core.execution.domain;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

import static com.google.common.base.Preconditions.*;

@XmlRootElement(name = "failure-action")
public enum FailureAction
{
    /**
     * 
     */
    @XmlEnumValue("RESTART")
    RESTART(1),

    /**
     * 
     */
    @XmlEnumValue("ABORT")
    ABORT(2);

    private final Integer id_;

    private FailureAction(Integer id_)
    {
        this.id_ = id_;
    }

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id_;
    }

    public static FailureAction valueOf(Integer id)
    {
        checkState(id != null && id > 0);
        
        for (FailureAction action : values())
        {
            if (action.getId().equals(id))
            {
                return action;
            }
        }

        throw new IllegalArgumentException(String.format("Invalid failure action [%s]!", id));
    }
}
