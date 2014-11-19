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
package org.excalibur.core.test.execution.domain.repository;

import java.io.IOException;
import java.util.Date;

import org.excalibur.core.execution.domain.ApplicationExecDescription;

import org.excalibur.core.execution.domain.ScriptStatement;
import org.excalibur.core.execution.domain.repository.ApplicationDescriptionRepository;
import org.excalibur.core.execution.domain.repository.ScriptStatementRepository;
import org.excalibur.core.test.TestSupport;
import org.junit.Before;
import org.junit.Test;

import static org.excalibur.core.execution.domain.FailureAction.*;
import static org.excalibur.core.util.YesNoEnum.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class ApplicationDescriptionRepositoryTest extends TestSupport
{
    private ApplicationDescriptionRepository applicationDescriptionRepository_;
    
    private ScriptStatement application;

    @Override
    @Before
    public void setup() throws IOException
    {
        super.setup();
        
        this.applicationDescriptionRepository_ = openRepository(ApplicationDescriptionRepository.class);
        this.application = new ScriptStatement().setActive(YES).setCreatedIn(new Date()).setName("who").setStatement("who");
        this.application.setId(openRepository(ScriptStatementRepository.class).insertScriptStatement(application));
    }
    
    
    @Test
    public void must_insert_one_application_description()
    {
        ApplicationExecDescription description = new ApplicationExecDescription();
        description.setApplication(application).setFailureAction(ABORT).setName("app-desc-1").setResource("c3.medium").setUser(user);

        Integer id = applicationDescriptionRepository_.insert(description);
        assertNotNull(id);
        assertThat(1, equalTo(1));
        description.setId(id);
        
        ApplicationExecDescription inserted = applicationDescriptionRepository_.findById(id);
        assertNotNull(inserted);
        
        assertCompletlyEquals(description, inserted);
    }


    private void assertCompletlyEquals(ApplicationExecDescription expected, ApplicationExecDescription newState)
    {
        assertThat(expected.getApplication().getId(), equalTo(newState.getApplication().getId()));
        assertThat(expected.getName(), equalTo(newState.getName()));
        assertThat(expected.getResource(), equalTo(newState.getResource()));
        assertThat(expected.getUser().getId(), equalTo(newState.getUser().getId()));
        assertThat(expected.getId(), equalTo(newState.getId()));
        assertThat(expected.getCreatedIn().getTime(), equalTo(newState.getCreatedIn().getTime()));
    }
}
