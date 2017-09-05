package org.excalibur.core.execution.domain;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

@XmlEnum(String.class)
@XmlRootElement(name="task-result-type")
public enum TaskOutputType 
{
	/**
	 * Task's sysout
	 */
	SYSOUT(1),
	
	/**
	 * Task's syserr output
	 */
	SYSERR(2),
	
	/**
	 * Task's file output
	 */
	FILE(3);
	
	private final int id_;
	
	private TaskOutputType(int id)
	{
		this.id_ = id;
	}

	/**
	 * @return the id
	 */
	public int getId() 
	{
		return id_;
	}
	
	public static TaskOutputType valueOf(int id)
	{
		for(TaskOutputType type: values())
		{
			if (type.getId() == id)
			{
				return type;
			}
		}
		
		throw new IllegalArgumentException(String.format("Invalid task output type %s", id));
	}
}
