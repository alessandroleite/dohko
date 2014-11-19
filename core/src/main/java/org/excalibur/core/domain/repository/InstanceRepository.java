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
package org.excalibur.core.domain.repository;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.excalibur.core.cloud.api.InstanceStateDetails;
import org.excalibur.core.cloud.api.Placement;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.VmConfiguration;
import org.excalibur.core.cloud.api.InstanceStateType;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.repository.InstanceRepository.InstanceMapper;
import org.excalibur.core.domain.repository.InstanceTypeRepository.InstanceTypeRowMapper;
import org.excalibur.core.domain.repository.RegionRepository.ZoneRowMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(InstanceMapper.class)
public interface InstanceRepository extends Closeable
{
    // -------------------------------------------------------- //
    // ---                 Constants                        --- //
    // -------------------------------------------------------- //
    String SQL_INSERT_INSTANCE = "INSERT INTO instance(instance_type_id, owner_id, zone_id, name, image_id, public_ip, private_ip,\n" +
              " public_dns, launch_time, platform, platform_username, keyname, group_name, user_data)\n" +
              " VALUES (:type.id, :owner.id, :location.id, :name, :imageId, :configuration.publicIpAddress, :configuration.privateIpAddress,\n" +
              " :configuration.publicDnsName, :launchTime, :configuration.platform, :configuration.platformUserName, :configuration.keyName,\n" +
              " :placement.groupName, :userData)"; 
    
    String SQL_UPDATE_INSTANCE = "UPDATE instance SET public_ip = :configuration.publicIpAddress, private_ip = :configuration.privateIpAddress,\n" +
    		"public_dns = :configuration.publicDnsName, launch_time = :launchTime " +
    		"WHERE id = :id and owner_id = :owner.id";
    
    String SQL_SELECT_INSTANCES = "SELECT " +
            " instance_id, instance_type_id, owner_id, zone_id, family_type_id, provider_id, instance_state_type_id, region_id,\n" +
            " instance_type_name, name, image_id, public_ip, private_ip, public_dns, launch_time, platform,\n" +
            " platform_username, keyname, number_compute_units, number_compute_units_core, group_name, user_data,\n" +
            " memory_size_gb, storage_size_gb, ub_number_instance, instance_type_generation, family_type_name,\n" +
            " region_name, region_endpoint, region_city_name, zone_name, geographic_region_id, geographic_region_name,\n" +
            " provider_name, provider_class_name, cost_per_unit_time, instance_state_time, ub_number_instance as ub_instances_per_type,\n" +
            " internal_net_throughput, external_net_throughput, sustainable_performance_gflops, support_placement_group, required_virtualization_type_id\n" +
            "FROM vw_instance\n";
    
    
    // -------------------------------------------------------- //
    // ---                 Update methods                   --- //
    // -------------------------------------------------------- //
    
    @SqlUpdate(SQL_INSERT_INSTANCE)
    @GetGeneratedKeys
    Integer insertInstance(@BindBean VirtualMachine instance);
    
    @SqlBatch(value=SQL_INSERT_INSTANCE)
    @BatchChunkSize(10)
    void insertInstances(@BindBean Collection<VirtualMachine> instances);
    
    @SqlUpdate(SQL_UPDATE_INSTANCE)
    void updateInstance(@BindBean VirtualMachine instance);
    
    @SqlUpdate(SQL_UPDATE_INSTANCE)
    void updateInstances(@BindBean Iterable<VirtualMachine> instances);
    
    @SqlQuery(SQL_SELECT_INSTANCES + " WHERE provider_id = :providerId AND lower(name) = lower(:instanceName)")
    VirtualMachine findInstanceByName(@Nonnull @Bind("instanceName") String instanceName, @Nonnull @Bind("providerId") Integer providerId);
    
    @SqlQuery(SQL_SELECT_INSTANCES + " WHERE lower(name) = lower(:instanceName)")
    VirtualMachine findInstanceByName(@Nonnull @Bind("instanceName") String instanceName);
    
    
    // -------------------------------------------------------- //
    // ---             Instance query methods               --- //
    // -------------------------------------------------------- //

    /**
     * Returns an instance with the given id or <code>null</code> if it does not exist.
     * 
     * @param owenerId
     *            The instance's owner.
     * @param instanceId
     *            The instance id.
     * @return The instance with the given id or <code>null</code> if it does not exist.
     */
    @SqlQuery(SQL_SELECT_INSTANCES + " WHERE owner_id = :ownerId AND instance_id = :instanceId")
    VirtualMachine findInstanceById(@Bind("ownerId")Integer ownerId, @Bind("instanceId") Integer instanceId);
    
    @SqlQuery(SQL_SELECT_INSTANCES + " WHERE owner_id = :ownerId AND public_dns = :hostAddress")
    VirtualMachine findInstanceByHostAddress(@Bind("ownerId")Integer ownerId, @Bind("hostAddress") String hostAddress);
    
