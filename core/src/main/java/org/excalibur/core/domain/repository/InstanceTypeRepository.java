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
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.excalibur.core.cloud.api.InstanceFamilyType;
import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.Provider;
import org.excalibur.core.cloud.api.ProviderSupport;
import org.excalibur.core.cloud.api.VirtualizationType;
import org.excalibur.core.domain.repository.RegionRepository.RegionRowMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.excalibur.core.util.YesNoEnum;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import io.dohko.jdbi.stereotype.Repository;

@Repository
public interface InstanceTypeRepository extends Closeable
{
    // -------------------------------------------------------- //
    // ---                 Constants                        --- //
    // -------------------------------------------------------- //
    
    String SELECT_ALL_INSTANCE_TYPES = 
    		  "SELECT i.id as instance_type_id, i.provider_id, i.name as instance_type_name, i.number_compute_units,\n"
    		+ " i.number_compute_units_core, i.memory_size_gb, i.storage_size_gb, i.ub_number_instance, i.generation_number as instance_type_generation,\n"
    		+ " i.sustainable_performance_gflops, i.internal_net_throughput, i.external_net_throughput, i.support_placement_group,\n"
    		+ " ft.name as family_type_name, ft.id as family_type_id, i.required_virtualization_type_id,\n"
    		+ " p.name as provider_name, p.class_name as provider_class_name, p.ub_instances_per_type\n"
    		+ "FROM instance_type i\n"
    		+ "  join instance_family_type ft on ft.id = i.family_type_id\n"
    		+ "  join provider              p on  p.id = i.provider_id\n";
    
    String QUERY_SELECT_INSTANCE_TYPE_BY_PROVIDER = SELECT_ALL_INSTANCE_TYPES + " WHERE it.provider_id = :providerId\n";

    String SQL_INSERT_INSTANCE_TYPE = "INSERT INTO instance_type\n" +
            "(\n"+
            "  provider_id, family_type_id, name, number_compute_units, number_compute_units_core,\n" +
            "  memory_size_gb, storage_size_gb, ub_number_instance, generation_number, sustainable_performance_gflops, support_placement_group,\n" +
            "  required_virtualization_type_id\n" +
            ")\n" +
            "VALUES\n(" +
            "  :provider.id, :familyType.id, :name, :configuration.numberOfComputeUnits, :configuration.numberOfCores,\n" +
            "  :configuration.ramMemorySizeGb, :configuration.diskSizeGb,\n" +
            "  :configuration.maximumNumberOfInstances, :configuration.generation, :configuration.sustainablePerformanceGflops,\n" +
            "  :supportPlacementGroup.id, :requiredVirtualizationType.id\n" +
            ")";
    
    String SELECT_INSTANCE_TYPES_PROVIDER_ON_REGION = "SELECT i.id as instance_type_id, i.provider_id, i.name as instance_type_name, i.number_compute_units,\n"
            + " i.number_compute_units_core, i.memory_size_gb, i.storage_size_gb, i.ub_number_instance, i.generation_number as instance_type_generation,\n"
            + " i.sustainable_performance_gflops, i.internal_net_throughput, i.external_net_throughput, i.required_virtualization_type_id,\n" 
            + " ft.name as family_type_name, ft.id as family_type_id,\n"
            + " p.name as provider_name, p.class_name as provider_class_name, p.ub_instances_per_type,\n"
            + " itc.cost_per_unit_time, unit_time_id, r.id as region_id, r.name as region_name, r.endpoint as region_endpoint, " +
              "  i.support_placement_group,\n" 
            + " r.city_name as region_city_name, r.geographic_region_id, gr.name as geographic_region_name\n"
            + "FROM instance_type_cost itc\n" 
            + "  join instance_type         i on i.id  = itc.instance_type_id\n"
            + "  join instance_family_type ft on ft.id = i.family_type_id\n"
            + "  join provider              p on p.id  = i.provider_id\n"
            + "  join region_provider      rp on rp.id = itc.region_provider_id\n"
            + "  join region                r on r.id = itc.region_provider_id\n" 
            + "  join geographic_region    gr on gr.id = r.geographic_region_id\n";
    
    
    // -------------------------------------------------------- //
    // ---                 Update methods                   --- //
    // -------------------------------------------------------- //
    
