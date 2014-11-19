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
package org.excalibur.core.services;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import org.excalibur.core.cloud.api.InstanceStateDetails;
import org.excalibur.core.cloud.api.InstanceStateType;
import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.Provider;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.VirtualMachineImage;
import org.excalibur.core.cloud.api.compute.ComputeService;
import org.excalibur.core.cloud.api.compute.ComputeServiceBuilder;
import org.excalibur.core.cloud.api.domain.Instances;
import org.excalibur.core.cloud.api.domain.Tag;
import org.excalibur.core.cloud.api.domain.Tags;
import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.UserProviderCredentials;
import org.excalibur.core.domain.repository.InstanceRepository;
import org.excalibur.core.domain.repository.InstanceTagRepository;
import org.excalibur.core.domain.repository.InstanceTypeRepository;
import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.domain.repository.VirtualMachineImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static org.excalibur.core.cloud.api.VirtualizationType.*;


@Service
public class InstanceService
{
    private static final Logger LOG = LoggerFactory.getLogger(InstanceService.class.getName());
    
    @Autowired
    private InstanceRepository instanceRepository_;
    
    @Autowired
    private InstanceTagRepository instanceTagRepository_;

    @Autowired
    private InstanceTypeRepository instanceTypeRepository_;

    @Autowired
    private RegionRepository regionRepository_;
    
    @Autowired
    private VirtualMachineImageRepository virtualMachineImageRepository_;
    
    @Autowired
    private ProviderService providerService_;
    
    @Autowired
    private UserService userService_;

    public void insertOrUpdateInstances(Iterable<VirtualMachine> instances)
    {
        for (VirtualMachine newInstance : instances)
        {
            VirtualMachine oldInstanceState = this.getInstanceByNameOnProvider(newInstance.getName(), newInstance.getType().getProvider());

            if (oldInstanceState != null)
            {
                this.mergeAndUpdateInstance(oldInstanceState, newInstance);
            }
            else
            {
                this.insertInstance(newInstance);
            }
        }
    }

    public void insertInstances(@Nonnull Iterable<VirtualMachine> instances)
    {
        for (VirtualMachine instance : instances)
        {
            insertInstance(instance);
        }
    }

    public void insertInstance(@Nonnull VirtualMachine instance)
    {
        instance.setType(instanceTypeRepository_.findInstanceTypeByName(instance.getType().getName()));
        instance.setOwner(this.userService_.findUserByUsername(instance.getOwner().getUsername()));
        
        if (instance.getLocation() == null && instance.getPlacement() != null && !isNullOrEmpty(instance.getPlacement().getZone()))
        {
            instance.setLocation(new Zone().setName(instance.getPlacement().getZone()));
        }
        
        instance.setLocation(regionRepository_.findZoneByName(instance.getLocation().getName()));
        instance.getType().setRegion(instance.getLocation().getRegion());
        instance.setId(instanceRepository_.insertInstance(instance));

        if (instance.getState() != null)
        {
            updateInstanceState(instance);
        }
        
        completeInstanceState(instance);
    }

    public List<VirtualMachine> updateInstances(@Nonnull Iterable<VirtualMachine> instances)
    {
        List<VirtualMachine> updatedInstances = newArrayList();

        for (final VirtualMachine instance : instances)
        {
            final VirtualMachine newInstanceState = this.getInstanceByNameOnProvider(instance.getName(), instance.getType().getProvider());

            mergeAndUpdateInstance(instance, newInstanceState);
            updatedInstances.add(newInstanceState);
        }
        return updatedInstances;
    }

    protected void mergeAndUpdateInstance(VirtualMachine oldInstanceState, VirtualMachine newInstanceState)
    {
        if (!oldInstanceState.getConfiguration().equals(newInstanceState.getConfiguration()))
        {
            oldInstanceState.getConfiguration().setPrivateIpAddress(newInstanceState.getConfiguration().getPrivateIpAddress());
            oldInstanceState.getConfiguration().setPublicDnsName(newInstanceState.getConfiguration().getPublicDnsName());
            oldInstanceState.getConfiguration().setPublicIpAddress(newInstanceState.getConfiguration().getPublicIpAddress());
            oldInstanceState.setLaunchTime(newInstanceState.getLaunchTime());
            
            oldInstanceState.setUserData(newInstanceState.getUserData());
            
            instanceRepository_.updateInstance(oldInstanceState);
            
            try
            {
                updateInstanceState(oldInstanceState);
            }
            catch (Exception exception)
            {
                // LOG.error(exception.getMessage(), exception);
            }
        }
        
//        LOG.debug("old-instance-state:[{}] new-instance-state:[{}]", oldInstanceState.getState(), newInstanceState.getState());
//        
//        if (newInstanceState.getState().getInstance() == null)
//        {
//            newInstanceState.getState().setInstance(oldInstanceState);
//        }
        
//        if (!oldInstanceState.getState().equals(newInstanceState.getState()))
////        {
//            oldInstanceState.setState(newInstanceState.getState());
//            try
//            {
//                updateInstanceState(oldInstanceState);
//            }
//            catch (Exception exception)
//            {
////                LOG.error(exception.getMessage(), exception);
//            }
//        }
        
        newInstanceState.setId(oldInstanceState.getId());
        newInstanceState.setCost(oldInstanceState.getCost());
        
        User owner = !isNullOrEmpty(newInstanceState.getOwner().getUsername()) ? this.userService_.findUserByUsername(newInstanceState.getOwner()
                .getUsername()) : this.userService_.findUserById(newInstanceState.getOwner().getId());
        
        newInstanceState.setOwner(owner);
        completeInstanceState(newInstanceState);
    }

