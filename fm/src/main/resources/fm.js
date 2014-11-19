/*
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
scope({c0_CENTOS:0, c0_DEBIAN:0, c0_Feature:5, c0_HIGH:0, c0_Instance:0, c0_LINUX:0, c0_LOW:0, c0_MODERATE:0, c0_NetworkingPerformance:0, c0_OperatingSystem:0, c0_Platform:0, c0_REDHAT:0, c0_SUSE:0, c0_TEN_GIGABITS:0, c0_UBUNTU:0, c0_VERY_LOW:0, c0_WINDOWS:0, c0_WINDOWS_2008_SERVER:0, c0_billing:0, c0_cost:5, c0_image:0, c1_Features:0, c1_cost:0, c1_type:0});
defaultScope(1);
intRange(-8, 7);

c0_Feature = Abstract("c0_Feature");
c0_Memory = Abstract("c0_Memory");
c0_VolumeType = Abstract("c0_VolumeType");
c0_Storage = Abstract("c0_Storage");
c0_Zone = Abstract("c0_Zone");
c0_Architecture = Abstract("c0_Architecture");
c0_Platform = Abstract("c0_Platform");
c0_HypervisorType = Abstract("c0_HypervisorType");
c0_VirtualizationType = Abstract("c0_VirtualizationType");
c0_PhysicalProcessor = Abstract("c0_PhysicalProcessor");
c0_Location = Abstract("c0_Location");
c0_ComputeUnit = Abstract("c0_ComputeUnit");
c0_HardwareConfiguration = Abstract("c0_HardwareConfiguration");
c0_Provider = Abstract("c0_Provider");
c0_OperatingSystem = Abstract("c0_OperatingSystem");
c0_BillingType = Abstract("c0_BillingType");
c0_Instance = Abstract("c0_Instance");
c0_Image = Abstract("c0_Image").extending(c0_Feature);
c0_InstanceType = Abstract("c0_InstanceType").extending(c0_Feature);
c0_NetworkingPerformance = Abstract("c0_NetworkingPerformance").extending(c0_Feature);
c0_Features = Abstract("c0_Features").extending(c0_Feature);
c0_cost = c0_Feature.addChild("c0_cost").withCard(1, 1);
c0_size = c0_Memory.addChild("c0_size").withCard(1, 1);
c0_STANDARD = c0_VolumeType.addChild("c0_STANDARD").withCard(0, 1);
c0_PROVISIONED = c0_VolumeType.addChild("c0_PROVISIONED").withCard(0, 1);
c1_size = c0_Storage.addChild("c1_size").withCard(1, 1);
c0_type = c0_Storage.addChild("c0_type").withCard(1, 1);
c0_StorageType = c0_Storage.addChild("c0_StorageType").withCard(1, 1).withGroupCard(1, 1);
c0_SSD = c0_Storage.addChild("c0_SSD").withCard(1, 1);
c0_EBS = c0_Storage.addChild("c0_EBS").withCard(1, 1);
c0_Status = c0_Zone.addChild("c0_Status").withCard(1, 1).withGroupCard(1, 1);
c0_UP = c0_Status.addChild("c0_UP").withCard(0, 1);
c0_DOWN = c0_Status.addChild("c0_DOWN").withCard(0, 1);
c0_x32 = c0_Architecture.addChild("c0_x32").withCard(0, 1);
c0_x64 = c0_Architecture.addChild("c0_x64").withCard(0, 1);
c0_LINUX = c0_Platform.addChild("c0_LINUX").withCard(0, 1);
c0_WINDOWS = c0_Platform.addChild("c0_WINDOWS").withCard(0, 1);
c0_OVM = c0_HypervisorType.addChild("c0_OVM").withCard(0, 1);
c0_XEN = c0_HypervisorType.addChild("c0_XEN").withCard(0, 1);
c0_HVM = c0_VirtualizationType.addChild("c0_HVM").withCard(0, 1);
c0_PARAVIRTUAL = c0_VirtualizationType.addChild("c0_PARAVIRTUAL").withCard(0, 1);
c0_IntelOpteron = c0_PhysicalProcessor.addChild("c0_IntelOpteron").withCard(0, 1);
c0_IntelXeon = c0_PhysicalProcessor.addChild("c0_IntelXeon").withCard(0, 1);
c0_IntelSandyBridge = c0_PhysicalProcessor.addChild("c0_IntelSandyBridge").withCard(0, 1);
c0_IntelIvyBridge = c0_PhysicalProcessor.addChild("c0_IntelIvyBridge").withCard(0, 1);
c0_zones = c0_Location.addChild("c0_zones").withCard(1, 1);
c0_speed = c0_ComputeUnit.addChild("c0_speed").withCard(1, 1);
c0_number = c0_ComputeUnit.addChild("c0_number").withCard(1, 1);
c0_processor = c0_ComputeUnit.addChild("c0_processor").withCard(1, 1).withGroupCard(1, 1).extending(c0_PhysicalProcessor);
c0_memory = c0_HardwareConfiguration.addChild("c0_memory").withCard(1, 1).extending(c0_Memory);
c0_computeUnit = c0_HardwareConfiguration.addChild("c0_computeUnit").withCard(1, 1).extending(c0_ComputeUnit);
c0_storage = c0_HardwareConfiguration.addChild("c0_storage").withCard(1, 1).extending(c0_Storage);
c0_locations = c0_Provider.addChild("c0_locations").withCard(0, 1);
c0_priceTypes = c0_Provider.addChild("c0_priceTypes").withCard(0, 1);
c0_CENTOS = c0_OperatingSystem.addChild("c0_CENTOS").withCard(0, 1);
c0_DEBIAN = c0_OperatingSystem.addChild("c0_DEBIAN").withCard(0, 1);
c0_REDHAT = c0_OperatingSystem.addChild("c0_REDHAT").withCard(0, 1);
c0_SUSE = c0_OperatingSystem.addChild("c0_SUSE").withCard(0, 1);
c0_UBUNTU = c0_OperatingSystem.addChild("c0_UBUNTU").withCard(0, 1);
c0_WINDOWS_2008_SERVER = c0_OperatingSystem.addChild("c0_WINDOWS_2008_SERVER").withCard(0, 1);
c0_architecture = c0_Image.addChild("c0_architecture").withCard(1, 1);
c0_location = c0_Image.addChild("c0_location").withCard(1, 1);
c0_hypervisor = c0_Image.addChild("c0_hypervisor").withCard(1, 1);
c0_virtualization = c0_Image.addChild("c0_virtualization").withCard(1, 1);
c0_hardware = c0_InstanceType.addChild("c0_hardware").withCard(1, 1);
c0_provider = c0_InstanceType.addChild("c0_provider").withCard(1, 1);
c0_PurposeUsage = c0_InstanceType.addChild("c0_PurposeUsage").withCard(1, 1).withGroupCard(1, 1);
c0_BOOTSTRAP = c0_PurposeUsage.addChild("c0_BOOTSTRAP").withCard(0, 1);
c0_CPU = c0_PurposeUsage.addChild("c0_CPU").withCard(0, 1);
c0_GENERAL = c0_PurposeUsage.addChild("c0_GENERAL").withCard(0, 1);
c0_GPU = c0_PurposeUsage.addChild("c0_GPU").withCard(0, 1);
c0_I_O = c0_PurposeUsage.addChild("c0_I_O").withCard(0, 1);
c0_MEMORY = c0_PurposeUsage.addChild("c0_MEMORY").withCard(0, 1);
c0_NETWORK = c0_PurposeUsage.addChild("c0_NETWORK").withCard(0, 1);
c0_availability = c0_InstanceType.addChild("c0_availability").withCard(1, 1);
c2_size = c0_InstanceType.addChild("c2_size").withCard(1, 1).withGroupCard(1, 1);
c0_MICRO = c2_size.addChild("c0_MICRO").withCard(0, 1);
c0_MILLI = c2_size.addChild("c0_MILLI").withCard(0, 1);
c0_SMALL = c2_size.addChild("c0_SMALL").withCard(0, 1);
c0_MEDIUM = c2_size.addChild("c0_MEDIUM").withCard(0, 1);
c0_LARGE = c2_size.addChild("c0_LARGE").withCard(0, 1);
c0_X2LARGE = c2_size.addChild("c0_X2LARGE").withCard(0, 1);
c0_X4LARGE = c2_size.addChild("c0_X4LARGE").withCard(0, 1);
c0_X8LARGE = c2_size.addChild("c0_X8LARGE").withCard(0, 1);
c0_X16LARGE = c2_size.addChild("c0_X16LARGE").withCard(0, 1);
c0_X32LARGE = c2_size.addChild("c0_X32LARGE").withCard(0, 1);
c0_MODERATE = c0_NetworkingPerformance.addChild("c0_MODERATE").withCard(0, 1);
c0_HIGH = c0_NetworkingPerformance.addChild("c0_HIGH").withCard(0, 1);
c0_LOW = c0_NetworkingPerformance.addChild("c0_LOW").withCard(0, 1);
c0_VERY_LOW = c0_NetworkingPerformance.addChild("c0_VERY_LOW").withCard(0, 1);
c0_TEN_GIGABITS = c0_NetworkingPerformance.addChild("c0_TEN_GIGABITS").withCard(0, 1);
c0_CLUSTER = c0_Features.addChild("c0_CLUSTER").withCard(0, 1);
c1_EBS = c0_Features.addChild("c1_EBS").withCard(0, 1);
c0_NETWORKING = c0_Features.addChild("c0_NETWORKING").withCard(0, 1);
c0_FREE_TIER = c0_Features.addChild("c0_FREE_TIER").withCard(0, 1);
c0_PER_USAGE = c0_BillingType.addChild("c0_PER_USAGE").withCard(0, 1);
c0_SPOT = c0_BillingType.addChild("c0_SPOT").withCard(0, 1).extending(c0_Feature);
c0_min_value = c0_SPOT.addChild("c0_min_value").withCard(1, 1);
c0_max_value = c0_SPOT.addChild("c0_max_value").withCard(1, 1);
c0_RESERVED = c0_BillingType.addChild("c0_RESERVED").withCard(0, 1);
c1_type = c0_Instance.addChild("c1_type").withCard(1, 1);
c0_billing = c0_Instance.addChild("c0_billing").withCard(1, 1);
c0_image = c0_Instance.addChild("c0_image").withCard(1, 1);
c1_Features = c0_Instance.addChild("c1_Features").withCard(0, 1).withGroupCard(1).extending(c0_Features);
c1_cost = c0_Instance.addChild("c1_cost").withCard(1, 1);
c0_cost.refToUnique(Int);
c0_size.refToUnique(Int);
c1_size.refToUnique(Int);
c0_type.refToUnique(c0_VolumeType);
c0_zones.refToUnique(c0_Zone);
c0_speed.refToUnique(Int);
c0_number.refToUnique(Int);
c0_locations.refToUnique(c0_Location);
c0_priceTypes.refToUnique(c0_BillingType);
c0_architecture.refToUnique(c0_Architecture);
c0_location.refToUnique(c0_Location);
c0_hypervisor.refToUnique(c0_HypervisorType);
c0_virtualization.refToUnique(c0_VirtualizationType);
c0_hardware.refToUnique(c0_HardwareConfiguration);
c0_provider.refToUnique(c0_Provider);
c0_availability.refToUnique(c0_Location);
c0_min_value.refToUnique(Int);
c0_max_value.refToUnique(Int);
c1_type.refToUnique(c0_InstanceType);
c0_billing.refToUnique(c0_BillingType);
c0_image.refToUnique(c0_Image);
c1_cost.refToUnique(Int);
Constraint(ifOnlyIf(some(join(global(c0_Platform), c0_LINUX)), or(or(or(or(some(join(global(c0_OperatingSystem), c0_CENTOS)), some(join(global(c0_OperatingSystem), c0_DEBIAN))), some(join(global(c0_OperatingSystem), c0_REDHAT))), some(join(global(c0_OperatingSystem), c0_SUSE))), some(join(global(c0_OperatingSystem), c0_UBUNTU)))));
Constraint(ifOnlyIf(some(join(global(c0_Platform), c0_WINDOWS)), some(join(global(c0_OperatingSystem), c0_WINDOWS_2008_SERVER))));
c0_cost.addConstraint(greaterThanEqual(joinRef($this()), constant(0)));
c0_size.addConstraint(greaterThan(joinRef($this()), constant(0)));
c1_size.addConstraint(greaterThan(joinRef($this()), constant(0)));
c0_Location.addConstraint(greaterThan(card(join($this(), c0_zones)), constant(0)));
c0_speed.addConstraint(greaterThan(joinRef($this()), constant(0)));
c0_number.addConstraint(greaterThan(joinRef($this()), constant(0)));
c0_Provider.addConstraint(greaterThan(card(join($this(), c0_priceTypes)), constant(0)));
c0_locations.addConstraint(greaterThan(card($this()), constant(0)));
c0_InstanceType.addConstraint(implies(some(join(join($this(), c2_size), c0_MICRO)), some(join(join($this(), c0_PurposeUsage), c0_BOOTSTRAP))));
c0_InstanceType.addConstraint(implies(some(join(join($this(), c2_size), c0_MILLI)), some(join(join($this(), c0_PurposeUsage), c0_BOOTSTRAP))));
c0_InstanceType.addConstraint(ifOnlyIf(some(join(join($this(), c0_PurposeUsage), c0_BOOTSTRAP)), or(some(join(join($this(), c2_size), c0_MICRO)), some(join(join($this(), c2_size), c0_MILLI)))));
c0_min_value.addConstraint(and(greaterThan(joinRef($this()), constant(0)), lessThanEqual(joinRef($this()), joinRef(join(joinParent($this()), c0_max_value)))));
c0_max_value.addConstraint(and(greaterThan(joinRef($this()), constant(0)), lessThanEqual(joinRef($this()), joinRef(join(joinParent($this()), c0_min_value)))));
c0_Instance.addConstraint(equal(joinRef(join($this(), c1_cost)), add(joinRef(join(joinRef(join($this(), c0_image)), c0_cost)), sum(join(join($this(), c1_Features), c0_cost)))));
