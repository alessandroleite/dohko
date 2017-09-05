package org.excalibur.core.execution.domain;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

import static java.lang.String.*;

@XmlEnum(String.class)
@XmlRootElement(name = "task-status-type")
public enum TaskStatusType 
{
	 /**
	  * Task is waiting its execution.
	  */
    PENDING(1), 
    
    /**
     * Task is running
     */
    RUNNING(2), 
    
    /**
     * Task has failed
     */
    FAILED(3), 
    
    
    /**
     * Task has finished
     */
    FINISHED(4); 
	
	private final int id_;

	private TaskStatusType(int id)
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
	
	/**
	 * Returns the enum's name.
	 * @return enum's name.
	 * @see #name()
	 */
	public String getName() 
	{
		return name();
	}
	
	/**
	 * Returns an enumeration given its id.
	 * 
	 * @param id id of the enum to return.
	 * @return enum with the given id
	 * @throws IllegalArgumentException if the id is unknown
	 */
	public static TaskStatusType valueOf(int id) 
	{
		for (TaskStatusType type: values())
		{
			if (type.getId() == id) 
			{
				return type;
			}
		}
		
		throw new IllegalArgumentException(format("Invalid %s task status type", id));
	}
}
