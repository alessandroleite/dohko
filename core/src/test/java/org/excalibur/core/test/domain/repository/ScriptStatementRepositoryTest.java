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
package org.excalibur.core.test.domain.repository;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.excalibur.core.execution.domain.ScriptStatement;
import org.excalibur.core.execution.domain.repository.ScriptStatementRepository;
import org.excalibur.core.test.TestSupport;
import org.excalibur.core.util.YesNoEnum;
import org.junit.Before;
import org.junit.Test;

import ch.vorburger.exec.ManagedProcessException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


public class ScriptStatementRepositoryTest extends TestSupport
{
    private ScriptStatementRepository scriptStatementRepository;
    
    @Override
    @Before
    public void setup() throws IOException, ManagedProcessException
    {
        super.setup();
        scriptStatementRepository = openRepository(ScriptStatementRepository.class);
    }
    
    @Test
    public void must_insert_one_statement() throws IOException
    {
        ScriptStatement statement = new ScriptStatement().setActive(YesNoEnum.YES).setStatement("who").setCreatedIn(new Date()).setName("who");
        Integer statementId = scriptStatementRepository.insertScriptStatement(statement);
        
        assertNotNull(statementId);
        assertThat(1, equalTo(statementId));
        statement.setId(statementId);
        
        ScriptStatement statement2 = this.scriptStatementRepository.findById(statementId);
        assertCompletlyEquals(statement, statement2);
        
        List<ScriptStatement> statements = this.scriptStatementRepository.listActiveStatements();
        
        assertThat(1, equalTo(statements.size()));
        assertCompletlyEquals(statement2, statements.get(0));
    }
    
    protected void assertCompletlyEquals(ScriptStatement statement1, ScriptStatement statement2)
    {
        assertThat(statement1, equalTo(statement2));
        
        assertThat(statement1.getActive(), equalTo(statement2.getActive()));
//        assertThat(statement1.getCreatedIn().getTime(), equalTo(statement2.getCreatedIn().getTime()));
        assertThat(statement1.getName(), equalTo(statement2.getName()));
        assertThat(statement1.getParents(), equalTo(statement2.getParents()));
        assertThat(statement1.getPlatform(), equalTo(statement2.getPlatform()));
        assertThat(statement1.getStatement(), equalTo(statement2.getStatement()));
    }
}
