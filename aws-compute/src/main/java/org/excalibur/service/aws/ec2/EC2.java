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
package org.excalibur.service.aws.ec2;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;

import static org.excalibur.core.cipher.TripleDESUtils.*;
import static org.excalibur.core.util.concurrent.DynamicExecutors.*;
import static org.excalibur.core.util.concurrent.Futures2.*;
import static org.excalibur.core.cloud.api.domain.Tags.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;

import org.apache.commons.codec.binary.Base64;
import org.excalibur.core.LoginCredentials;
import org.excalibur.core.Status;
import org.excalibur.core.cloud.api.Attribute;
import org.excalibur.core.cloud.api.HypervisorType;
import org.excalibur.core.cloud.api.InstanceStateDetails;
import org.excalibur.core.cloud.api.InstanceStateType;
import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.KeyPair;
import org.excalibur.core.cloud.api.KeyPairs;
import org.excalibur.core.cloud.api.OsArchitectureType;
import org.excalibur.core.cloud.api.Platform;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.VirtualMachineImage;
import org.excalibur.core.cloud.api.VirtualizationType;
import org.excalibur.core.cloud.api.VmConfiguration;
import org.excalibur.core.cloud.api.VolumeType;
import org.excalibur.core.cloud.api.compute.ComputeService;
import org.excalibur.core.cloud.api.domain.InstanceTemplate;
import org.excalibur.core.cloud.api.domain.InstanceTemplateStatus;
import org.excalibur.core.cloud.api.domain.Instances;
import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.cloud.api.domain.SpotInstanceOffer;
import org.excalibur.core.cloud.api.domain.SpotInstanceOfferResult;
import org.excalibur.core.cloud.api.domain.SpotInstanceOfferStateType;
import org.excalibur.core.cloud.api.domain.SpotInstanceOfferStatus;
import org.excalibur.core.cloud.api.domain.SpotPriceHistory;
import org.excalibur.core.cloud.api.domain.SpotPriceHistoryRequest;
import org.excalibur.core.cloud.api.domain.Tags;
import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.UserProviderCredentials;
import org.excalibur.core.util.BackoffLimitedRetryHandler;
import org.excalibur.core.util.Lists2;
import org.excalibur.core.util.Strings2;
import org.excalibur.core.util.SystemUtils2;
import org.excalibur.core.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.AmazonEC2AsyncClient;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.BlockDeviceMapping;
import com.amazonaws.services.ec2.model.CancelSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreatePlacementGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.CreateVolumeRequest;
import com.amazonaws.services.ec2.model.CreateVolumeResult;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstanceAttributeRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.DescribePlacementGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeRegionsRequest;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsResult;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryResult;
import com.amazonaws.services.ec2.model.EbsBlockDevice;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.ImportKeyPairRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceAttribute;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.LaunchSpecification;
import com.amazonaws.services.ec2.model.ModifyInstanceAttributeRequest;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.PlacementStrategy;
import com.amazonaws.services.ec2.model.RequestSpotInstancesRequest;
import com.amazonaws.services.ec2.model.RequestSpotInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;
import com.amazonaws.services.ec2.model.SpotInstanceType;
import com.amazonaws.services.ec2.model.SpotPrice;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.Volume;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListeningExecutorService;

public final class EC2 implements ComputeService, Closeable
{
    private static final Logger LOG = LoggerFactory.getLogger(EC2.class.getName());
    private final Object lock_ = new Object();
    
    private static final Function<Instance, String> INSTANCE_STRING_FUNCTION = new Function<Instance, String>()
    {
        @Override
        public String apply(Instance input)
        {
            return input.getInstanceId();
        }
    };

    private static final Function<List<Tag>, Map<String, Tag>> TAGS = new Function<List<Tag>, Map<String, Tag>>()
    {
        @Override
        @Nullable
        public Map<String, Tag> apply(@Nullable List<Tag> input)
        {
            Map<String, Tag> tags = Maps.newHashMap();

            for (Tag tag : input)
            {
                tags.put(tag.getKey(), tag);
            }

            return tags;
        }
    };
    
    private final Function<List<Image>, List<VirtualMachineImage>> IMAGES_FUNCTION = new Function<List<Image>, List<VirtualMachineImage>>()
    {
        @Override
        @Nullable
        public List<VirtualMachineImage> apply(@Nullable List<Image> input)
        {
            List<VirtualMachineImage> images = newArrayList();
            
            for (Image image: input)
            {
                VirtualMachineImage vmi = new VirtualMachineImage()
                        .setArchitecture(OsArchitectureType.valueOfFromValue(image.getArchitecture()))
                        .setDefaultUsername(DEFAULT_PLATFORM_USER_NAME)
                        .setHypervisor(HypervisorType.valueOfFromValue(image.getHypervisor()))
                        .setName(image.getImageId())
                        .setPlatform(image.getPlatform() == null ? Platform.LINUX : Platform.valueOfFromValue(image.getPlatform()))
                        .setRegion(credentials_.getRegion())
                        .setVirtualizationType(VirtualizationType.valueOfFromValue(image.getVirtualizationType()));
                
                if ("ebs".equals(image.getRootDeviceType()))
                {

                    EbsBlockDevice ebs = image.getBlockDeviceMappings().get(0).getEbs();

                    org.excalibur.core.cloud.api.Volume volume = new org.excalibur.core.cloud.api.Volume()
                            .setName(image.getRootDeviceName())
                            .setType(new VolumeType().setName(ebs.getVolumeType()))
                            .setSizeGb(ebs.getVolumeSize());
                    
                    vmi.setRootVolume(volume);
                }                
                
                images.add(vmi);
            }
            return images;
        }
    };
    
    public static final Region DEFAULT_API_REGION = new Region().setEndpoint("https://ec2.us-east-1.amazonaws.com").setName("us-east-1");

    private static final String DEFAULT_INSTANCE_NAME_TAG = "Name";
    private static final String DEFAULT_PLATFORM = "Linux";
    private static final String DEFAULT_PLATFORM_INSTANCE_USERNAME_TAG = "platform-username";

    private static final Integer ID = "ec2".hashCode();

    private static final String DEFAULT_PLATFORM_USER_NAME = System.getProperty("org.excalibur.default.platform.username", "ubuntu");
    private static final int MAX_RETRY_STATE = SystemUtils2.getIntegerProperty("org.excalibur.deployment.max.state.retry", 100);
    
