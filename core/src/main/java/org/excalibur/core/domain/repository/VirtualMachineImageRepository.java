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
import java.util.List;

import org.excalibur.core.cloud.api.OsArchitectureType;
import org.excalibur.core.cloud.api.Platform;
import org.excalibur.core.cloud.api.VirtualMachineImage;
import org.excalibur.core.cloud.api.HypervisorType;
import org.excalibur.core.cloud.api.VirtualizationType;
import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.domain.repository.VirtualMachineImageRepository.VirtualMachineImageSetMapper;
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

@RegisterMapper(VirtualMachineImageSetMapper.class)
public interface VirtualMachineImageRepository extends Closeable
{
    String SQL_INSERT = "INSERT INTO virtual_instance_image (region_id, name, hypervisor_id, virtualization_type_id, platform, os_architecture_id, endpoint, default_username)\n" +
    		            "VALUES (:region.id, :name, :hypervisor.id, :virtualizationType.id, :platform.value, :architecture.id, :endpoint, :defaultUsername)";
    
    String SQL_SELECT_ALL = "SELECT vmi.id as vmi_id, vmi.region_id, vmi.name as vim_name, vmi.hypervisor_id, vmi.platform as vmi_platform, vmi.description as vmi_description,\n" +
    		                " vmi.os_architecture_id as vmi_os_architecture_id, vmi.virtualization_type_id, vmi.endpoint as vmi_endpoint, virtualization_type_id,\n" +
    		                " default_username as vmi_platform_username, r.name as region_name, r.endpoint as region_endpoint, r.city_name as region_city_name\n" +
    		                "FROM virtual_instance_image vmi\n" +
    		                "JOIN region r ON r.id = vmi.region_id\n";

    @SqlUpdate(SQL_INSERT)
    @GetGeneratedKeys
    Integer insertVirtualMachineImage(@BindBean VirtualMachineImage vmi);

    @SqlBatch(SQL_INSERT)
    @BatchChunkSize(10)
    void insertVirtualMachineImages(@BindBean Iterable<VirtualMachineImage> vmi);

    @SqlQuery(SQL_SELECT_ALL + " WHERE lower(vmi.name) = lower(:name)")
    VirtualMachineImage findByExactlyName(@Bind("name") String name);

    @SqlQuery(SQL_SELECT_ALL + " WHERE vmi.id = :vmiId")
    VirtualMachineImage findById(@Bind("vmiId") Integer vmiId);

    @SqlQuery(SQL_SELECT_ALL + " WHERE vmi.region_id = :regionId")
    List<VirtualMachineImage> listAllVirtualMachineImagesOfRegion(@Bind("regionId") Integer regionId);
    
    @SqlQuery(SQL_SELECT_ALL + " WHERE vmi.virtualization_type_id = :id ORDER BY vmi.name")
    List<VirtualMachineImage> listAllVirtualMachineImagesOfVirtualizationType(@BindBean VirtualizationType type);
    
    @SqlQuery(SQL_SELECT_ALL + " WHERE vmi.virtualization_type_id = :type AND vmi.region_id = :regionId ORDER BY vmi.name")
    List<VirtualMachineImage> listAllVirtualMachineImagesOfVirtualizationTypeOnRegion(@Bind("type") Integer virtualizationTypeId, @Bind("regionId") Integer regionId);
    
    @SqlQuery(SQL_SELECT_ALL + " WHERE lower(r.name) = lower(:regionName)")
    List<VirtualMachineImage> listAllVirtualMachineImagesOfExactRegionName(@Bind("regionName") String regionName);
    
    @SqlQuery(SQL_SELECT_ALL + " WHERE lower(vmi.name) = lower(:imageId) AND lower(r.name) = lower(:regionName)")
    VirtualMachineImage findByExactNameOnRegion(@Bind("imageId") String imageId, @Bind("regionName") String regionName);
    
    @SqlQuery(SQL_SELECT_ALL + " ORDER BY r.name, vmi.name")
    List<VirtualMachineImage> listAllVirtualMachineImages();
    
    @SqlQuery(SQL_SELECT_ALL + " JOIN region_provider rp ON rp.region_id = r.id AND provider_id = :providerId ORDER BY r.name, vmi.name\n")
    List<VirtualMachineImage> listAllVirtualMachineImagesOfProvider(@Bind("providerId") Integer providerId);

    public static final class VirtualMachineImageSetMapper implements ResultSetMapper<VirtualMachineImage>
    {
        @Override
        public VirtualMachineImage map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            VirtualMachineImage vmi = new VirtualMachineImage();
            
            vmi.setArchitecture(OsArchitectureType.valueOf(r.getInt("vmi_os_architecture_id")))
               .setDefaultUsername(r.getString("vmi_platform_username"))
               .setDescription(r.getString("vmi_description"))
               .setEndpoint(r.getString("vmi_endpoint"))
               .setHypervisor(HypervisorType.valueOf(r.getInt("hypervisor_id")))
               .setId(r.getInt("vmi_id"))
               .setName(r.getString("vim_name"))
               .setPlatform(Platform.valueOfFromValue(r.getString("vmi_platform")))
               .setRegion(new Region().setCity(r.getString("region_city_name"))
                                      .setEndpoint(r.getString("region_endpoint"))
                                      .setId(r.getInt("region_id"))
                                      .setName(r.getString("region_name")))
               .setVirtualizationType(VirtualizationType.valueOf(r.getInt("virtualization_type_id")));
            
            return vmi;
        }
    }
}
