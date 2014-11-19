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
import java.util.List;

import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.Placement;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.VmConfiguration;
import org.excalibur.core.domain.repository.InstanceRepository;
import org.excalibur.core.execution.domain.ApplicationExecDescription;
import org.excalibur.core.execution.domain.ApplicationExecutionResult;
import org.excalibur.core.execution.domain.ScriptStatement;
import org.excalibur.core.execution.domain.repository.ApplicationDescriptionRepository;
import org.excalibur.core.execution.domain.repository.ApplicationExecutionResultRepository;
import org.excalibur.core.execution.domain.repository.ScriptStatementRepository;
import org.excalibur.core.test.TestSupport;

import org.junit.Before;
import org.junit.Test;

import static org.excalibur.core.util.YesNoEnum.*;
import static org.excalibur.core.execution.domain.FailureAction.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class ApplicationExecutionResultRepositoryTest extends TestSupport
{
    private ApplicationExecutionResultRepository instanceStatementRepository_;

    private VirtualMachine instance;
    private ApplicationDescriptionRepository applicationDescriptionRepository_;

    @Override
    @Before
    public void setup() throws IOException
    {
        super.setup();
        this.instanceStatementRepository_ = openRepository(ApplicationExecutionResultRepository.class);
        
        instance = new VirtualMachine();

        instance.setConfiguration(new VmConfiguration().setKeyName("keytest").setPlatform("linux").setPlatformUserName("ubuntu")
                .setPrivateIpAddress("127.0.0.1").setPublicDnsName("localhost").setPublicIpAddress("127.0.0.1"));
        instance.setImageId("ami-832b72ea").setLaunchTime(new Date()).setName("i-fd6125d3").setType(InstanceType.valueOf("t1.micro").setId(120))
                .setOwner(user).setLocation(zone);
        
        instance.setPlacement(new Placement());

        instance.setId(openRepository(InstanceRepository.class).insertInstance(instance));

        ScriptStatement statement = new ScriptStatement().setActive(YES).setStatement("who").setName("who");
        statement.setId(openRepository(ScriptStatementRepository.class).insertScriptStatement(statement));
        
        ApplicationExecDescription applicationExecDescription = new ApplicationExecDescription()
                .setApplication(statement)
                .setFailureAction(ABORT)
                .setName("app-name-1")
                .setResource(instance.getName())
                .setUser(user);
        
        applicationDescriptionRepository_ = openRepository(ApplicationDescriptionRepository.class);
        applicationExecDescription.setId(applicationDescriptionRepository_.insert(applicationExecDescription));

    }

    @Test
    public void must_insert_one_instance_statement()
    {
        List<ApplicationExecDescription> pendentExecutions = applicationDescriptionRepository_.listPendentExecutionsForInstance(instance.getId());
        assertThat(1, equalTo(pendentExecutions.size()));

        ApplicationExecutionResult is = new ApplicationExecutionResult()
                .setError("error")
                .setExitCode(0)
                .setStartedIn(new Date())
                .setFinishedIn(new Date())
                .setInstance(instance)
                .setOutput("I am ...")
                .setApplication(pendentExecutions.get(0));
        
        Integer instanceStatementId = instanceStatementRepository_.insertApplicationExecution(is);
        assertNotNull(instanceStatementId);
        assertThat(1, equalTo(instanceStatementId));
        is.setId(instanceStatementId);
        
        pendentExecutions = applicationDescriptionRepository_.listPendentExecutionsForInstance(instance.getId());
        assertThat(true, equalTo(pendentExecutions.isEmpty()));
        
        ApplicationExecutionResult is2 = instanceStatementRepository_.findById(instanceStatementId);
        assertCompletlyEquals(is, is2);
        
        List<ApplicationExecutionResult> instanceStatements = instanceStatementRepository_.listApplicationsExecutedOnInstance(instance.getId());
        assertThat(1, equalTo(instanceStatements.size()));
        assertCompletlyEquals(is, instanceStatements.get(0));
        
    }
    
    protected void assertCompletlyEquals(ApplicationExecutionResult is1, ApplicationExecutionResult is2)
    {
        assertThat(is1, equalTo(is2));
        assertThat(is1.getError(), equalTo(is2.getError()));
        assertThat(is1.getExitCode(), equalTo(is2.getExitCode()));
        assertThat(new Date(is1.getFinishedIn().getTime()), equalTo(new Date(is2.getFinishedIn().getTime())));
        assertThat(is1.getInstance(), equalTo(is2.getInstance()));
        assertThat(is1.getOutput(), equalTo(is2.getOutput()));
        assertThat(new Date(is1.getStartedIn().getTime()), equalTo(new Date(is2.getStartedIn().getTime())));
        assertThat(is1.getApplication(), equalTo(is2.getApplication()));
    }
}