    private static final Filter CONSIDERED_INSTANCE_STATES_FILTER = new Filter()
                     .withName("instance-state-name")
                     .withValues
                     (
                                 InstanceStateName.Running.toString(), 
                                 InstanceStateName.Stopped.toString(), 
                                 InstanceStateName.Stopping.toString()
                     );

    private final AmazonEC2Async ec2_;
    private final UserProviderCredentials credentials_;
    private final String defaultUserGroupName_;
    private final BackoffLimitedRetryHandler backoffLimitedRetryHandler_;

    private final AWSCredentials awsCredentials_;
    
    
    public EC2(UserProviderCredentials credentials)
    {
        this.credentials_ = checkNotNull(credentials);
        checkState(!isNullOrEmpty(credentials.getLoginCredentials().getCredentialName()));
        checkNotNull(credentials.getRegion());
        checkState(!isNullOrEmpty(credentials.getRegion().getName()));
        checkState(!isNullOrEmpty(credentials.getRegion().getEndpoint()));
        
        this.awsCredentials_ = new BasicAWSCredentials(credentials.getLoginCredentials().getIdentity(), 
                                                       credentials.getLoginCredentials().getCredential());
        
        ec2_ = new AmazonEC2AsyncClient(this.awsCredentials_);
        ec2_.setEndpoint(credentials.getRegion().getEndpoint());
        this.defaultUserGroupName_ = System.getProperty("org.excalibur.security.default.group.name", "excalibur-security-group");
        backoffLimitedRetryHandler_ = new BackoffLimitedRetryHandler();
        
    }

    @Override
    public Integer getId()
    {
        return ID;
    }

    @Override
    public Instances createInstances(final InstanceTemplate request)
    {
        return this.createInstances(request, true);
    }

    @Override
    public Instances createInstances(final InstanceTemplate template, final boolean waitForRunningState)
    {
        LoginCredentials credentials = template.getLoginCredentials();
        KeyPair keyPair = getKeyPair(checkNotNull(credentials.getCredentialName()));

        checkState(keyPair != null || (!isNullOrEmpty(credentials.getPublicKey())));

        if (keyPair == null)
        {
            String material = decrypt(credentials.getPublicKey());
            keyPair = new KeyPair().setKeyName(credentials.getCredentialName()).setKeyMaterial(material);
            keyPair.setKeyFingerprint(importKeyPair(keyPair)).setKeyMaterial(null);
        }
        
        checkState(keyPair != null);

        createSecurityGroupIfDoesNotExist(defaultUserGroupName_);
        
        final Image image = getImageById(template.getImageId());
        checkNotNull(image, String.format("Image %s does not exist", template.getImageId()));
        
        StringBuilder sb = new StringBuilder();
        sb.append("#start-data\n").append("#").append(credentials.getPrivateKey());
        
        if (!isNullOrEmpty(credentials.getPublicKey()))
        {
            sb.append("\n#").append(credentials.getPublicKey());
        }
        sb.append("\n#end-data");
        
        String userData = Strings2.nullAsEmpty(template.getUserData()).concat(new String(Base64.encodeBase64(sb.toString().getBytes())));

        RunInstancesRequest runInstacesRequest = new RunInstancesRequest()
                .withInstanceType(template.getInstanceType().getName())
                .withImageId(template.getImageId())
                .withMinCount(template.getMinCount())
                .withMaxCount(template.getMaxCount())
                .withKeyName(keyPair.getKeyName())
                .withSecurityGroups(defaultUserGroupName_)
                .withUserData(userData);

        Zone zone = null;
        
        final Placement placement = new Placement();
        
        if (template.getGroup() != null && !isNullOrEmpty(template.getGroup().getZone()))
        {
            zone = this.getZoneByName(template.getGroup().getZone());
            
            checkState(zone != null, String.format("Invalid zone name [%s] on region [%s]", 
                    template.getGroup().getZone(),  credentials_.getRegion().getName()));
            
            placement.withAvailabilityZone(zone.getName());
            
            if (!isNullOrEmpty(template.getGroup().getGroupName()))
            {
                placement.withGroupName(template.getGroup().getGroupName());
                this.createPlacementGroupsIfDoNotExist(template.getGroup());
            }
        }
        
        if (zone == null)
        {
            zone = Lists2.first(listAvailableZonesOfRegion(this.credentials_.getRegion()));
            placement.withAvailabilityZone(zone.getName());
        }
        
        checkNotNull(zone);
        checkState(!isNullOrEmpty(placement.getAvailabilityZone()));
        
        runInstacesRequest.withPlacement(placement);
        
        BlockDeviceMapping blockDeviceMapping = image.getBlockDeviceMappings().get(0);
        
        Integer diskSize = template.getDiskSize() == null ? SystemUtils2.getIntegerProperty("org.excalibur.amazon.default.disk.size", 30) : 
            template.getDiskSize();
        
        EbsBlockDevice disk = new EbsBlockDevice()
                .withSnapshotId(blockDeviceMapping.getEbs().getSnapshotId())
                .withVolumeSize(diskSize)
                .withVolumeType("gp2");
        
        runInstacesRequest.withBlockDeviceMappings(new BlockDeviceMapping()
                .withDeviceName(image.getRootDeviceName())
                .withEbs(disk));
        
        
        RunInstancesResult result = ec2_.runInstances(runInstacesRequest);
        template.setStatus(new InstanceTemplateStatus().setStatus(Status.SUCCESS));

        Iterable<Instance> ec2Instances = waitForRunningState ? waitForRunningInstacesState(result.getReservation().getInstances())
                : describeEC2Instances(result.getReservation().getInstances());

        CreateTagsRequest tagsRequest = new CreateTagsRequest();
        
        String instanceName = isNullOrEmpty(template.getInstanceName()) ? UUID.randomUUID().toString(): template.getInstanceName();
        
        tagsRequest.withTags(new Tag().withKey(DEFAULT_INSTANCE_NAME_TAG).withValue(instanceName));
        tagsRequest.withTags(new Tag().withKey(DEFAULT_PLATFORM_INSTANCE_USERNAME_TAG).withValue(DEFAULT_PLATFORM_USER_NAME));
        tagsRequest.withTags(new Tag().withKey("keyname").withValue(keyPair.getKeyName()));
        
        tagsRequest.withResources(Collections2.transform(newArrayList(ec2Instances), INSTANCE_STRING_FUNCTION));
        
        for (org.excalibur.core.cloud.api.domain.Tag tag: template.getTags())
        {
            if (!isNullOrEmpty(tag.getName()) && !isNullOrEmpty(tag.getValue()))
            {
                tagsRequest.withTags(new Tag().withKey(tag.getName()).withValue(tag.getValue()));
            }
        }

        ec2_.createTags(tagsRequest);
        
        if (template.getMaxCount() > 1)
        {
            
            for (int i = 0; i < result.getReservation().getInstances().size(); i++)
            {
                CreateTagsRequest request = new CreateTagsRequest();
                request.withResources(result.getReservation().getInstances().get(i).getInstanceId())
                           .withTags(new Tag().withKey(DEFAULT_INSTANCE_NAME_TAG).withValue(String.format("%s-%s", instanceName, i + 1)));
                ec2_.createTags(request);
            }
        }
        
//        return new Instances(toExcaliburInstances(ec2Instances, keyPair));
        Instances instances = this.describeInstances(ec2Instances);
        
        //
        LOG.debug("Waiting instances' ready state....");
        ThreadUtils.sleep(30 * 1000);
        
        LOG.debug("Created [{}] instance(s) from [{}/{}]", instances.size(), template.getMinCount(), template.getMaxCount());
        
        return instances;
    }