    @SqlUpdate(SQL_INSERT_INSTANCE_TYPE)
    @GetGeneratedKeys
    Integer insertInstanceType(@BindBean InstanceType type);

    @SqlBatch(SQL_INSERT_INSTANCE_TYPE)
    @BatchChunkSize(10)
    void insertInstanceType(@BindBean List<InstanceType> type);
    

    // -------------------------------------------------------- //
    // ---           Instance types by provider             --- //
    // -------------------------------------------------------- //
        
    /**
     * Returns all the instance types of a given {@link Provider} ordered by the family and name.
     * 
     * @param provider The provider to return its instance types. Might not be <code>null</code>.
     * @return A not <code>null</code> {@link List} with the available instances.
     */
    @SqlQuery(QUERY_SELECT_INSTANCE_TYPE_BY_PROVIDER + " order by family_type_id, instance_type_name")
    @RegisterMapper(InstanceTypeRowMapper.class)
    List<InstanceType> getInstanceTypesOfProvider(@Bind("providerId") Integer providerId);
    
    
    /**
     * Returns all the instance types of a given {@link Provider} ordered by the family and name and the number of virtual cores.
     * 
     * @param providerId The provider to return its instance types. Might not be <code>null</code>.
     * @param regionId The region to return the instance types. Might not be <code>null</code>.
     * @return A not <code>null</code> {@link List} with the available instances.
     */
    @SqlQuery(SELECT_INSTANCE_TYPES_PROVIDER_ON_REGION + " WHERE i.provider_id = :providerId AND r.id = :regionId\n" +
    		  " ORDER by ft.name, i.number_compute_units_core\n")
    @RegisterMapper(InstanceTypeProviderRowMapper.class)
    List<InstanceType> getInstanceTypesOfProviderInRegion(@Bind("providerId") Integer providerId, @Bind("regionId") Integer regionId);
    
    /**
     * Returns all the instance types of a given {@link Provider} ordered by the family and name and the number of virtual cores.
     * 
     * @param providerId The provider to return its instance types. Might not be <code>null</code>.
     * @param regionName The region's name to return the instance types. Might not be <code>null</code>.
     * @return A not <code>null</code> {@link List} with the available instances.
     */
    @SqlQuery(SELECT_INSTANCE_TYPES_PROVIDER_ON_REGION + " WHERE i.provider_id = :providerId AND " +
    		  " r.id = (select rr.id from region rr where lower(rr.name) = lower(:regionName))\n" +
              " ORDER by ft.name, i.number_compute_units_core\n")
    @RegisterMapper(InstanceTypeProviderRowMapper.class)
    List<InstanceType> getInstanceTypesOfProviderInRegion(@Bind("providerId") Integer providerId, @Bind("regionName") String regionName);
    
    /**
     * Returns all the available instance types of the given family type in a provider. The instance types are ordered by name.
     * 
     * @param provider The provider to return its instance types. Might not be <code>null</code>.
     * @param family The family which you are interested in.
     * @return A not <code>null</code> {@link List} with the available instances of the given family.
     */
    @SqlQuery(QUERY_SELECT_INSTANCE_TYPE_BY_PROVIDER + " and family_type_id = :family.id order by instance_type_name")
    @RegisterMapper(InstanceTypeRowMapper.class)
    List<InstanceType> getInstanceTypesOfFamilyType(@Bind("providerId") Integer providerId, @BindBean InstanceFamilyType family);
    
    /**
     * Returns all instance types available in all the providers. The set are ordered by provider, instance family type, and instance name.
     * @return The available instances ordered by by provider, instance family type, and instance name.
     */
    @SqlQuery(SELECT_ALL_INSTANCE_TYPES + " order by i.provider_id, family_type_id, instance_type_name")
    @RegisterMapper(InstanceTypeRowMapper.class)
    List<InstanceType> getAllInstanceTypes();
    
    @SqlQuery(SELECT_ALL_INSTANCE_TYPES + " where lower(i.name) = lower(:name)")
    @RegisterMapper(InstanceTypeRowMapper.class)
    InstanceType findInstanceTypeByName(@Bind("name") String name);
    
