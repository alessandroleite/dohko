package org.excalibur.discovery.jackson.databind.test;

import java.io.IOException;

import org.excalibur.core.domain.User;
import org.excalibur.core.execution.domain.Application;
import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.core.execution.domain.Block;
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
				.addApplication(new Application().setName("echo").setCommandLine("echo 'hello world'"));
		
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
				.addApplication(new Application().setName("echo").setCommandLine("echo 'hello world'"));
		
		toSerialize.addApplication(new Application().setName("echo2").setCommandLine("echo Hello World"));
		
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
		
		toSerialize.addApplication(echo);
		
		String json = mapper.writeValueAsString(toSerialize);
		assertFalse(Strings.isNullOrEmpty(json));
		
		ApplicationDescriptor deserialized = mapper.readValue(json, ApplicationDescriptor.class);
		assertNotNull(deserialized);
		assertEquals(1, deserialized.getApplications().size());
		assertEquals(1, deserialized.getApplication(0).orNull().getPreconditions().size());
		assertEquals(echo.getPreconditions().get(0), deserialized.getApplication(0).orNull().getPreconditions().get(0));
	}
	
	@Test
	public void must_serialize_an_application_descriptor_with_two_blocks() throws IOException
	{
		ApplicationDescriptor toSerialize = new ApplicationDescriptor()
				.setName("test")
				.setUser(new User().setUsername("Alice"))
				.addPrecondition(newPrecondition("a"));
		
		Block b1 = new Block()
				.setId("1")
				.addApplication(new Application().setName("echo1").setCommandLine("echo 'Hello World 1'").addPrecondition(newPrecondition("b")));
		
		Block b2 = new Block()
				.setId("2")
				.addApplications(
						new Application().setName("echo2").setCommandLine("echo 'Hello World 2'").addPrecondition(newPrecondition("c", "b", "d")),
						new Application().setName("echo3").setCommandLine("echo '3'"))
				.setParents("1")
				.setRepeat(2);
		
		toSerialize.addBlocks(b1, b2);
		toSerialize.addApplications(new Application().setName("echo4").setCommandLine("echo '4'"));
		
		String json = mapper.writeValueAsString(toSerialize);
		assertFalse(Strings.isNullOrEmpty(json));
		
		ApplicationDescriptor deserialized = mapper.readValue(json, ApplicationDescriptor.class);
		assertNotNull(deserialized);
		assertEquals(1, deserialized.getApplications().size());
		assertEquals(2, deserialized.getBlocks().size());
	}
}
