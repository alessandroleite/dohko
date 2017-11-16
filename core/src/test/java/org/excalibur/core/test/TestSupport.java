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

import javax.annotation.Nullable;

import org.apache.commons.dbcp.BasicDataSource;
import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.domain.repository.UserRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import io.dohko.jdbi.BigIntegerArgumentFactory;
import io.dohko.jdbi.JodaTimeArgumentFactory;
import io.dohko.jdbi.OptionalArgumentFactory;
import io.dohko.jdbi.OptionalContainerFactory;
import io.dohko.jdbi.args.JodaDateTimeMapper;
import io.dohko.jdbi.util.BigIntegerMapper;

import static java.lang.System.getProperty;
import static java.lang.String.format;

public class TestSupport
{
	protected static DB db;
	protected static int dbport;
	protected static String dbname;
	protected static BasicDataSource ds;
	
    protected DBI dbi;
    protected User user;     
    protected Zone zone;
    
    private static final java.util.Random random = new java.util.Random();
    
    protected final static Logger LOG = LoggerFactory.getLogger(TestSupport.class.getName());
    
    @BeforeClass
    public static void newEmbeddedDB() throws ManagedProcessException
    {
    	dbport = random.nextInt(4100) + 3306;
    	dbname = getProperty("org.excalibur.database.name", "dohko");
    	
    	db = DB.newEmbeddedDB(dbport);
    	db.start();
    	db.createDB(dbname);
    	addVMShutdownHook();
    }

    @Before
    public void setup() throws IOException, ManagedProcessException
    {
//    	final int dbport = random.nextInt(4100) + 3306;
//    	final String dbname = getProperty("org.excalibur.database.name", "dohko");
    	
//    	startEmbeddedDB(dbport, dbname);
    	db.run(format("drop database %s", dbname));
    	db.createDB(dbname);
    	
    	createAndConfigureDatasource(dbport, dbname);
//    	addVMShutdownHook();
        
        dbi = new DBI(ds);
        dbi.registerArgumentFactory(new JodaTimeArgumentFactory());
        dbi.registerArgumentFactory(new OptionalArgumentFactory());
        dbi.registerArgumentFactory(new BigIntegerArgumentFactory());
        dbi.registerContainerFactory(new OptionalContainerFactory());

        dbi.registerMapper(new BigIntegerMapper());
        dbi.registerMapper(new JodaDateTimeMapper());

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
            db.source(script);
        }

        h.close();

        user = new User().setPassword("passwd").setUsername("username");
        user.setId(openRepository(UserRepository.class).insert(user));
        zone = openRepository(RegionRepository.class).findZoneByName("us-east-1a");
        
    }

	private static void addVMShutdownHook() 
	{
		Runtime.getRuntime().addShutdownHook(new Thread(() -> 
        {
        	try 
        	{
				db.stop();
//				if (ds != null)
//				{
//					ds.close();
//				}
			} 
        	catch (ManagedProcessException e) 
        	{
//        		LOG.error(e.getMessage(), e);
			}
        	
        }));
	}

//	private void startEmbeddedDB(final int dbport, final String dbname) throws ManagedProcessException 
//	{
//		db = DB.newEmbeddedDB(dbport);
//		
//		LOG.info("Starting database server on port [{}]", dbport);
//    	db.start();
//
//    	LOG.info("Creating database [{}]", dbname);
//    	db.createDB(dbname);
//	}

	private void createAndConfigureDatasource(final int dbport, final String dbname) 
	{
		ds = new BasicDataSource();
    	ds.setUrl(getProperty("org.excalibur.database.jdbc.test.url", format("jdbc:mysql://localhost:%s/%s", dbport, dbname)));
    	ds.setUsername(getProperty("org.excalibur.database.jdbc.test.username", "root"));
    	ds.setPassword(getProperty("org.excalibur.database.jdbc.test.password", ""));
    	
    	ds.setInitialSize(2);
    	ds.setMinIdle(2);
    	ds.setMaxActive(10);
    	ds.setTestOnBorrow(true);
    	ds.setTestOnReturn(true);
    	ds.setValidationQuery("SELECT 1 + 1");
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
//    	if (db != null)
//    	{
//    		db.stop();
//    	}
    	
        if (dbi != null)
        {
        	ds.close();
        }
    }
    
    @AfterClass
    public static void stopdb() throws ManagedProcessException
    {
    	if (db != null)
    	{
    		db.stop();
    	}

    }
    
    protected User getUser() 
    {
		return user;
	}
}
