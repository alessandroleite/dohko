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

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.excalibur.core.cloud.api.Platform;
import org.excalibur.core.execution.domain.ScriptStatement;
import org.excalibur.core.execution.domain.repository.ScriptStatementRepository.ScriptStatementSetMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.excalibur.core.util.YesNoEnum;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(ScriptStatementSetMapper.class)
public interface ScriptStatementRepository extends Closeable
{
    String QUERY_SELECT_ALL = "SELECT s.id as statement_id, s.statement as statement_script, s.active as statement_state, s.parents as statement_parents,\n" +
    		                  "       s.name as statement_name, s.platform as statement_platform, s.created_in as statement_created_in\n" +
                              "FROM script_statement s\n";

    @SqlUpdate("INSERT INTO script_statement (statement, parents, active, name, platform) VALUES (:statement, :parents, :active.id, :name, :platform.value)")
    @GetGeneratedKeys
    Integer insertScriptStatement(@BindBean ScriptStatement statement);
    
    @SqlQuery(QUERY_SELECT_ALL + "WHERE s.id = :statementId")
    ScriptStatement findById(@Bind("statementId") Integer statementId);

    @SqlQuery(QUERY_SELECT_ALL + "WHERE active = 'Y' ORDER BY name, platform")
    List<ScriptStatement> listActiveStatements();
    
    @SqlQuery(QUERY_SELECT_ALL + "WHERE active = 'Y' and platform = :platform.value ORDER BY name, platform")
    List<ScriptStatement> listAllActiveStatementsOfPlatform(@BindBean Platform platform);

    static final class ScriptStatementSetMapper implements ResultSetMapper<ScriptStatement>
    {
        @Override
        public ScriptStatement map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new ScriptStatement()
                    .setActive(YesNoEnum.valueOf(r.getString("statement_state").charAt(0)))
                    .setCreatedIn(r.getTimestamp("statement_created_in"))
                    .setId(r.getInt("statement_id"))
                    .setName(r.getString("statement_name"))
                    .setParents(r.getString("statement_parents"))
                    .setPlatform(Platform.valueOfFromValue(r.getString("statement_platform")))
                    .setStatement(r.getString("statement_script"));
        }
    }
}
