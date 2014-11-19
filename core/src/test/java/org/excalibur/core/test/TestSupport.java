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
package org.excalibur.core.test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.domain.repository.UserRepository;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

public class TestSupport
{
    protected JdbcConnectionPool ds;
    protected DBI dbi;
    protected User user;     
    protected Zone zone;
    
    protected final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    @Before
    public void setup() throws IOException
    {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:" + UUID.randomUUID(), "sa", "sa");
        dbi = new DBI(ds);

        // see https://code.google.com/p/reflections/wiki/UseCases
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                               .addUrls(ClasspathHelper.forPackage("org.excalibur"))
                               .addScanners(new ResourcesScanner()));

        List<String> databaseScripts = Lists.newArrayList(reflections.getResources(new Predicate<String>()
        {
            @Override
            public boolean apply(@Nullable String input)
            {
                return input != null && input.endsWith(".sql");
            }
        }));

        Collections.sort(databaseScripts);

        Handle h = dbi.open();

        for (String script : databaseScripts)
        {
            LOG.debug("Executing the database script {}", script);
            h.execute(script);            
        }

        h.close();

        user = new User().setPassword("passwd").setUsername("username");
        user.setId(openRepository(UserRepository.class).insert(user));
        this.zone = openRepository(RegionRepository.class).findZoneByName("us-east-1a");
    }

    public <T> T openRepository(Class<T> klass)
    {
        if (dbi != null)
        {
            return dbi.open(klass);
        }
        return null;
    }

    @After
    public void tearDown() throws Exception
    {
        if (dbi != null)
        {
            ds.dispose();
        }
    }
}
