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
package org.excalibur.core.execution.domain.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.excalibur.core.execution.domain.Application;
import org.excalibur.core.execution.domain.Block;
import org.excalibur.core.execution.domain.repository.BlockRepository.BlockResultSetMapper;
import org.excalibur.core.json.databind.ObjectMapperUtil;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.google.common.base.Optional;

import io.dohko.jdbi.binders.BindBean;
import io.dohko.jdbi.stereotype.Repository;

import static org.excalibur.core.sql.ResultSets.*;


@Repository
@RegisterMapper(BlockResultSetMapper.class)
public interface BlockRepository 
{
	@GetGeneratedKeys
	@SqlUpdate("INSERT INTO group_job_task (job_id, uuid, name, repeats, parents, raw_description) VALUES ( (SELECT id FROM job WHERE lower(uuid) = lower(:jobId)), :id, :name, :repeat, :parentNames, :plainText)")
	Integer insert(@BindBean Block block);
	
	@SqlBatch("INSERT INTO group_job_task (job_id, uuid, name, repeats, parents, raw_description) VALUES ( (SELECT id FROM job WHERE lower(uuid) = lower(:jobId)), :id, :name, :repeat, :parentNames, :plainText)")
	void insert(@BindBean Iterable<Block> blocks);
	
	@SingleValueResult
	@SqlQuery(" SELECT j.uuid as job_uuid, b.job_id, b.uuid, b.name, b.repeats, b.parents, b.raw_description\n" + 
	          " FROM group_job_task b \n" + 
			  " JOIN job j ON t.id = b.job_id \n" +
	          " WHERE lower (b.uuid) = lower(:uuid)" )
	Optional<Block> findById(@Bind("uuid") String id);
	
	@SqlQuery(" SELECT j.uuid as job_uuid, b.job_id, b.uuid, b.name, b.repeats, b.parents, b.raw_description\n" + 
	          " FROM group_job_task b \n" + 
			  " JOIN job j ON t.id = b.job_id \n" +
	          " WHERE lower (j.uuid) = lower(:jobId) \n" +
			  " ORDER BY j.uuid, b.name")
	List<Block> getAllBlocksOfJob(@Bind("jobId") String jobId);
	
	class BlockResultSetMapper implements ResultSetMapper<Block>
	{
		@Override
		public Block map(int index, ResultSet r, StatementContext ctx) throws SQLException 
		{
			List<Application> applications = new ObjectMapperUtil().readJsonValue(Block.class, readString("raw_description", r).orElse(null)).orElse(new Block()).getApplications();
			
			return new Block()
					.setId(r.getString("uuid"))
					.setJobId(r.getString("job_uuid"))
					.setName(r.getString("name"))
					.setParents(readString("parents", r).orElse("").split(","))
					.setRepeat(r.getInt("repeats"))
					.addApplications(applications);
		}
	}
}
