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

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

@XmlEnum(String.class)
@XmlRootElement(name="resource-type")
public enum ResourceType 
{
	CPU(1), 
	
	RAM(2),
	
	DISK(3);
	
	private final int id_;
	
	private ResourceType(int id)
	{
		id_ = id;
	}

	/**
	 * @return the id
	 */
	public int getId() 
	{
		return id_;
	}
	
	@Nonnull
	public static ResourceType valueOf(int id)
	{
		for (ResourceType type: values())
		{
			if (type.getId() == id)
			{
				return type;
			}
		}
		throw new IllegalArgumentException(String.format("Invalid resource type's id %s", id));
	}
}
