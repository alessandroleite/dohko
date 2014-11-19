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
package org.excalibur.benchmark.test;

import static com.google.common.net.HostAndPort.fromParts;
import static org.excalibur.core.io.utils.IOUtils2.readLines;
import static org.springframework.util.ClassUtils.getDefaultClassLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.domain.InstanceTemplate;
import org.excalibur.core.cloud.api.domain.Instances;
import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.cloud.api.domain.Tags;
import org.excalibur.core.domain.UserProviderCredentials;
import org.excalibur.core.exec.ExecutableResponse;
import org.excalibur.core.io.utils.IOUtils2;
import org.excalibur.core.ssh.SshClient;
import org.excalibur.core.ssh.SshClientFactory;
import org.excalibur.core.util.Properties2;
import org.excalibur.core.util.SystemUtils2;
import org.excalibur.core.util.concurrent.DynamicExecutors;
import org.excalibur.core.util.concurrent.Futures2;
import org.excalibur.service.aws.ec2.EC2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListeningExecutorService;

public class EC2InstancesBenchmark
{
    private static final Logger LOG = LoggerFactory.getLogger(EC2InstancesBenchmark.class.getName());
    
    public static void main(String[] args) throws IOException
    {
        final String benchmark = "sp";
        final String outputDir = "/home/alessandro/excalibur/source/services/benchmarks/ec2/";
        
        final String[] scripts = 
        {
                readLines(getDefaultClassLoader().getResourceAsStream("org/excalibur/service/deployment/resource/script/iperf3.sh")), 
                readLines(getDefaultClassLoader().getResourceAsStream("org/excalibur/service/deployment/resource/script/linkpack.sh")),
                readLines(getDefaultClassLoader().getResourceAsStream("org/excalibur/service/deployment/resource/script/benchmarks/run_linpack_xeon64.sh")),
        };
        
        final String[] instanceTypes = {"c3.8xlarge", "r3.large", "r3.xlarge", "r3.2xlarge", "i2.xlarge"};
        
        final String privateKeyMaterial = IOUtils2.readLines(new File(SystemUtils2.getUserDirectory(), "/.ec2/leite.pem"));
        final File sshKey = new File(SystemUtils2.getUserDirectory(), "/.ec2/leite.pem");
        Properties properties = Properties2.load(getDefaultClassLoader().getResourceAsStream("aws-config.properties"));
        
        final LoginCredentials loginCredentials = new LoginCredentials.Builder()
                .identity(properties.getProperty("aws.access.key"))
                .credential(properties.getProperty("aws.secret.key"))
                .credentialName("leite")
                .build();
        
        final UserProviderCredentials userProviderCredentials = new UserProviderCredentials()
                .setLoginCredentials(loginCredentials)
                .setRegion(new Region("us-east-1").setEndpoint("https://ec2.us-east-1.amazonaws.com"));
        
        final EC2 ec2 = new EC2(userProviderCredentials);
        
        List<Callable<Void>> tasks = Lists.newArrayList();
        
        for (final String instanceType : instanceTypes)
        {
            tasks.add(new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    InstanceTemplate template = new InstanceTemplate()
                            .setImageId("ami-864d84ee")
                            .setInstanceType(InstanceType.valueOf(instanceType))
                            .setKeyName("leite")
                            .setLoginCredentials(loginCredentials.toBuilder().privateKey(privateKeyMaterial).build())
                            .setGroup(new org.excalibur.core.cloud.api.Placement().setZone("us-east-1a")) //.setGroupName("iperf-bench")
                            .setMinCount(1)
                            .setMaxCount(1)
                            .setInstanceName(String.format("%s-%s", instanceType, benchmark))
                            .setRegion(userProviderCredentials.getRegion())
                            .setTags(Tags.newTags(new org.excalibur.core.cloud.api.domain.Tag("benchmark", benchmark)));
                    
                    final Instances instances = ec2.createInstances(template);
                    
                    for (VirtualMachine instance: instances)
                    {
                        Preconditions.checkState(!Strings.isNullOrEmpty(instance.getConfiguration().getPlatformUserName()));
                        Preconditions.checkState(!Strings.isNullOrEmpty(instance.getConfiguration().getPublicIpAddress()));
                        
                        HostAndPort hostAndPort = fromParts(instance.getConfiguration().getPublicIpAddress(), 22);
                        
                        LoginCredentials sshCredentials = new LoginCredentials.Builder()
                                .authenticateAsSudo(true)
                                .privateKey(sshKey)
                                .user(instance.getConfiguration().getPlatformUserName())
                                .build();
                        
                        SshClient client = SshClientFactory.defaultSshClientFactory().create(hostAndPort, sshCredentials);
                        client.connect();
                        
                        try
                        {
                            for (int i = 0; i < scripts.length; i++)
                            {
                                ExecutableResponse response = client.execute(scripts[i]);
                                Files.write(response.getOutput().getBytes(), new File(outputDir, String.format("%s-%s.output.txt", template.getInstanceName(), i)));
                                LOG.info("Executed the script [{}] with exit code [{}], error [{}], and output [{}] ", new Object[] { scripts[i],
                                        String.valueOf(response.getExitStatus()), response.getError(), response.getOutput() });
                            }
                        }
                        finally
                        {
                            client.disconnect();
                        }
                        
                        ec2.terminateInstance(instance);
                    }
                    return null;
                }
            });            
        }
        
        ListeningExecutorService executor = 
                DynamicExecutors.newListeningDynamicScalingThreadPool("benchmark-instances-thread-%d");
        
        Futures2.invokeAllAndShutdownWhenFinish(tasks, executor);
        
        ec2.close();
    }
}