    @SqlQuery(SQL_SELECT_INSTANCES + " WHERE public_ip = :publicIp")
    VirtualMachine findInstanceByPublicIp(@Bind("publicIp") String publicIp);
    
    @SqlQuery(SQL_SELECT_INSTANCES + " WHERE owner_id = :ownerId AND lower(name) = lower(:name)")
    VirtualMachine findInstanceByName(@Bind("ownerId")Integer ownerId, @Bind("name") String name);

    /**
     * Returns the instances of a {@link User} in the given {@code state}.
     * 
     * @param ownerId The {@link User} to return his/her instances.
     * @param state The state of the instances.
     * @return A non <code>null</code> {@link List} with the instances in the given state.
     */
    @SqlQuery(SQL_SELECT_INSTANCES + " WHERE owner_id = :ownerId AND instance_state_type_id = :id")
    List<VirtualMachine> listInstancesOfUserWithState(@Nonnull @Bind("ownerId") Integer ownerId, @BindBean(params={"id:id"}) InstanceStateType state);
    
    @SqlQuery(SQL_SELECT_INSTANCES + " WHERE provider_id = :providerId AND instance_state_type_id = :id")
    List<VirtualMachine> listInstancesOnProviderWithState(@Nonnull @Bind("providerId") Integer providerId, @BindBean(params={"id:id"}) InstanceStateType state);
    
    /**
     * Returns all instances with the given states.
     * 
     * @param owner The user who the instances belong to.
     * @param states The instances on the states.
     * @return The instances on the given states or an empty {@link List}.
     */
//    List<VirtualMachine> listInstancesOfUserWithStates(Integer ownerId, InstanceStateType ... states);
    
    /**
     * Returns all instances of a provider in the given state.
     * 
     * @param ownerIdcolumnIndex
     * @param providerId
     * @param state
     * @return The instances or an empty {@link List} if there is not any.
     */
    @SqlQuery(SQL_SELECT_INSTANCES + " WHERE owner_id = :ownerId AND provider_id = :providerId AND instance_state_type_id = :id")
    List<VirtualMachine> getInstancesInState(@Bind("ownerId") Integer ownerId, @Bind("providerId") Integer providerId, @BindBean(params={"id:id"}) InstanceStateType state);
    
        
    // -------------------------------------------------------- //
    // ---             Instance state methods               --- //
    // -------------------------------------------------------- //
    
    String SQL_INSERT_INSTANCE_STATE_DETAILS = "INSERT INTO instance_state_history " +
    		"(instance_id, instance_state_type_id, state_time) VALUES (:instance.id, :state.id, :time)";
    
    @SqlUpdate(SQL_INSERT_INSTANCE_STATE_DETAILS)
    @GetGeneratedKeys
    Integer insertInstanceState(@BindBean InstanceStateDetails state);
    
    @SqlBatch(value=SQL_INSERT_INSTANCE_STATE_DETAILS)
    @BatchChunkSize(10)
    void insertInstanceStates(Iterable<InstanceStateDetails> states);
    
    public static final class InstanceMapper implements ResultSetMapper<VirtualMachine>
    {
        @Override
        public VirtualMachine map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            VirtualMachine instance = new VirtualMachine()
                   .setConfiguration(new VmConfiguration()
                           .setKeyName(r.getString("keyname"))                           
                           .setPlatform(r.getString("platform"))
                           .setPlatformUserName(r.getString("platform_username"))
                           .setPrivateIpAddress(r.getString("private_ip"))
                           .setPublicDnsName(r.getString("public_dns"))
                           .setPublicIpAddress(r.getString("public_ip")))
                   .setId(r.getInt("instance_id"))        
                   .setImageId(r.getString("image_id"))
                   .setLaunchTime(r.getTimestamp("launch_time"))
                   .setName(r.getString("name"))
                   .setOwner(new User(r.getInt("owner_id")))
                   .setPlacement(new Placement().setGroupName(r.getString("group_name")).setZone(r.getString("zone_name")))
                   .setType(new InstanceTypeRowMapper().map(index, r, ctx))
//                   .setLocation(new Region()
//                           .setId(r.getInt("region_id"))
//                           .setName(r.getString("region_name"))
//                           .setEndpoint(r.getString("region_endpoint")))
                   .setLocation(new ZoneRowMapper().map(index, r, ctx))
                   .setCost(r.getBigDecimal("cost_per_unit_time"))
                   .setUserData(r.getString("user_data"));
            
            int stateTypeId = r.getInt("instance_state_type_id");
            if (!r.wasNull() && stateTypeId > 0)
            {
                instance.setState(new InstanceStateDetails(InstanceStateType.valueOf(stateTypeId), r.getTimestamp("instance_state_time")));
            }
            return instance;
        }
    }
    
    void close();
}
