package org.excalibur.discovery.jackson.databind.test;

import java.io.IOException;

import org.excalibur.core.domain.User;
import org.excalibur.core.execution.domain.Application;
import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.core.execution.domain.Applications;
import org.excalibur.core.execution.domain.Precondition;
import org.excalibur.jackson.databind.JsonYamlObjectMapper;
import org.junit.Test;

import com.google.common.base.Strings;

import static org.junit.Assert.*;

import static org.excalibur.core.execution.domain.Precondition.*;

public class ApplicationDescriptorSerializerTest 
{
	private final JsonYamlObjectMapper mapper = new JsonYamlObjectMapper();
	
	@Test
	public void must_serialize_a_minimal_application_descriptor () throws IOException
	{
		ApplicationDescriptor toSerialize = new ApplicationDescriptor()
				.setName("test")
				.setUser(new User().setUsername("Alice"))
				.setApplications(new Applications().add(new Application().setName("echo").setCommandLine("echo 'hello world'")));
		
		String json = mapper.writeValueAsString(toSerialize);
		assertNotNull(json);
		assertFalse(Strings.isNullOrEmpty(json));
		
		ApplicationDescriptor deserialized = mapper.readValue(json, ApplicationDescriptor.class);
		assertNotNull(deserialized);
		assertEquals(toSerialize.getName(), deserialized.getName());
		assertEquals(toSerialize.getUser().getUsername(), deserialized.getUser().getUsername());
		assertEquals(toSerialize.getApplications().size(), deserialized.getApplications().size());
	}
	
	
	@Test
	public void must_serialize_an_application_descriptor_with_multiple_applications () throws IOException
	{
		ApplicationDescriptor toSerialize = new ApplicationDescriptor()
				.setName("test")
				.setUser(new User().setUsername("Alice"))
				.setApplications(new Applications().add(new Application().setName("echo").setCommandLine("echo 'hello world'")));
		
		toSerialize.getApplications().add(new Application().setName("echo2").setCommandLine("echo Hello World"));
		
		String json = mapper.writeValueAsString(toSerialize);
		assertNotNull(json);
		assertFalse(Strings.isNullOrEmpty(json));
		
		ApplicationDescriptor deserialized = mapper.readValue(json, ApplicationDescriptor.class);
		assertNotNull(deserialized);
		assertEquals(2, deserialized.getApplications().size());
	}
	
	@Test
	public void must_serialize_an_application_descriptor_with_preconditions() throws IOException 
	{
		Precondition precondition = newPrecondition("a", "b");
		
		ApplicationDescriptor toSerialize = new ApplicationDescriptor()
				.setName("test")
				.setUser(new User().setUsername("Alice"))
				.addPrecondition(precondition);
		
		Application echo = new Application()
				.setName("echo")
				.setCommandLine("echo Hello World")
				.addPrecondition(precondition);
		
		
		toSerialize.getApplications().add(echo);
		
		String json = mapper.writeValueAsString(toSerialize);
		assertFalse(Strings.isNullOrEmpty(json));
		
		ApplicationDescriptor deserialized = mapper.readValue(json, ApplicationDescriptor.class);
		assertNotNull(deserialized);
		assertEquals(1, deserialized.getApplications().size());
		assertEquals(1, deserialized.getApplications().first().orNull().getPreconditions().size());
		assertEquals(echo.getPreconditions().get(0), deserialized.getApplications().first().orNull().getPreconditions().get(0));
	}
}
