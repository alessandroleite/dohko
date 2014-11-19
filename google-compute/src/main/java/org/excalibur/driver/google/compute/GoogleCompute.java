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
package org.excalibur.driver.google.compute;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.apache.commons.codec.binary.Base64;
import org.excalibur.core.cloud.api.InstanceStateDetails;
import org.excalibur.core.cloud.api.KeyPair;
import org.excalibur.core.cloud.api.KeyPairs;
import org.excalibur.core.cloud.api.Placement;
import org.excalibur.core.cloud.api.Platform;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.VirtualMachineImage;
import org.excalibur.core.cloud.api.VmConfiguration;
import org.excalibur.core.cloud.api.InstanceStateType;
import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.InstanceHardwareConfiguration;
import org.excalibur.core.cloud.api.compute.ComputeService;
import org.excalibur.core.cloud.api.domain.InstanceTemplate;
import org.excalibur.core.cloud.api.domain.Instances;
import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.cloud.api.domain.RegionStatus;
import org.excalibur.core.cloud.api.domain.Tag;
import org.excalibur.core.cloud.api.domain.Tags;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.UserProviderCredentials;
import org.excalibur.core.util.BackoffLimitedRetryHandler;
import org.excalibur.core.util.Properties2;
import org.excalibur.core.util.SystemUtils2;
import org.excalibur.core.util.ThreadUtils;
import org.excalibur.core.util.concurrent.Futures2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.ComputeScopes;
import com.google.api.services.compute.model.AccessConfig;
import com.google.api.services.compute.model.AttachedDisk;
import com.google.api.services.compute.model.AttachedDiskInitializeParams;
import com.google.api.services.compute.model.Disk;
import com.google.api.services.compute.model.Instance;
import com.google.api.services.compute.model.InstancesScopedList;
import com.google.api.services.compute.model.MachineType;
import com.google.api.services.compute.model.MachineTypesScopedList;
import com.google.api.services.compute.model.Metadata;
import com.google.api.services.compute.model.Metadata.Items;
import com.google.api.services.compute.model.NetworkInterface;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.Zone;
import com.google.api.services.compute.model.ZoneList;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static org.excalibur.core.cipher.TripleDESUtils.*;
import static org.excalibur.core.cloud.api.domain.Tags.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class GoogleCompute implements ComputeService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleCompute.class.getName());
    
    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    
    private static final Map<String, String> VM_IMAGES_NAME_MAPPING = Collections.unmodifiableMap((Map) Properties2.load("vmi_mapping.properties"));
    
    private static final Function<List<Items>, Map<String, String>> METADATA_ITEMS_TO_MAP = new Function<List<Items>, Map<String,String>>()
    {
        @Override
        @Nullable
        public Map<String, String> apply(@Nullable List<Items> items)
        {
            Map<String, String> values = new HashMap<String, String>();
            
            for (Items item: items)
            {
                if (item != null)
                {
                    values.put(item.getKey(), item.getValue());
                }
            }
            return Collections.unmodifiableMap(values);
        }
    };
    
    
    class DeployInstanceTask implements Callable<VirtualMachine>
    {
        private final BackoffLimitedRetryHandler backoffLimitedRetryHandler; 
        private final int MAX_RETRY_STATE = SystemUtils2.getIntegerProperty("org.excalibur.deployment.max.state.retry", 100);
        
        private final Instance instance;
        private final CountDownLatch latch;
        
        public DeployInstanceTask (Instance instance, CountDownLatch latch)
        {
            this.instance = instance;
            this.latch = latch;
            this.backoffLimitedRetryHandler = new BackoffLimitedRetryHandler();  
        }
        
        @Override
        public VirtualMachine call() throws Exception
        {
            int retries = 0;
            
            Operation operation = compute.instances().insert(credentials_.getProject(), instance.getZone(), instance).execute();
            
            if (LOGGER.isInfoEnabled())
            {
                LOGGER.debug("The instance [{}] was provisioned on zone [{}] at [{}]. Operation: [{}]", 
                		instance.getName(), 
                		instance.getZone(), 
                		operation.getCreationTimestamp(), 
                		operation.toPrettyString());
            }
            
            VirtualMachine vm = null;
            Instance provisionedInstance = null;
            
            try
            {
                do
                {
                    try
                    {
                        provisionedInstance = compute.instances().get(credentials_.getProject(), instance.getZone(), instance.getName()).execute();
                        vm = getInstancefromComputeInstance(provisionedInstance);
                    }
                    catch (IOException ioException)
                    {
                        String message = String.format("Instance {} is not yet available on zone {}!", instance.getName(), instance.getZone());
                        backoffForAttempt(retries + 1, message);
                    }
                } while ((provisionedInstance == null || 
                		!InstanceStateType.RUNNING.equals(InstanceStateType.valueOf(provisionedInstance.getStatus()))) && 
                		retries++ < MAX_RETRY_STATE);
            }
            catch (Throwable throwable)
            {
                LOGGER.error(throwable.getMessage(), throwable);
            }
            
            latch.countDown();
            return vm;
        }
        
        private void backoffForAttempt(int retryAttempt, String message)
        {
            backoffLimitedRetryHandler.imposeBackoffExponentialDelay(300L, 2, retryAttempt, MAX_RETRY_STATE, message);
        }
    }
    
    
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /** Instance of the HTTP transport. */
    private final HttpTransport httpTransport;

    private final Compute compute;

    private final UserProviderCredentials credentials_;

    public GoogleCompute(UserProviderCredentials credentials)
    {
        this.credentials_ = checkNotNull(credentials, "[%s] credential is null", this.getClass().getName());
        
        checkState
        (
        		!isNullOrEmpty(credentials.getLoginCredentials().getCredentialName()), 
        		"[%s] credential is null or empty [%s]",
        		this.getClass().getName(),
        		credentials.getLoginCredentials().getCredentialName()
        );
        
        checkNotNull(credentials.getRegion(), "[%s] region is null", this.getClass().getName());
        checkState(!isNullOrEmpty(credentials.getRegion().getName()), "[%s] region's name is null", this.getClass().getName());
        checkState(!isNullOrEmpty(credentials.getRegion().getEndpoint()), "[%s] region's endpoint is null", this.getClass().getName());

        try
        {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = authorize();
            compute = new Compute.Builder(httpTransport, JSON_FACTORY, null)
                          .setHttpRequestInitializer(credential)
                          .setApplicationName("excalibur")
                          .build();
            
        }
        catch (IOException | GeneralSecurityException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer getId()
    {
        return getName().hashCode();
    }

    @Override
    public String getName()
    {
        return this.getClass().getName();
    }

    @Override
    public String getDescription()
    {
        return getName();
    }

    @Override
    public List<Region> listRegions()
    {
        List<Region> regions = Lists.newArrayList();
        Compute.Zones.List zones = compute.zones().list(credentials_.getProject());
        ZoneList zoneList = zones.execute();

        for (Zone zone : zoneList.getItems())
        {
            regions.add(new Region(zone.getName())
                    .setEndpoint(zone.getSelfLink())
                    .setId(zone.getId().intValue())
                    .setStatus(RegionStatus.valueOf(zone.getStatus())));
        }

        return Collections.unmodifiableList(regions);
    }

    // https://developers.google.com/compute/docs/instances
    @Override
    public Instances createInstances(InstanceTemplate template)
    {
        Instances instances = new Instances();
        CountDownLatch latch = new CountDownLatch(template.getMaxCount());
        
        ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(this.executorService);
        List<DeployInstanceTask> tasks = Lists.newArrayList();
        
        final Instance model = createInstanceFromTemplate(template);
        
        for (int i = 0; i < template.getMaxCount(); i++)
        {
            Instance instance = model.clone();
            
            if (template.getMaxCount() > 1)
            {
                instance.setName(String.format("%s-%s", model.getName(), i + 1));
                instance.getDisks().get(0).getInitializeParams().setDiskName(instance.getName());
                instance.getDisks().get(0).setDeviceName(instance.getName());
            }
            tasks.add(new DeployInstanceTask(instance, latch));
        }
        
        try
        {
            List<Future<VirtualMachine>> submittedTasks = listeningExecutorService.invokeAll(tasks);
            latch.await(5, TimeUnit.MINUTES);
            
            for (Future<VirtualMachine> f: submittedTasks)
            {
                instances.addInstance(f.get());
            }
        }
        catch (InterruptedException e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        catch (ExecutionException e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        
        listeningExecutorService.shutdown();
        
        LOGGER.debug("Waiting the instances' ready state....");
        ThreadUtils.sleep(30 * 1000);
        
        LOGGER.debug("Created [{}] instance(s) of [{}/{}]", instances.size(), template.getMinCount(), template.getMaxCount());
        
        return instances;
    }

    @Override
    public Instances createInstances(InstanceTemplate template, boolean waitForRunningState)
    {
        return createInstances(template);
    }
    
    @Override
    public void setTag(String instanceId, Tag tag)
    {
        this.setTags(instanceId, newTags(tag));
    }
    
    @Override
    public void setTags(String instanceId, Tags tags)
    {
        this.setTags(newArrayList(instanceId), tags);
    }
    
    @Override
    public void setTags(Iterable<String> instanceIds, Tags tags)
    {
        Instances instances = this.listInstances(instanceIds);
        Metadata metadata = new Metadata().setItems(new ArrayList<Metadata.Items>());
        
        for (Tag tag: tags)
        {
            metadata.getItems().add(new Items().setKey(tag.getName()).setValue(tag.getValue()));
        }
        
        for (VirtualMachine instance : instances)
        {
            this.compute.instances().setMetadata(this.credentials_.getProject(), instance.getLocation().getName(), instance.getName(), metadata);
        }
    }
    
    public List<InstanceType> listInstanceTypes()
    {
        List<InstanceType> instanceTypes = new ArrayList<InstanceType>();
        
        Map<String, MachineTypesScopedList> machineTypesByZone = this.compute.machineTypes().aggregatedList(this.credentials_.getProject()).execute().getItems();
        
        for (String zone: machineTypesByZone.keySet())
        {
            List<MachineType> machineTypesOfZone = machineTypesByZone.get(zone).getMachineTypes();
            
            if (machineTypesOfZone != null)
            {
                for (MachineType type: machineTypesOfZone)
                {
                    InstanceType instance = new InstanceType()
                            .setConfiguration(
                                    new InstanceHardwareConfiguration()
                                            .setNumberOfCores(type.getGuestCpus())
                                            .setRamMemorySizeGb(BigDecimal.valueOf(type.getMemoryMb().doubleValue() / 1024)
                                                                          .setScale(2, java.math.RoundingMode.HALF_EVEN).doubleValue())
                                            .setDiskSizeGb(type.getImageSpaceGb().longValue()))
                                            .setId(type.getId().intValue())
                            .setName(type.getName());
                    if (!instanceTypes.contains(instance))
                    {
                        instanceTypes.add(instance);
                    }
                }
            }
        }
        
        return ImmutableList.copyOf(instanceTypes);
    }
    
    protected Instance createInstanceFromTemplate(InstanceTemplate template)
    {
        Instance instance = new Instance()
                .setMachineType(template.getInstanceType().getName())
                .setName(template.getInstanceName())
                .setNetworkInterfaces(new ArrayList<NetworkInterface>());
        
        instance.getNetworkInterfaces().add(new NetworkInterface().setAccessConfigs(new ArrayList<AccessConfig>()).setNetwork(
                String.format("https://www.googleapis.com/compute/v1/projects/%s/global/networks/default", credentials_.getProject())));
        instance.getNetworkInterfaces().get(0).getAccessConfigs().add(new AccessConfig().setName("External NAT").setType("ONE_TO_ONE_NAT"));

        instance.setZone(template.getRegion().getName());
        instance.setMachineType(getMachineType(instance.getZone(), template.getInstanceType().getName()).getSelfLink());

        instance.setDisks(new ArrayList<AttachedDisk>());
        instance.getDisks().add(new AttachedDisk()
                .setType("PERSISTENT")
                .setAutoDelete(false)
                .setBoot(true)
                .setMode("READ_WRITE")
                .setInitializeParams(
                        new AttachedDiskInitializeParams()
                                .setDiskName(template.getInstanceName())
                                .setDiskSizeGb(SystemUtils2.getIntegerProperty("org.excalibur.default.disk.size", 10).longValue())
                                .setSourceImage(VM_IMAGES_NAME_MAPPING.get(template.getImageId()))));

        List<Items> items = new ArrayList<Metadata.Items>();
        
        Metadata metadata = new Metadata().setItems(items);
        instance.setMetadata(metadata);
        
        items.add
        (       new Items().setKey("sshKeys")
                           .setValue(String.format("%s:%s", template.getLoginCredentials().getUser(), 
                                   decrypt(template.getLoginCredentials().getPublicKey()).replaceAll("\n", "")))
        );
        
        items.add(new Items().setKey("image-id").setValue(template.getImageId()));
        items.add(new Items().setKey("keyname").setValue(template.getKeyName().toLowerCase()));
        items.add(new Items().setKey("zone").setValue(instance.getZone()));
        items.add(new Items().setKey("platform").setValue(Platform.LINUX.name().toLowerCase()));
        items.add(new Items().setKey("platform-username").setValue(template.getLoginCredentials().getUser()));
        items.add(new Items().setKey("owner").setValue(template.getLoginCredentials().getUser()));
        items.add(new Items().setKey("pem-key").setValue(Base64.encodeBase64URLSafeString(template.getLoginCredentials().getPrivateKey().getBytes())));
        
        
        for (Tag tag: template.getTags())
        {
            items.add(new Items().setKey(tag.getName()).setValue(tag.getValue()));
        }
        
        return instance;
    }
    
    public Instances listInstances(Iterable<String> instanceIds)
    {
        final Instances instances = new Instances();
        Map<String, Instance> availableInstances = getAllComputeInstances();
        
        for (String instanceId: instanceIds)
        {
            if (availableInstances.containsKey(instanceId))
            {
                instances.addInstance(getInstancefromComputeInstance(availableInstances.get(instanceId)));
            }
        }
        
        return instances;
    }

    @Override
    public Instances listInstances()
    {
        Instances instances = new Instances();
        Map<String, Instance> availableInstances = getAllComputeInstances();

        for (Instance instance : availableInstances.values())
        {
            instances.addInstance(getInstancefromComputeInstance(instance));
        }
        
        return instances;
    }
    
    @Override
    public Instances listInstancesWithTags(Tags tags)
    {
        final Instances instances = new Instances();
        
        external: for (VirtualMachine vm : listInstances())
        {
            for (Tag tag: tags)
            {
                if (vm.getTags().contains(tag))
                {
                    instances.addInstance(vm);
                    continue external;
                }
            }
        }
        
        return instances;
    }
    
    @Override
    public Instances listInstancesWithTags(Tag ... tags)
    {
        return this.listInstancesWithTags(Tags.newTags(tags));
    }
    
    @Override
    public VirtualMachine getInstanceWithName(String name, String zone)
    {
        VirtualMachine vm = null;
        
        Instance instance = this.compute.instances().get(credentials_.getProject(), zone, name).execute();
        
        if (instance != null)
        {
            vm = getInstancefromComputeInstance(instance);
        }
    	
    	return vm;
    }
    
    @Override
    public Instances aggregateInstances()
    {
    	final Instances instances = new Instances();
    	ImmutableMap<String, Instance> allComputeInstances = getAllComputeInstances();
    	
    	for (Instance inst: allComputeInstances.values())
    	{
    		instances.addInstance(getInstancefromComputeInstance(inst));
    	}
    	
    	return instances;
    }
    

    protected MachineType getMachineType(String zone, String machineType)
    {
        return this.compute.machineTypes().get(credentials_.getProject(), zone, machineType).execute();
    }
    
    protected Disk getDisk(String zone, String disk)
    {
        return this.compute.disks().get(credentials_.getProject(), zone, disk).execute();
    }
    
    protected Disk getBootDisk(Instance instance)
    {
        AttachedDisk bootDisk = Lists.newArrayList(Iterables.filter(instance.getDisks(), new Predicate<AttachedDisk>()
        {
            @Override
            public boolean apply(@Nullable AttachedDisk input)
            {
                return input != null && input.getBoot();
            }
        })).get(0);
        
        //https://www.googleapis.com/compute/v1/projects/poised-bot-553/zones/us-central1-a/disks/f1-micro-test
        //persistent-disk-0
        // I removed this constraint because of a possible bug in the Google Compute Engine, since it is not setting the correct name of the disk.
        // This issue is also occurring at the google client. Above we have an example of the returned values;
//        Preconditions.checkState(Objects.equal(splitAndGetLast("/", bootDisk.getSource()), bootDisk.getDeviceName()));
//        return getDisk(splitAndGetLast("/", instance.getZone()), bootDisk.getDeviceName());
        return getDisk(splitAndGetLast("/", instance.getZone()), splitAndGetLast("/", bootDisk.getSource()));
    }
    
    protected ImmutableMap<String, Instance> getAllComputeInstances()
    {
        Map<String, Instance> instances = new HashMap<String, Instance>();
        
        Map<String, InstancesScopedList> instancesByZone = this.compute.instances().aggregatedList(credentials_.getProject()).execute().getItems();
        
        for (String zone: instancesByZone.keySet())
        {
            List<Instance> instancePerZone = instancesByZone.get(zone).getInstances();
            if (instancePerZone != null)
            {
                for (Instance instance : instancePerZone)
                {
                    instances.put(instance.getName(), instance);
                }
            }
        }
        
        return ImmutableMap.copyOf(instances);
    }

    private VirtualMachine getInstancefromComputeInstance(Instance computeInstance)
    {
        MachineType machineType = getMachineType(splitAndGetLast("/", computeInstance.getZone()), 
        		splitAndGetLast("/", computeInstance.getMachineType()));
        
        NetworkInterface networkInterface = computeInstance.getNetworkInterfaces().get(0);
        Map<String, String> metadata = METADATA_ITEMS_TO_MAP.apply(computeInstance.getMetadata().getItems());
        checkState(metadata.size() >= 8);

        AccessConfig accessConfig = Lists.newArrayList(Iterables.filter(networkInterface.getAccessConfigs(), new Predicate<AccessConfig>()
        {
            @Override
            public boolean apply(@Nullable AccessConfig input)
            {
                return "ONE_TO_ONE_NAT".equals(input.getType());
            }
        })).get(0);

        VirtualMachine instance = new VirtualMachine();
        
        KeyPairs keypairs = new KeyPairs();
		keypairs.setPrivateKey
		        (
		        		new KeyPair().setKeyName(metadata.get("keyname"))
		        		             .setKeyMaterial(new String(Base64.decodeBase64(metadata.get("pem-key"))))
		        )
			    .setPublicKey
			    (
			    		new KeyPair().setKeyName(metadata.get("keyname"))
			    		             .setKeyMaterial(cipher(metadata.get("sshKeys").split(":")[1]))
				);

        instance.setConfiguration(new VmConfiguration()
                .setKeyName(metadata.get("keyname"))
                .setPlatform(metadata.get("platform"))
                .setPlatformUserName(metadata.get("platform-username"))
                .setPrivateIpAddress(networkInterface.getNetworkIP())
                .setPublicDnsName(accessConfig.getNatIP())
                .setPublicIpAddress(accessConfig.getNatIP())
                .setKeyPairs(keypairs));
        
		instance.setImageId(metadata.get("image-id"))
				.setLaunchTime(
						new Date(DateTime.parseRfc3339(computeInstance.getCreationTimestamp()).getValue()))
				.setName(computeInstance.getName())
				.setPlacement(new Placement().setZone(metadata.get("zone")))
				.setState(
						new InstanceStateDetails()
						    //.setState(InstanceStateType.valueOf(computeInstance.getStatus()))
						    .setState(InstanceStateType.RUNNING)
						    .setTime(instance.getLaunchTime()))
						    .setType(new InstanceType().setName(machineType.getName())
								.setId(machineType.getId().intValue())
								.setProvider(this.credentials_.getProvider()))
				.setLocation(new org.excalibur.core.cloud.api.domain.Zone().setName(metadata.get("zone")));
        
        Disk disk = getBootDisk(computeInstance);
                
        instance.getType().getConfiguration()
                .setNumberOfCores(machineType.getGuestCpus())
                .setRamMemorySizeGb
                (
                		BigDecimal.valueOf(machineType.getMemoryMb().doubleValue() / 1024)
                		.setScale(2,java.math.RoundingMode.HALF_EVEN)
                		.doubleValue()
                )
                .setDiskSizeGb(disk.getSizeGb());
        
        instance.setOwner(new User().setId(this.credentials_.getUserId()).setUsername(metadata.get("owner")));
        
        for (String tagKey: metadata.keySet())
        {
        	instance.getTags().add(new Tag().setName(tagKey).setValue(metadata.get(tagKey)));
        }

        return instance;
    }

    @Override
    public void stop(VirtualMachine instance)
    {
        this.stop(instance.getName());
    }
    
    public void stop(Instances instances)
    {
    	if (instances != null)
    	{
    		this.stop(instances.instancesName());
    	}
    }

    @Override
    public void stop(String... names)
    {
        ImmutableMap<String, Instance> instances = getAllComputeInstances();
        
        for (String instanceName: names)
        {
            Instance instance = instances.get(instanceName);
            Disk disk = getBootDisk(instance);
            compute.instances().setDiskAutoDelete(credentials_.getProject(), instance.getZone(), instance.getName(), false, disk.getName());
        }
        
        this.terminateInstances(names);
    }

    @Override
    public void terminateInstances(final String... instanceNames)
    {
        if (instanceNames != null && instanceNames.length > 0)
        {
            final Map<String, Instance> availableInstances = this.getAllComputeInstances();
            
            List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
            
            for (final String name: instanceNames)
            {
                tasks.add(new Callable<Boolean>()
                {
                    @Override
                    public Boolean call() throws Exception
                    {
                        Instance instanceToTerminate = availableInstances.get(name);
                        
                        if (instanceToTerminate != null)
                        {
                            terminateInstance(splitAndGetLast("/", instanceToTerminate.getZone()), instanceToTerminate.getName());
                            
                            return true;
                        }
                        return false;
                    }
                });
            }
            
            Futures2.invokeAllAndShutdownWhenFinish(tasks, "terminate-instances-thread");
        }
    }
    
    @Override
    public void terminateInstances(Instances instances)
    {
        terminateInstances(Iterables.toArray(instances.getInstanceNames(), String.class));
    }

    @Override
    public void terminateInstance(VirtualMachine instance)
    {
        if (instance != null && Strings.isNullOrEmpty(instance.getName()))
        {
            this.terminateInstances(instance.getName());
        }
    }
    
    @Override
    public String importKeyPair(KeyPair keyPair)
    {
        return null;
    }
    
    @Override
    public List<VirtualMachineImage> listImages(String ... imageIds)
    {
        List<VirtualMachineImage> images = Lists.newArrayList();
        return images;
    }
    
    @Override
    public void close()
    {
        
    }
    
    public void terminateInstance(String region, String name)
    {
        compute.instances().delete(credentials_.getProject(), region, name).execute();
    }

    private Credential authorize() throws IOException, GeneralSecurityException
    {
        PrivateKey securityKey = SecurityUtils.loadPrivateKeyFromKeyStore
        (
        		SecurityUtils.getPkcs12KeyStore(), 
                ClassUtils.getDefaultClassLoader().getResourceAsStream(credentials_.getLoginCredentials().getCredential()), 
                "notasecret", "privatekey", "notasecret"
        );
        
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), 
                new FileReader(new File(SystemUtils2.getApplicationDataPath(), credentials_.getLoginCredentials().getIdentity())));
        
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId((String) clientSecrets.getWeb().get("client_email"))
                .setServiceAccountScopes(Arrays.asList(ComputeScopes.COMPUTE, ComputeScopes.DEVSTORAGE_FULL_CONTROL))
                .setServiceAccountPrivateKey(securityKey)
                .build();

        return credential;
    }
    
    static String splitAndGetLast(String regex, String value)
    {
    	String[] parts = value.split(regex);
    	return parts != null && parts.length > 0 ? parts[parts.length - 1] : value;
    }
}