    // @Override
    public List<SpotInstanceOfferResult> createSpotInstanceOffer(SpotInstanceOffer request)
    {
        RequestSpotInstancesRequest spotRequest = new RequestSpotInstancesRequest(request.getOfferValue().toPlainString())
                .withValidFrom(request.getValidFrom())
                .withValidUntil(request.getValidUntil())
                .withType(SpotInstanceType.fromValue(request.getType().getName()))
                .withInstanceCount(request.getNumberOfInstances())
                .withLaunchSpecification(new LaunchSpecification()
                                .withInstanceType(request.getInstanceType().getName())
                                .withImageId(request.getImageId())
                                .withKeyName(request.getKeyName()));

        RequestSpotInstancesResult requestedSpotInstances = ec2_.requestSpotInstances(spotRequest);
        request.setStatus(new InstanceTemplateStatus().setStatus(Status.SUCCESS));

        return toSpotInstanceResults(request, requestedSpotInstances.getSpotInstanceRequests());
    }
    
    protected Volume createVolume(Zone zone, Integer sizeGb, String snapshotId)
    {
        CreateVolumeResult request = ec2_.createVolume(new CreateVolumeRequest()
                .withAvailabilityZone(zone.getName())
                .withSize(sizeGb)
                .withSnapshotId(snapshotId));
        
        return request.getVolume();
    }
    
    public void modifyInstanceAttributes(String instanceId, Attribute... attributes)
    {
        checkState(!isNullOrEmpty(instanceId));
        
//        InstanceState instanceState = 
//                ec2_.describeInstanceStatus(new DescribeInstanceStatusRequest().withInstanceIds(instanceId)).getInstanceStatuses().get(0).getInstanceState();
//        
//        final InstanceStateType stateType = InstanceStateType.valueOfFrom(instanceState.getName());
//        boolean isRunning = InstanceStateType.RUNNING.equals(stateType);
//        
//        if (isRunning)
//        {
            this.stop(instanceId);
//        }
        
        for (Attribute attribute : attributes)
        {
            if (attribute != null && !isNullOrEmpty(attribute.getName()))
            {
                ModifyInstanceAttributeRequest request = new ModifyInstanceAttributeRequest()
                        .withInstanceId(instanceId)
                        .withAttribute(attribute.getName());

                if (!isNullOrEmpty(attribute.getValue()))
                {
                    request.setValue(attribute.getValue());
                }
                
                ec2_.modifyInstanceAttribute(request);
            }
        }
        
//        if (isRunning)
//        {
            this.startInstances(instanceId);
//        }
    }

    // @Override
    public void cancelSpotInstanceOffers(String... spotInstanceOfferIds)
    {
        ec2_.cancelSpotInstanceRequests(new CancelSpotInstanceRequestsRequest().withSpotInstanceRequestIds(spotInstanceOfferIds));
    }

    // @Override
    public List<SpotInstanceOfferResult> getStatusOfSpotInstanceOffers(String... spotInstanceOfferIds)
    {
        DescribeSpotInstanceRequestsResult describeSpotInstanceResult = ec2_.describeSpotInstanceRequests(new DescribeSpotInstanceRequestsRequest()
                .withSpotInstanceRequestIds(spotInstanceOfferIds));

        return toSpotInstanceResults(null, describeSpotInstanceResult.getSpotInstanceRequests());
    }

    // @Override
    public List<SpotPriceHistory> getSpotPriceHistory(SpotPriceHistoryRequest request)
    {
        List<SpotPriceHistory> history = new ArrayList<SpotPriceHistory>();

        DescribeSpotPriceHistoryRequest spotPriceRequest = new DescribeSpotPriceHistoryRequest().withEndTime(request.getUntil())
                .withStartTime(request.getFrom()).withAvailabilityZone(request.getRegion() != null ? request.getRegion().getName() : null)
                .withMaxResults(request.getMaxResult()).withProductDescriptions(request.getImageTypes());

        List<InstanceType> types = request.getInstanceTypes();
        String[] instanceTypes = new String[types.size()];

        for (int i = 0; i < instanceTypes.length; i++)
        {
            instanceTypes[i] = types.get(i).getName();
        }

        spotPriceRequest.withInstanceTypes(instanceTypes);

        DescribeSpotPriceHistoryResult describeSpotPriceHistory = ec2_.describeSpotPriceHistory(spotPriceRequest);

        for (SpotPrice spot : describeSpotPriceHistory.getSpotPriceHistory())
        {
            history.add(new SpotPriceHistory().withInstanceType(new InstanceType().setName(spot.getInstanceType())).withPrice(spot.getSpotPrice())
                    .withImageTypeDescription(spot.getProductDescription()).withRegion(new Region(spot.getAvailabilityZone()))
                    .withTime(spot.getTimestamp()));
        }

        return history;
    }

