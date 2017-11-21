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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.RoundingMode;

import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.InstanceTypes;
import org.excalibur.core.domain.repository.InstanceTypeRepository;
import org.skife.jdbi.v2.DBI;

public class GenerateProvidersInstanceTypesTable extends TestSupport
{
    private InstanceTypeRepository instanceTypeRepository_;
    
    @Override
//    @Before
    public void setup() throws IOException
    {
//        super.setup();
//        ds = JdbcConnectionPool.create("jdbc:h2:tcp://localhost:6083/~/.excalibur/database/database", "", "");
        dbi = new DBI(ds);
        
        this.instanceTypeRepository_ = this.openRepository(InstanceTypeRepository.class);
        
        
//        String content = IOUtils2.readLines(new File("/home/alessandro/thesis/text/03-chapters/02-contributions/07-multi-objective-resource-allocation-for-cloud-federation/script/measure-network-throughput.sh"));
//        
//        ScriptStatement script = new ScriptStatement().setActive(YesNoEnum.YES).setName("bench-n1-highcpu-instances").setPlatform(Platform.LINUX)
//                .setStatement(content);
//        
//        script.setId(this.openRepository(ScriptStatementRepository.class)
//                .insertScriptStatement(script));
//        
//        ApplicationExecDescription application = new ApplicationExecDescription().setApplication(script).setFailureAction(FailureAction.RESTART)
//                .setName("n1-highcpu-2-bench").setNumberOfExecutions(24).setResource("n1-highcpu-2-bbb89ead04d1-0").setUser(new User().setId(4));
//        
//        this.openRepository(ApplicationDescriptionRepository.class).insert(application);
        
    }
    
//    @Test
    public void must_generate_one_table()
    {
        printProviderInstanceTypesTable(1, "us-east-1", 37);
        printProviderInstanceTypesTable(2, "us-central1-a", 15);
    }
    
    private void printProviderInstanceTypesTable(Integer providerId, String regionName, int expectedNumberOfInstances)
    {
        InstanceTypes providerInstanceTypes = InstanceTypes.valueOf(instanceTypeRepository_.getInstanceTypesOfProviderInRegion(providerId, regionName));
        assertEquals(expectedNumberOfInstances, providerInstanceTypes.size());
        
        print(providerInstanceTypes);
        System.out.println();
    }

    private void print(InstanceTypes types)
    {
        String header = "%15s|%13s|%11s|%17s|%15s|%12s|%34s|%34s";
        
        System.out.format(header, 
                "Instance type", 
                "Virtual cores", 
                "Memory (GB)", 
                "Cost (USD) / Hour", 
                "Family type", 
                "GFlops", 
                "Internal network throughput (Gbps)", 
                "External network throughput (Gbps)");
        
        System.out.println();
        
        for (InstanceType type: types)
        {
            System.out.format
            (
                    header, 
                    type.getName(), 
                    type.getConfiguration().getNumberOfCores(),
                    type.getConfiguration().getRamMemorySizeGb(),
                    type.getCost(),
                    type.getFamilyType().name().toLowerCase(), 
                    type.getConfiguration().getSustainablePerformanceGflops().setScale(4, RoundingMode.HALF_EVEN),
                    type.getConfiguration().getInternalNetworkThroughput(),
                    type.getConfiguration().getNetworkThroughput()
            );
            System.out.println();
        }
    }
}
