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

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.domain.Tag;
import org.excalibur.core.cloud.api.domain.Tags;
import org.excalibur.core.domain.repository.InstanceTagRepository.InstanceTagRowSetMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import io.dohko.jdbi.stereotype.Repository;

@Repository
@RegisterMapper(InstanceTagRowSetMapper.class)
public interface InstanceTagRepository extends Closeable
{
    
    @SqlUpdate("INSERT INTO instance_tag (instance_id, tag_name, tag_value) VALUES(:id, :name, :value)")
    @GetGeneratedKeys
    Integer addTagToInstance(@BindBean(params={"id:id"}) VirtualMachine instance, @BindBean(params={"name:name", "value:value"}) Tag tag);
    
    @SqlBatch("INSERT INTO instance_tag (instance_id, tag_name, tag_value) VALUES(:id, :name, :value)")
    void addTagsToInstance(@BindBean(params={"id:id"}) VirtualMachine instance, @BindBean(params={"name:name", "value:value"}) Tags tag);
    
    @SqlUpdate("UPDATE instance_tag SET tag_value = :name WHERE instance_id = :id")
    void updateTag(@BindBean VirtualMachine instance, @BindBean Tag tag);
    
    @SqlUpdate("DELETE FROM instance_tag WHERE instance_id =:instanceId")
    void removeAllTagsFromInstance(@Bind("instanceId") Integer instanceId);
    
    @SqlQuery("SELECT id as tag_id, instance_id, tag_name, tag_value FROM instance_tag WHERE instance_id = :instanceId ORDER BY tag_name")
    List<Tag> listTagsOfInstanceId(@Bind("instanceId") Integer instanceId);
    
    static class InstanceTagRowSetMapper implements ResultSetMapper<Tag>
    {
        @Override
        public Tag map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new Tag().setName(r.getString("tag_name")).setValue(r.getString("tag_value"));
        }
    }
}