    // @Override
    public KeyPair createKeyPair(String keyname)
    {
        checkState(!isNullOrEmpty(keyname));
        CreateKeyPairResult createKeyPair = ec2_.createKeyPair(new CreateKeyPairRequest().withKeyName(keyname));
        com.amazonaws.services.ec2.model.KeyPair keyPair = createKeyPair.getKeyPair();

        String material = cipher(keyPair.getKeyMaterial());
        checkState(isNullOrEmpty(material));
        return new KeyPair(keyPair.getKeyName()).withKeyFingerprint(keyPair.getKeyFingerprint()).withKeyMaterial(material);
    }

    @Override
    public String importKeyPair(final KeyPair keyPair)
    {
        synchronized (lock_)
        {
            String fingerprint;

            checkState(!isNullOrEmpty(keyPair.getKeyName()));
            checkState(!isNullOrEmpty(keyPair.getKeyMaterial()));

            KeyPair keyPair2 = getKeyPair(keyPair.getKeyName());

            if (keyPair2 == null)
            {
                fingerprint = new AmazonEC2Client(awsCredentials_).importKeyPair(new ImportKeyPairRequest(keyPair.getKeyName(), keyPair.getKeyMaterial())).getKeyFingerprint();
            }
            else
            {
                fingerprint = keyPair2.getKeyFingerprint();
            }
            
            return fingerprint;
        }
    }

    // @Override
    public List<KeyPair> getKeyPairs()
    {
        List<KeyPair> keyPairs = new ArrayList<KeyPair>();

        DescribeKeyPairsResult availableKeyPairs = ec2_.describeKeyPairs();

        for (KeyPairInfo keyInfo : availableKeyPairs.getKeyPairs())
        {
            keyPairs.add(new KeyPair(keyInfo.getKeyName()).withKeyFingerprint(keyInfo.getKeyFingerprint()));
        }

        return Collections.unmodifiableList(keyPairs);
    }

    // @Override
    public KeyPair getKeyPair(String keyName)
    {
        KeyPair keyPair = null;

        if (!isNullOrEmpty(keyName))
        {
            try
            {
                DescribeKeyPairsResult describeKeyPairs = ec2_.describeKeyPairs(new DescribeKeyPairsRequest().withKeyNames(keyName));
                List<KeyPairInfo> keyPairs = describeKeyPairs.getKeyPairs();

                if (keyPairs != null && !keyPairs.isEmpty())
                {
                    KeyPairInfo keyPairInfo = keyPairs.get(0);
                    keyPair = new KeyPair(keyPairInfo.getKeyName()).withKeyFingerprint(keyPairInfo.getKeyFingerprint());
                }
            }
            catch (AmazonClientException exception)
            {
                LOG.debug("Error on describing keyPairs [{}] on [{}]. Error message: [{}]",  keyName,  credentials_.getProvider().getName(),
                           exception.getMessage());
            }
        }

        return keyPair;
    }

    @Override
    public Instances listInstances()
    {
        DescribeInstancesRequest request = new DescribeInstancesRequest().withFilters(CONSIDERED_INSTANCE_STATES_FILTER);
        
        Iterable<Instance> ec2Instances = toEc2Instances(ec2_.describeInstances(request).getReservations());
        Iterable<VirtualMachine> excaliburInstances = toExcaliburInstances(ec2Instances, null);

        return new Instances(excaliburInstances);
    }
    
    @Override
    public VirtualMachine getInstanceWithName(String name, String zone)
    {
        Iterable<Instance> ec2Instances = toEc2Instances(ec2_.describeInstances(new DescribeInstancesRequest().withInstanceIds(name)).getReservations());
        List<VirtualMachine> instances = newArrayList(toExcaliburInstances(ec2Instances, null));
        
        return Lists2.first(instances);
    }
    
    @Override
    public Instances aggregateInstances()
    {
        final Instances instances = new Instances();

        for (final Region region : this.listRegions())
        {
            AmazonEC2Client client = new AmazonEC2Client(awsCredentials_);
            client.setEndpoint(region.getEndpoint());
            DescribeInstancesRequest request = new DescribeInstancesRequest().withFilters(CONSIDERED_INSTANCE_STATES_FILTER);
            instances.addInstances(toExcaliburInstances(toEc2Instances(client.describeInstances(request).getReservations()), null));
        }
        return instances;
    }

    @Override
    public List<Region> listRegions()
    {
        List<Region> regions = newArrayList();

        for (com.amazonaws.services.ec2.model.Region region : new AmazonEC2Client(awsCredentials_).describeRegions().getRegions())
        {
            regions.add(new Region().setEndpoint(region.getEndpoint()).setName(region.getRegionName()));
        }
        
        Collections.sort(regions);
        return ImmutableList.copyOf(regions);
    }
    
    public List<VirtualMachineImage> listImages(OsArchitectureType architecture, Platform platform)
    {
        return this.listImages(architecture, platform, null, null);
    }
    
    public List<VirtualMachineImage> listImages(OsArchitectureType architecture, Platform platform, HypervisorType hypervisor,
            VirtualizationType virtualizationType, String... imageIds)
    {
        checkArgument(architecture != null);
        
        List<VirtualMachineImage> images = newArrayList();
        
        DescribeImagesRequest request = new DescribeImagesRequest();
        request.withFilters(new Filter().withName("state").withValues("available"),
                            new Filter().withName("architecture").withValues(architecture.name().toLowerCase()),
                            new Filter().withName("image-type").withValues("machine"));
        
        if (platform != null)
        {
            request.withFilters(new Filter().withName("platform").withValues(platform.getValue()));
        }
        
        if (hypervisor != null)
        {
            request.withFilters(new Filter().withName("hypervisor").withValues(hypervisor.getValue()));
        }
        
        if (virtualizationType != null)
        {            
            request.withFilters(new Filter().withName("virtualization-type").withValues(hypervisor.getValue()));
        }
        
        if (imageIds != null && imageIds.length > 0)
        {
            request.withImageIds(imageIds);
        }
        
        
        try
        {
            images = IMAGES_FUNCTION.apply(ec2_.describeImages(request).getImages());
        }
        catch (AmazonClientException exception)
        {
            LOG.error(exception.getMessage(), exception);
        }
        
        return images;
    }
    
