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
package org.excalibur.client.commands.test;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.excalibur.client.commands.DeployCommand;
import org.excalibur.client.commands.DeployCommandOptions;
import org.junit.Before;
import org.junit.Test;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class DeployCommandTest
{

    JCommander jc;
    DeployCommand dc;

    @Before
    public void setUp()
    {
        jc = new JCommander();
        jc.addCommand("deploy", dc = new DeployCommand());
    }

    @Test
    public void must_create_a_valid_requirement_object()
    {
        jc.parse("deploy", "--cpus", "1", "--memory-size", "2", "--cost", "0.25", "--host", "localhost");
        assertEquals(dc.getName(), jc.getParsedCommand());

        DeployCommandOptions options = dc.getOptions();

        assertThat(1, equalTo(options.getNumberOfCpuCores()));
        assertThat(2, equalTo(options.getMemorySize()));
        assertThat(new BigDecimal(0.25), equalTo(options.getMaximalCostPerHour()));
    }

    @Test(expected = ParameterException.class)
    public void must_be_an_invalid_cloud_option()
    {
        jc.parse("deploy", "--clouds", "ec3");
    }
}