    @SqlQuery(SELECT_INSTANCE_TYPES_PROVIDER_ON_REGION + "WHERE rp.provider_id = :providerId AND itc.region_provider_id = :regionId\n ORDER by ft.name, i.name")
    @RegisterMapper(InstanceTypeProviderRowMapper.class)
    List<InstanceType> getProviderInstanceTypesOnRegion(@Bind("providerId") Integer providerId, @Bind("regionId") Integer regionId);
    
    @SqlQuery(SELECT_INSTANCE_TYPES_PROVIDER_ON_REGION + "WHERE rp.provider_id = :providerId AND r.geographic_region_id = :regionId\n ORDER by ft.name, i.name")
    @RegisterMapper(InstanceTypeProviderRowMapper.class)
    List<InstanceType> getProviderInstanceTypesOnGeographicRegion(@Bind("providerId") Integer providerId, @Bind("regionId") Integer geographicRegionId);
    
    /**
     * Returns all instance types available in a given geographic region. In other words, the instance types of all providers of the given region.
     * 
     * @param geographicRegionId
     *            The geographic region's id. Might not be <code>null</code>.
     * @return A non-null {@link List} with the available instance types order by the family type and instance type name.
     */
    @SqlQuery(SELECT_INSTANCE_TYPES_PROVIDER_ON_REGION + "WHERE r.geographic_region_id = :geoRegionId\n ORDER by ft.name, i.name")
    @RegisterMapper(InstanceTypeProviderRowMapper.class)
    List<InstanceType> listAllInstanceTypesOfGeographicRegion(@Bind("geoRegionId") Integer geographicRegionId);
    
    
    static class InstanceTypeRowMapper implements ResultSetMapper<InstanceType>
    {
        @Override
        public InstanceType map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            InstanceType type = new InstanceType().setName(r.getString("instance_type_name")).setId(r.getInt("instance_type_id"));
            type.setProvider(new ProviderSupport().setId(r.getInt("provider_id"))
                                                  .setLimitOfResourcesPerType(r.getInt("ub_instances_per_type"))
                                                  .setName(r.getString("provider_name"))
                                                  .setServiceClass(r.getString("provider_class_name")));
            
            type.setFamilyType(InstanceFamilyType.valueOf(r.getInt("family_type_id")))
                .setSupportPlacementGroup(YesNoEnum.valueOfFrom(r.getString("support_placement_group")));
            
            Integer virtualizationType = r.getInt("required_virtualization_type_id");
            
            if (!r.wasNull())
            {
                type.setRequiredVirtualizationType(VirtualizationType.valueOf(virtualizationType));
            }
            else 
            {
                type.setRequiredVirtualizationType(VirtualizationType.ANY);
            }
            
            type.getConfiguration()
                    .setGeneration(r.getInt("instance_type_generation"))
                    .setMaximumNumberOfInstances(r.getInt("ub_number_instance"))
                    .setNumberOfComputUnits(r.getInt("number_compute_units"))
                    .setNumberOfCores(r.getInt("number_compute_units_core"))
                    .setRamMemorySizeGb(r.getDouble("memory_size_gb"))
                    .setDiskSizeGb(r.getLong("storage_size_gb"))
                    .setInternalNetworkThroughput(r.getDouble("internal_net_throughput"))
                    .setNetworkThroughput(r.getDouble("external_net_throughput"));
            
            BigDecimal sustainablePerformance = r.getBigDecimal("sustainable_performance_gflops");
            
            if (!r.wasNull())
            {
                type.getConfiguration().setSustainablePerformanceGflops(sustainablePerformance);
            }

            Double networkThroughput = r.getDouble("external_net_throughput");

            if (!r.wasNull())
            {
                type.getConfiguration().setNetworkThroughput(networkThroughput);
            }

            networkThroughput = r.getDouble("internal_net_throughput");

            if (!r.wasNull())
            {
                type.getConfiguration().setInternalNetworkThroughput(networkThroughput);
            }
            
            return type;
        }
    }
    
    static class InstanceTypeProviderRowMapper extends InstanceTypeRowMapper
    {
        @Override
        public InstanceType map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            InstanceType type = super.map(index, r, ctx)
                    .setCost(r.getBigDecimal("cost_per_unit_time"))
                    .setRegion(new RegionRowMapper().map(index, r, ctx));
            
            return type;
        }
    }
}