    public List<VirtualMachineImage> listImages(String ... imageIds)
    {
        checkNotNull(imageIds);
        checkArgument(imageIds.length >= 1);
        
        List<Image> images = newArrayList();
        
        try
        {
            images = ec2_.describeImages(new DescribeImagesRequest().withImageIds(imageIds)).getImages();
        }
        catch (AmazonClientException exception)
        {
            LOG.error(exception.getMessage());
        }
        
        return IMAGES_FUNCTION.apply(images);
    }

    public Region getRegionByName(String regionName)
    {
        try
        {
            DescribeRegionsResult regionResult = ec2_.describeRegions(new DescribeRegionsRequest().withRegionNames(regionName));
            com.amazonaws.services.ec2.model.Region region = org.excalibur.core.util.Lists2.first(regionResult.getRegions());
            return new Region().setEndpoint(region.getEndpoint()).setName(region.getRegionName());
        }
        catch (AmazonClientException exception)
        {
            LOG.error("Invalid region name [{}]. Message error: [{}]", regionName, exception.getMessage());
            
            return null;
        }
    }
    
    public Image getImageById(String imageId)
    {
        Image image = null;
        try
        {
            DescribeImagesResult describeImages = ec2_.describeImages(new DescribeImagesRequest().withImageIds(imageId));
            image = Lists2.first(describeImages.getImages());
        }
        catch (com.amazonaws.AmazonServiceException exception)
        {
            LOG.error("Error on describing image: [{}]. Error message: [{}]", imageId, exception.getMessage(), exception);
        }
        return image;
    }
    
    private Zone getZoneByName(String zoneName)
    {
        checkState(!isNullOrEmpty(zoneName));
        
        try
        {
            DescribeAvailabilityZonesResult zones = ec2_
                    .describeAvailabilityZones(new DescribeAvailabilityZonesRequest()
                    .withZoneNames(zoneName)
                    .withFilters(new Filter().withName("region-name").withValues(credentials_.getRegion().getName())));

            if (zones != null && zones.getAvailabilityZones().size() == 1)
            {
                //available | impaired | unavailable
                AvailabilityZone availabilityZone = zones.getAvailabilityZones().get(0);
                return new Zone().setName(availabilityZone.getZoneName()).setRegion(credentials_.getRegion()).setStatus(availabilityZone.getState());
            }

        }
        catch (AmazonClientException exception)
        {
            LOG.debug("Invalid zone [{}]! Error message: [{}]", zoneName, exception.getMessage(), exception);
        }
        
        return null;
    }
    
    private List<Zone> listAvailableZonesOfRegion(Region region)
    {
        checkNotNull(region);
        checkState(!isNullOrEmpty(region.getName()));
        checkState(!isNullOrEmpty(region.getEndpoint()));

        List<Zone> zones = newArrayList();
        
        AmazonEC2Client client = new AmazonEC2Client(awsCredentials_);
        client.setEndpoint(region.getEndpoint());

        try
        {
            DescribeAvailabilityZonesResult availabilityZonesResult = client.describeAvailabilityZones(new DescribeAvailabilityZonesRequest()
                    .withFilters(new com.amazonaws.services.ec2.model.Filter().withName("region-name").withValues(region.getName()),
                                 new com.amazonaws.services.ec2.model.Filter().withName("state").withValues("available"))
                                );

            for (AvailabilityZone zone : availabilityZonesResult.getAvailabilityZones())
            {
                zones.add(new Zone().setName(zone.getZoneName()).setRegion(region));
            }

            Collections.sort(zones);
        }
        catch (AmazonClientException exception)
        {
            LOG.warn("Error on listing the available zones of region [name:{}, endpoint:{}]. Error message: [{}]", 
                    region.getName(), region.getEndpoint(), exception.getMessage(), exception);
        }

        return zones;
    }

    public void createPlacementGroups(org.excalibur.core.cloud.api.Placement... groups)
    {
        createPlacementGroupsIfDoNotExist(groups);
    }

    public void createPlacementGroupsIfDoNotExist(org.excalibur.core.cloud.api.Placement... groups)
    {
        if (groups != null)
        {
            ListeningExecutorService executor = newListeningDynamicScalingThreadPool(String.format("create-groups-%s", credentials_
                    .getRegion().getName()));
            
            List<Callable<Void>> tasks = newArrayList();

            for (final org.excalibur.core.cloud.api.Placement placement : groups)
            {
                tasks.add(new Callable<Void>()
                {
                    @Override
                    public Void call() throws Exception
                    {
                        if (placement != null && !isNullOrEmpty(placement.getGroupName()))
                        {
                            try
                            {
                                new AmazonEC2Client(awsCredentials_).describePlacementGroups(new DescribePlacementGroupsRequest().withGroupNames(placement.getGroupName()));
                            }
                            catch (AmazonClientException exception)
                            {
                                LOG.debug("The group {} is unknown! Provider message: {}", placement.getGroupName(), exception.getMessage());
                                ec2_.createPlacementGroup(new CreatePlacementGroupRequest()
                                    .withGroupName(placement.getGroupName()).withStrategy(PlacementStrategy.Cluster));
                            }
                        }
                        return null;
                    }
                });
            }
            
            invokeAllAndShutdownWhenFinish(tasks, executor);
        }
    }

    public void createSecurityGroupIfDoesNotExist(String groupName)
    {
        checkState(!isNullOrEmpty(groupName));
        
        List<SecurityGroup> groups = newArrayList();

        try
        {
            LOG.debug("checking if the security group [{}] already exists on region [{}].", groupName, DEFAULT_API_REGION.getName());
            groups = ec2_.describeSecurityGroups(new DescribeSecurityGroupsRequest().withGroupNames(groupName)).getSecurityGroups();
        }
        catch (AmazonServiceException exception)
        {
            LOG.debug("The security group {} does not already exist on region {}.", groupName, DEFAULT_API_REGION.getName());
        }

        if (groups.isEmpty())
        {
            LOG.debug("Creating the security group [{}] on region [{}].", groupName, DEFAULT_API_REGION.getName());
            
            CreateSecurityGroupResult createSecurityGroup = ec2_.createSecurityGroup(new CreateSecurityGroupRequest()
                    .withGroupName(groupName)
                    .withDescription("default-app-group"));
            
            LOG.debug("The security group [{}] was created on region [{}], and its id is [{}]", groupName, 
                    DEFAULT_API_REGION.getName(), 
                    createSecurityGroup.getGroupId());
            
            authorizeTcpAndSshIngressTraffic(groupName);
        }
    }

