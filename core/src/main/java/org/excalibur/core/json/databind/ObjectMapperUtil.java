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
package org.excalibur.core.json.databind;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.google.common.base.Strings;

public class ObjectMapperUtil 
{
	private static final Logger LOG = LoggerFactory.getLogger(ObjectMapperUtil.class);
	private final ObjectMapper mapper = new ObjectMapper();
	
	public ObjectMapperUtil()
	{
		mapper.registerModules(new JaxbAnnotationModule(), new GuavaModule());
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	}
	
	public ObjectMapper mapper()
	{
		return mapper;
	}
	
	public <T> Optional<String> toJson(T type)
	{
		Optional<String> result = Optional.empty();
		
		try 
		{
			result = Optional.of(mapper.writeValueAsString(type));
		} 
		catch (JsonProcessingException e) 
		{
			LOG.error("Error on serializing object [{}].", type);
			LOG.error("The reason is [{}]", e.getMessage(), e);
		}
		
		return result;
	}
	
	public <T> Optional<String> writeValueAsString(T type)
	{
		Optional<String> result = Optional.empty();
		
		try 
		{
			result = Optional.of(mapper.writeValueAsString(type));
		} 
		catch (JsonProcessingException e) 
		{
			LOG.error("Error on serializing object [{}].", type);
			LOG.error("The reason is [{}]", e.getMessage(), e);
		}
		
		return result;
	}
	
	public <T> Optional<T> readJsonValue(Class<T> type, String content)
	{
		Optional<T> result = Optional.empty();
		
		if (!Strings.isNullOrEmpty(content))
		{
			try 
			{
				result = Optional.of(mapper.readValue(content, type));
			} 
			catch (IOException e) 
			{
				LOG.error("Error on deserializing type [{}] from content [{}].", type, content);
				LOG.error("The reason is [{}]", e.getMessage(), e);
			}
		}
		
		return result;
		
	}
	
	public <T> Optional<T> readJsonValue(Class<T> type, String name, ResultSet r) throws SQLException
	{
		Optional<T> result = Optional.empty();
		String value = r.getString(name);
		
		if (!r.wasNull() && !Strings.isNullOrEmpty(value))
		{
			result = readJsonValue(type, value);
		}
		
		return result;
	}
}
