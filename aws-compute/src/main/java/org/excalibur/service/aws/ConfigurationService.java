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
package org.excalibur.service.aws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.elasticmapreduce.model.InstanceState;
import com.google.common.base.Preconditions;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ConfigurationService
{
    public static enum InstanceStateType
    {
        PENDING("pending"), RUNNING("running"), SHUTTING_DOWN("shutting-down"), TERMINATED("terminated"), STOPPING("stopping"), STOPPED("stopped");

        private String value_;

        private InstanceStateType(String value)
        {
            this.value_ = value;
        }

        @Override
        public String toString()
        {
            return this.value_;
        }

        public String getValue()
        {
            return value_;
        }

        public static InstanceStateType fromValue(String value)
        {
            if (value == null || value.trim().isEmpty())
            {
                throw new IllegalArgumentException();
            }

            for (InstanceStateType type : InstanceStateType.values())
            {
                if (type.value_.equals(value))
                {
                    return type;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    private static final Properties AWS_PROPERTIES = new Properties();

    static
    {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("aws-config.properties");
        
        try
        {
            AWS_PROPERTIES.load(Preconditions.checkNotNull(is, "The aws-config.properties not found in the classpath."));
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    private final AmazonEC2 ec2_;

    public ConfigurationService()
    {
        ec2_ = new AmazonEC2Client(
                new BasicAWSCredentials(AWS_PROPERTIES.getProperty("aws.access.key"), AWS_PROPERTIES.getProperty("aws.secret.key")));
    }

    public List<Instance> getInstances()
    {
        List<Instance> instances = new ArrayList<Instance>();

        DescribeInstancesResult describeInstances = ec2_.describeInstances();
        List<Reservation> reservations = describeInstances.getReservations();

        for (int i = 0; i < reservations.size(); i++)
        {
            instances.addAll(reservations.get(i).getInstances());
        }

        return Collections.unmodifiableList(instances);
    }

    public List<Instance> filterInstances(InstanceStateType state, List<Instance> instances)
    {
        List<Instance> instancesInState = new ArrayList<Instance>();

        for (Instance instance : instances)
        {
            if (state.getValue().equals(instance.getState().getName()))
            {
                instancesInState.add(instance);
            }
        }

        return instancesInState;
    }

    public static void main(String[] args) throws JSchException, IOException 
    {
        ConfigurationService service = new ConfigurationService();
        //Instance instance = service.filterInstances(InstanceStateType.RUNNING, service.getInstances()).get(0);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        JSch shell = new JSch();
        shell.addIdentity(System.getProperty("user.home") + "/.ec2/leite.pem");
//        Session session = shell.getSession("ubuntu", instance.getPublicDnsName());
//        session.setConfig(config);
//        session.connect();
//
//        Channel channel = session.openChannel("shell");
//        channel.setInputStream(System.in);
//        channel.setOutputStream(System.out);
//        channel.connect();
        
        
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
                 .withInstanceType("t1.micro")
                 .withImageId("ami-832b72ea")
                 .withMinCount(18)
                 .withMaxCount(18)
                 .withSecurityGroupIds("sg-e352488a")
                 .withKeyName("leite");
        
        System.out.println(new Date());
        RunInstancesResult runInstances = service.ec2_.runInstances(runInstancesRequest);
        List<Instance> instances = runInstances.getReservation().getInstances();
        
        for(Instance inst: instances)
        {
            System.out.printf("id:%s, dns:%s lauch time:%s, state:%s\n", inst.getInstanceId(), inst.getPublicDnsName(), inst.getLaunchTime(), inst.getState().getName());
            
            Session session = shell.getSession("ubuntu", inst.getPublicDnsName());
            session.setConfig(config);
            
            if (InstanceState.RUNNING.equals(inst.getState()))
            {
                session.connect();
                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                channel.connect();
                
                channel = (ChannelExec) session.openChannel("exec");
                BufferedReader br = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                channel.setCommand("uname -a && date && uptime && who");
                channel.connect();
                
                while (true)
                {
                    String line = br.readLine();
                    if (line == null)
                    {
                        br.close();
                        break;
                    }
                    System.out.println(line);
                }
            }
        }
        
        System.out.println(instances.size());
    }
}