    public void updateInstancesState(@Nonnull Iterable<VirtualMachine> instances)
    {
        for (VirtualMachine instance : instances)
        {
            InstanceStateDetails state = instance.getState();
            checkNotNull(state).setInstance(instance);
            
            state.setId(instanceRepository_.insertInstanceState(state));
        }
    }

    public void updateInstanceState(@Nonnull VirtualMachine instance)
    {
        this.updateInstancesState(newArrayList(instance));
    }

    public List<VirtualMachine> listStoppedInstancesOfProvider(Integer providerId)
    {
        checkNotNull(providerId);
         List<VirtualMachine> vms = this.instanceRepository_.listInstancesOfUserWithState(providerId, InstanceStateType.STOPPED);
         
         for(VirtualMachine vm: vms)
         {
             completeInstanceState(vm);
         }
         
         return vms;
    }

    public VirtualMachine getInstanceByNameOnProvider(String name, Provider provider)
    {
        checkNotNull(name);
        checkState(provider != null && !isNullOrEmpty(provider.getName()));
        
        Provider providerName = this.providerService_.get(provider.getName());
        VirtualMachine vm = this.instanceRepository_.findInstanceByName(name, providerName.getId());
        
        return completeInstanceState(vm);
    }
    
    public Instances getRunningInstancesWithTagFromUser(Tag tag, User owner)
    {
        checkNotNull(tag);
        checkArgument(tag.isValid(), "Invalid tag [%s] state", tag);
        
        Instances runningInstances = this.listRunningInstances(owner);
        Instances instances = new Instances();
        
        for (VirtualMachine instance: runningInstances)
        {
            if (instance.getTags().contains(tag))
            {
                instances.addInstance(instance);
            }
        }
        
        return instances;
    }
    
    public VirtualMachine getInstanceByName(String name)
    {
        checkArgument(!isNullOrEmpty(name));
        VirtualMachine vm = this.instanceRepository_.findInstanceByName(name);
        return completeInstanceState(vm);
    }
    
    public VirtualMachine getInstanceByPublicIp(String host)
    {
        checkArgument(!isNullOrEmpty(host));
        
        VirtualMachine vm = this.instanceRepository_.findInstanceByPublicIp(host);
        return completeInstanceState(vm);
    }
    
    
    public List<VirtualMachineImage> listAvailableImagesForInstanceType(String type, Integer regionId)
    {
        checkArgument(!isNullOrEmpty(type));
        checkArgument(regionId != null);
        
        InstanceType instanceType = this.instanceTypeRepository_.findInstanceTypeByName(type);
        List<VirtualMachineImage> images = newArrayList();
        
        if (instanceType != null)
        {
            if (instanceType.getRequiredVirtualizationType() != null && !ANY.equals(instanceType.getRequiredVirtualizationType()))
            {
                images = this.virtualMachineImageRepository_.listAllVirtualMachineImagesOfVirtualizationTypeOnRegion(instanceType.getRequiredVirtualizationType().getId(), regionId);
            }
            else 
            {
                images = this.virtualMachineImageRepository_.listAllVirtualMachineImagesOfRegion(regionId);
            }
        }
        return images;
    }

    public InstanceType findInstanceTypeByName(String name)
    {
        checkState(!isNullOrEmpty(name));
        return this.instanceTypeRepository_.findInstanceTypeByName(name);
    }

    public Instances listRunningInstances(User owner)
    {
        checkNotNull(owner);
        checkNotNull(owner.getId());
        
        List<VirtualMachine> instances = this.instanceRepository_.listInstancesOfUserWithState(owner.getId(), InstanceStateType.RUNNING);
        
        for (VirtualMachine vm: instances)
        {
           completeInstanceState(vm);
        }
        
        return new Instances(instances);
    }
    
    private VirtualMachine completeInstanceState(final VirtualMachine vm)
    {
        if (vm != null)
        {
            vm.getTags().copyFrom(this.instanceTagRepository_.listTagsOfInstanceId(vm.getId()));
            vm.setOwner(this.userService_.findUserById(vm.getOwner().getId()));
            vm.getConfiguration().setCredentials(this.userService_.getUserProviderCredentials(vm.getOwner().getUsername(), 
                    vm.getType().getProvider().getName(), vm.getLocation().getName(), vm.getConfiguration().getKeyName()));
            
            if (vm.getConfiguration().getKeyPairs() == null)
            {
                vm.getConfiguration().setKeyPairs(vm.getOwner().getKeyPairs(vm.getConfiguration().getKeyName()));
            }
        }
        
        return vm;
    }
    
    public void updateMetadata(VirtualMachine instance, Tag ... tags)
    {
        checkArgument(instance != null);
        checkArgument(tags != null);
        
        UserProviderCredentials credential = this.userService_.getUserProviderCredentials(instance.getOwner().getUsername(), 
                instance.getType().getProvider().getName(), instance.getLocation().getName(), instance.getConfiguration().getKeyName());
        
        try
        {
            try (ComputeService service = ComputeServiceBuilder.builder().credentials(credential).build())
            {
                service.setTags(instance.getName(), Tags.newTags(tags));
            }
        }
        catch (IOException exception)
        {
            LOG.error("Error on updating instance [{}] tags. Error message [{}]", instance.getName(), exception.getMessage());
        }
    }
}