    protected void authorizeTcpAndSshIngressTraffic(String groupName)
    {
        LOG.debug("Adding a TCP ingress rule for the security group [{}].", groupName);
        
        AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest = new AuthorizeSecurityGroupIngressRequest()
                .withFromPort(0)
                .withToPort(65535)
                .withIpProtocol("tcp")
                .withGroupName(groupName)
                .withCidrIp("0.0.0.0/0");
        
        ec2_.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);
        
        LOG.debug("The following ingress rule was created. Security group [{}], protocol [{}] from port [{}] to port [{}] and " +
        		"CIDR IP address [{}], region [{}]",
                authorizeSecurityGroupIngressRequest.getGroupName(),
                authorizeSecurityGroupIngressRequest.getIpProtocol(),
                authorizeSecurityGroupIngressRequest.getFromPort(), 
                authorizeSecurityGroupIngressRequest.getToPort(),
                authorizeSecurityGroupIngressRequest.getCidrIp(),
                DEFAULT_API_REGION.getName());
        
        authorizeSecurityGroupIngressRequest.withFromPort(22).withToPort(22);
        ec2_.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);
        
    }
    

    @Override
    public void stop(VirtualMachine vm)
    {
        if (vm != null)
        {
            this.stop(vm.getName());
        }
    }
    
    @Override
    public void stop(Instances instances)
    {
      if (instances != null)
      {
          this.stop(instances.instancesName());
      }
    }

    @Override
    public void stop(final String... instanceIds)
    {
        if (instanceIds != null && instanceIds.length > 0)
        {
            final long startTime = System.nanoTime();
            
            StopInstancesResult stopInstancesResult = ec2_.stopInstances(new StopInstancesRequest().withInstanceIds(instanceIds));
            List<Callable<org.excalibur.service.aws.ConfigurationService.InstanceStateType>> tasks = newArrayList();
            
            for (final InstanceStateChange state: stopInstancesResult.getStoppingInstances())
            {
                tasks.add(new Callable<org.excalibur.service.aws.ConfigurationService.InstanceStateType>()
                {
                    BackoffLimitedRetryHandler backoffLimitedRetryHandler = new BackoffLimitedRetryHandler();
                    
                    @Override
                    public org.excalibur.service.aws.ConfigurationService.InstanceStateType call() throws Exception
                    {
                        InstanceState currentState = state.getCurrentState();
                        org.excalibur.service.aws.ConfigurationService.InstanceStateType finalState = 
                                org.excalibur.service.aws.ConfigurationService.InstanceStateType.STOPPED;
                        
                        int failureCount = 0;

                        do
                        {
                            if ("stopping".equalsIgnoreCase(currentState.getName()))
                            {
                                backoffLimitedRetryHandler.imposeBackoffExponentialDelay(1000, 2, failureCount++, 1000, String.format("waiting instance%:s", state.getInstanceId()));
                                
                                currentState = ec2_
                                        .describeInstanceStatus(new DescribeInstanceStatusRequest().withInstanceIds(state.getInstanceId()))
                                        .getInstanceStatuses().get(0).getInstanceState();
                            }
                        } while (!finalState.getValue().equalsIgnoreCase(currentState.getName()));
                        
                        System.out.println(state.getInstanceId() + "=" + (System.nanoTime() - startTime));

                        return finalState;
                    }
                });
            }
            
            invokeAll(tasks);
            
            long end = System.nanoTime() - startTime;
            System.out.println(end);
        }
    }
    
    public void startInstances(String ... instanceIds)
    {
        LOG.info("Starting up the instances [{}]", Arrays.toString(instanceIds));
        List<InstanceStateChange> startingInstances = ec2_.startInstances(new StartInstancesRequest().withInstanceIds(instanceIds)).getStartingInstances();
        
        for (InstanceStateChange state: startingInstances)
        {
            LOG.info("The state of the instance [{}] changed from [{}] to [{}]", 
                    state.getInstanceId(), 
                    state.getPreviousState().getName(), 
                    state.getCurrentState().getName());
        }
//        waitForEc2Instance(instanceToWaitRunningState);
    }

    @Override
    public void terminateInstances(String... instanceIds)
    {
        ec2_.terminateInstancesAsync(new TerminateInstancesRequest().withInstanceIds(instanceIds));
    }
    
    @Override
    public void terminateInstance(VirtualMachine instance)
    {
        if (instance != null && isNullOrEmpty(instance.getName()))
        {
            this.terminateInstances(instance.getName());
        }
    }
    
    @Override
    public void terminateInstances(Instances instances)
    {
        this.terminateInstances(Iterables.toArray(instances.getInstanceNames(), String.class));
    }
    
    protected Instances describeInstances(Iterable<Instance> ec2Instances)
    {
        List<String> instanceIds = new ArrayList<String>();
        
        for (Instance instance: ec2Instances)
        {
            instanceIds.add(instance.getInstanceId());
        }
        
        return this.describeInstances(instanceIds);
    }

    public Instances describeInstances(final String... instanceIds)
    {
        return this.describeInstances(Arrays.asList(instanceIds));
    }

    public Instances describeInstances(final Collection<String> instanceIds)
    {
        return describeInstances(new DescribeInstancesRequest().withInstanceIds(instanceIds));
    }

    @Override
    public Instances listInstancesWithTags(Tags tags)
    {
        Instances instances = new Instances ();
        
        if (tags != null && !tags.isEmpty())
        {
            DescribeInstancesRequest request = new DescribeInstancesRequest();
            
            for (org.excalibur.core.cloud.api.domain.Tag tag: tags)
            {
                if (!isNullOrEmpty(tag.getName()))
                {
                    request.withFilters(new Filter().withName("tag-key").withValues(tag.getName()));
                }
                
                if (!isNullOrEmpty(tag.getValue()))
                {
                    request.withFilters(new Filter().withName("tag-value").withValues(tag.getValue()));
                }
            }
            
           instances = describeInstances(request);
        }
        
        return instances;
    }
    
    @Override
    public Instances listInstancesWithTags(org.excalibur.core.cloud.api.domain.Tag ... tags)
    {
       return this.listInstancesWithTags(Tags.newTags(tags));
    }
    
    
    @Override
    public void setTag(String instanceId, org.excalibur.core.cloud.api.domain.Tag tag)
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
        List<Tag> awsTags = newArrayList();
        
        for (org.excalibur.core.cloud.api.domain.Tag tag: tags)
        {
            awsTags.add(new Tag(tag.getName(), tag.getValue()));
        }

        this.ec2_.createTags(new CreateTagsRequest().withResources(toArray(instanceIds, String.class)).withTags(awsTags));
    }
    
    private Instances describeInstances(DescribeInstancesRequest request)
    {
        final Instances instances = new Instances();

        DescribeInstancesResult result = ec2_.describeInstances(request);

        for (Reservation reservation : result.getReservations())
        {
            for (Instance instance : reservation.getInstances())
            {
                instances.addInstance(toExcaliburInstance(instance, null));
            }
        }

        return instances;
    }

    private List<SpotInstanceOfferResult> toSpotInstanceResults(SpotInstanceOffer request, List<SpotInstanceRequest> spotInstanceRequests)
    {
        List<SpotInstanceOfferResult> resultList = new ArrayList<SpotInstanceOfferResult>();

        for (SpotInstanceRequest spotInstanceResult : spotInstanceRequests)
        {
            SpotInstanceOfferResult result = new SpotInstanceOfferResult().setCreateTime(new Date()).setOfferRequest(request);

            result.setState(SpotInstanceOfferStateType.valueOfFrom(spotInstanceResult.getState()));
            result.setSpotRequestId(spotInstanceResult.getSpotInstanceRequestId());

            result.setStatus(new SpotInstanceOfferStatus()
                    .setCode(spotInstanceResult.getStatus().getCode())
                    .setMessage(spotInstanceResult.getStatus().getMessage())
                    .setUpdateTime(spotInstanceResult.getStatus().getUpdateTime()));
            
            resultList.add(result);
        }

        return resultList;
    }

    private Instance describeEC2Instance(String instanceId)
    {
        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest().withInstanceIds(instanceId);
        DescribeInstancesResult describeInstances = ec2_.describeInstances(describeInstancesRequest);
        Instance instance = getOnlyElement(toEc2Instances(describeInstances.getReservations()));

        return instance;
    }

    private Iterable<Instance> describeEC2Instances(Iterable<Instance> instancesToDescribe)
    {
        String [] instanceIds = toArray(transform(instancesToDescribe, INSTANCE_STRING_FUNCTION), String.class);
        return this.describeEC2Instances(instanceIds);
    }
    
    private Iterable<Instance> describeEC2Instances(String ... instanceIds)
    {
        DescribeInstancesResult describeInstances = ec2_.describeInstances(new DescribeInstancesRequest().withInstanceIds(instanceIds));
        return toEc2Instances(describeInstances.getReservations());
    }

    private VirtualMachine toExcaliburInstance(Instance instance, KeyPair keyPair)
    {
        Map<String, Tag> tags = TAGS.apply(instance.getTags());

        VirtualMachine vm = new VirtualMachine()
                .setName(instance.getInstanceId())
                .setImageId(instance.getImageId())
                .setType(InstanceType.valueOf(instance.getInstanceType()).setProvider(this.credentials_.getProvider()))
                .setState(new InstanceStateDetails(InstanceStateType.valueOfFrom(instance.getState().getName()), new Date())) //TODO we need to improve this
                .setConfiguration(
                        new VmConfiguration()
                                .setKeyName(instance.getKeyName())
                                .setKeyPairs(new KeyPairs().setPrivateKey(keyPair))
                                .setPlatform(isNullOrEmpty(instance.getPlatform()) ? DEFAULT_PLATFORM : instance.getPlatform())
                                .setPlatformUserName(tags.get(DEFAULT_PLATFORM_INSTANCE_USERNAME_TAG) != null ? 
                                        tags.get(DEFAULT_PLATFORM_INSTANCE_USERNAME_TAG).getValue() : 
                                        System.getProperty("org.excalibur.default.platform.username"))
                                .setPrivateIpAddress(instance.getPrivateIpAddress()).setPublicIpAddress(instance.getPublicIpAddress())
                                .setPublicDnsName(instance.getPublicDnsName())).setLaunchTime(instance.getLaunchTime())
//                .setLocation(new Region().setName(instance.getPlacement().getAvailabilityZone()))
//                .setLocation(credentials_.getRegion())
                .setLocation(new Zone().setName(instance.getPlacement().getAvailabilityZone()).setRegion(credentials_.getRegion()))                
                .setPlacement(new org.excalibur.core.cloud.api.Placement()
                                .setGroupName(instance.getPlacement().getGroupName())
                                .setZone(instance.getPlacement().getAvailabilityZone()))
                .setOwner(new User(this.credentials_.getUserId()).setUsername(tags.get("username") != null ? tags.get("username").getValue(): null));
        
        if (tags.containsKey("keyname"))
        {
            if (vm.getConfiguration().getKeyPairs().getPrivateKey() == null)
            {
                vm.getConfiguration().getKeyPairs().setPrivateKey(new KeyPair());
            }
            
            vm.getConfiguration().getKeyPairs().getPrivateKey().setKeyName(tags.get("keyname").getValue());
        }
        else
        {
            return null;
        }
        
        InstanceAttribute attribute = new AmazonEC2Client(awsCredentials_).describeInstanceAttribute(
                new DescribeInstanceAttributeRequest().withInstanceId(instance.getInstanceId()).withAttribute("userData")).getInstanceAttribute();
        
//        List<InstanceStatus> instanceStatuses = new AmazonEC2Client(awsCredentials_).describeInstanceStatus(
//                new DescribeInstanceStatusRequest().withInstanceIds(instance.getInstanceId())).getInstanceStatuses();

        if (!isNullOrEmpty(attribute.getUserData()))
        {
            String userData = new String(Base64.decodeBase64(attribute.getUserData().getBytes()));
            int i = userData.indexOf("#start-data"), f = userData.indexOf("#end-data");
            
            if (i > -1 && f > -1)
            {
                String[] keys = userData.substring(i, f).split("#");
                checkState(keys.length == 4);
                
                vm.getConfiguration().getKeyPairs().getPrivateKey().setKeyMaterial(keys[2]);
                vm.getConfiguration().getKeyPairs()
                        .setPublicKey(new KeyPair().setKeyName(tags.get("keyname").getValue()).setKeyMaterial(keys[3].trim()));
            }
            
            vm.setUserData(attribute.getUserData());
        } 
        
        for (Tag tag: tags.values())
        {
            vm.getTags().add(org.excalibur.core.cloud.api.domain.Tag.valueOf(tag.getKey(), tag.getValue()));
        }
        
        return vm;

    }

    /**
     * Converts a {@link Collection} of {@link Reservation} into a {@link Iterable} of their underlying instances.
     * 
     * @param reservations
     * @return
     */
    private Iterable<Instance> toEc2Instances(Iterable<Reservation> reservations)
    {
        Iterable<List<Instance>> listOfInstances = transform(reservations, new Function<Reservation, List<Instance>>()
        {
            @Override
            public List<Instance> apply(Reservation reservation)
            {
                return reservation.getInstances();
            }
        });
        
        return concat(listOfInstances);
    }

    private Iterable<VirtualMachine> toExcaliburInstances(Iterable<Instance> instances, KeyPair keyPair)
    {
        List<VirtualMachine> listOfInstances = newArrayList();

        for (Instance instance : instances)
        {
            VirtualMachine vm = toExcaliburInstance(instance, keyPair);
            if (vm != null)
            {
                listOfInstances.add(vm);
            }
        }

        return listOfInstances;
    }

    private List<Instance> waitForRunningInstacesState(Iterable<Instance> instances)
    {
        List<Instance> readyInstances = newArrayList();
        for (Instance instance : instances)
        {
            Instance runningInstance = waitForInstanceRunningState(instance);
            if (runningInstance != null)
            {
                readyInstances.add(runningInstance);
            }
        }

        return readyInstances;
    }

    /**
     * <p>
     * Wait for the ec2 instances starting up and returns it up to date.
     * </p>
     * <p>
     * Note: some information are missing of the {@link Instance} returned by {@link AmazonEC2#describeInstances(DescribeInstancesRequest)} as long as
     * the instance is not "running" (e.g., {@link Instance#getPublicDnsName()}).
     * </p>
     * 
     * @param instanceToWaitRunningState
     * @return up to date instances or <code>null</code> if the instance crashed at startup.
     */
    private Instance waitForInstanceRunningState(final Instance instanceToWaitRunningState)
    {
        Instance instance = instanceToWaitRunningState;

        int counter = 0;
        
        while (InstanceStateName.Pending.name().equalsIgnoreCase(instance.getState().getName())) //|| (instance.getPublicIpAddress() == null) || (instance.getPublicDnsName() == null)
        {
            String description = String.format("Waiting running state of the instance [%s]: [%s]", instance.getInstanceId(), instance);
            
            backoffForAttempt(counter + 1, description);

            instance = describeEC2Instance(instance.getInstanceId());
            counter++;

            if (counter >= MAX_RETRY_STATE)
            {
                LOG.warn("Timeout waiting for startup of [{}]: [{}]", instance.getInstanceId(), instanceToWaitRunningState);
                return instanceToWaitRunningState;
            }
        }

        if (InstanceStateName.ShuttingDown.equals(instance.getState()) || InstanceStateName.Terminated.equals(instance.getState()))
        {
            LOG.warn("Terminating and skipping dying instance [{}] (stateReason=[{}], stateTransitionReason=[{}]): [{}]",
                    instance.getInstanceId(), instance.getStateReason(), instance.getStateTransitionReason(), instance);

            this.terminateInstances(instance.getInstanceId());
            return null;
        }

        LOG.debug("Instance {} is running", instance.getInstanceId());
        return instance;
    }
    
    private void backoffForAttempt(int retryAttempt, String message)
    {
        backoffLimitedRetryHandler_.imposeBackoffExponentialDelay(300L, 2, retryAttempt, MAX_RETRY_STATE, message);
    }

    @Override
    public String getName()
    {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getDescription()
    {
        return "Amazon EC2 Driver";
    }

    @Override
    public void close() throws IOException
    {
        this.ec2_.shutdown();
    }
    
    @Override
    protected void finalize() throws Throwable
    {
        close();
        super.finalize();
    }

    
    
//    public static void main(String[] args) throws IOException
//    {
//        String privateKey = IOUtils2.readLines(new File(SystemUtils2.getUserDirectory(), "/.ec2/leite.pem"));
//        Properties properties = Properties2.load(ClassUtils.getDefaultClassLoader().getResourceAsStream("aws-config.properties"));
//        
//        LoginCredentials loginCredentials = new LoginCredentials.Builder()
//                .identity(properties.getProperty("aws.access.key"))
//                .credential(properties.getProperty("aws.secret.key"))
//                .credentialName("leite")
//                .build();
//        
//        UserProviderCredentials userProviderCredentials = new UserProviderCredentials()
//                .setLoginCredentials(loginCredentials)
//                .setRegion(new Region("us-east-1").setEndpoint("https://ec2.us-east-1.amazonaws.com"));
//        
//        EC2 ec2 = new EC2(userProviderCredentials);
//        
//        InstanceTemplate template = new InstanceTemplate()
//                .setImageId("ami-1d8c9574")
//                .setInstanceType(InstanceType.valueOf("c3.8xlarge"))
//                .setKeyName("leite")
//                .setLoginCredentials(loginCredentials.toBuilder().privateKey(privateKey).build())
//                .setGroup(new org.excalibur.core.cloud.api.Placement().setGroupName("iperf-bench").setZone("us-east-1a"))
//                .setMinCount(1)
//                .setMaxCount(1)
//                .setInstanceName("iperf-server")
//                .setRegion(userProviderCredentials.getRegion())
//                .setTags(Tags.newTags(new org.excalibur.core.cloud.api.domain.Tag("benchmark", "iperf")));
//        
////        Instances instances = ec2.createInstances(template);
////        System.out.println(instances.first().orNull());
//        
//        ec2.modifyInstanceAttributes("i-d8e09ff2", new Attribute("sriovNetSupport").setValue("simple"));
//                
//        ec2.close();
//    }

}
