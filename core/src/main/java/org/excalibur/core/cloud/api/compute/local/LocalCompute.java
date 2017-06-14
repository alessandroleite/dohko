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
package org.excalibur.core.cloud.api.compute.local;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.KeyPair;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.VirtualMachineImage;
import org.excalibur.core.cloud.api.VmConfiguration;
import org.excalibur.core.cloud.api.compute.ComputeService;
import org.excalibur.core.cloud.api.domain.InstanceTemplate;
import org.excalibur.core.cloud.api.domain.Instances;
import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.cloud.api.domain.Tag;
import org.excalibur.core.cloud.api.domain.Tags;
import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.UserKey;
import org.excalibur.core.domain.UserProviderCredentials;

import static java.lang.System.*;

public class LocalCompute implements ComputeService 
{
	private static final String ID = "local";
	private final UserProviderCredentials credentials_;
	
	
	public LocalCompute(UserProviderCredentials credentials)
	{	
		credentials_ = credentials;
	}
	
	@Override
	public Integer getId() 
	{
		return ID.hashCode();
	}

	@Override
	public String getName() 
	{
		return this.getClass().getSimpleName();
	}

	@Override
	public String getDescription() 
	{
		return "Local Driver";
	}

	@Override
	public List<Region> listRegions() 
	{
		return Collections.emptyList();
	}

	@Override
	public void close() throws IOException 
	{
	}

	@Override
	public Instances createInstances(InstanceTemplate request) 
	{
		return new Instances();
	}

	@Override
	public Instances createInstances(InstanceTemplate request, boolean waitForRunningState) 
	{
		return new Instances();
	}

	@Override
	public Instances listInstances() 
	{
		return new Instances();
	}

	@Override
	public Instances aggregateInstances() 
	{
		return new Instances();
	}

	@Override
	public VirtualMachine getInstanceWithName(String name, String zone) 
	{
		final UserKey userKey = new UserKey().setName(getProperty("org.excalibur.user.keyname"));
		
//		try 
//		{
//			userKey.setPrivateKeyMaterial(IOUtils2.readLines(ClassUtils.getDefaultClassLoader().getResourceAsStream("org/excalibur/core/driver/local/compute/id_rsa")))
//			   .setPublicKeyMaterial(IOUtils2.readLines(ClassUtils.getDefaultClassLoader().getResourceAsStream("org/excalibur/core/driver/local/compute/id_rsa.pub")));
//		} 
//		catch (IOException e) 
//		{
//		}
		
		User owner = new User()
				.setUsername(getProperty("org.excalibur.user.name"))
				.addKey(userKey);
		
		VmConfiguration configuration = new VmConfiguration()
				.setPublicIpAddress("127.0.0.1")
				.setPrivateIpAddress("127.0.0.1")
				.setKeyName(getProperty("org.excalibur.user.keyname"));
		
		return new VirtualMachine()
				.setConfiguration(configuration)
				.setLocation(new Zone().setName(zone).setRegion(credentials_.getRegion()))
				.setName(name)
				.setOwner(owner)
				.setType(new InstanceType().setName("local").setProvider(credentials_.getProvider()));
	}

	@Override
	public Instances listInstancesWithTags(Tags tags) 
	{
		return new Instances();
	}

	@Override
	public Instances listInstancesWithTags(Tag... tags) 
	{
		return new Instances();
	}

	@Override
	public void setTag(String instanceId, Tag tag) 
	{
	}

	@Override
	public void setTags(String instanceId, Tags tags) 
	{
	}

	@Override
	public void setTags(Iterable<String> instanceIds, Tags tags) 
	{
	}

	@Override
	public void stop(VirtualMachine instance) 
	{
	}

	@Override
	public void stop(String... instanceIds) 
	{
	}

	@Override
	public void stop(Instances instances) 
	{
	}

	@Override
	public void terminateInstances(String... instanceIds) 
	{
	}

	@Override
	public void terminateInstances(Instances instances) 
	{
	}

	@Override
	public void terminateInstance(VirtualMachine instance) 
	{
	}

	@Override
	public String importKeyPair(KeyPair keyPair) 
	{
		return null;
	}

	@Override
	public List<VirtualMachineImage> listImages(String... imageIds) 
	{
		return Collections.emptyList();
	}
}
