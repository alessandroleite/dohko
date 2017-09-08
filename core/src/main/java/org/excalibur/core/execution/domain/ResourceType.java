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
