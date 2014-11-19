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
package org.excalibur.client.commands;

import java.math.BigDecimal;

import org.excalibur.client.jcommander.converters.ApplicationDescriptorConverter;
import org.excalibur.client.jcommander.converters.CloudProvidersEnumConverter;
import org.excalibur.client.jcommander.converters.HostAndPortConverter;
import org.excalibur.client.jcommander.validators.CloudProvidersValidator;
import org.excalibur.client.jcommander.validators.FileValidator;
import org.excalibur.client.jcommander.validators.PositiveBigDecimalValue;
import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.core.execution.domain.Requirements;
import org.springframework.beans.BeanUtils;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.BigDecimalConverter;
import com.beust.jcommander.validators.PositiveInteger;
import com.google.common.base.Objects;
import com.google.common.net.HostAndPort;

public class DeployCommandOptions
{
    @Parameter(names = { "--cores", "--vcpus", "--cpus" }, validateWith = PositiveInteger.class, description = "Minimal number of CPU cores. It might be greater than zero")
    private Integer numberOfCpuCores_;

    @Parameter(names = { "--mem-size", "--memory", "--memory-size" }, validateWith = PositiveInteger.class, description = "Minimal RAM memory size in gigabytes. Please, write only the number and it must be an integer value. Example: 1")
    private Integer memorySize_;

    @Parameter(names = { "--cost", "--maximum-cost" }, validateValueWith = PositiveBigDecimalValue.class, converter = BigDecimalConverter.class, description = "Maximal cost to pay per hour in USD")
    private BigDecimal maximalCostPerHour_;

    @Parameter(names = { "--max-instances-per-cloud", "--max-instances" }, validateWith = PositiveInteger.class, description = "Maximum number of instances per cloud provider")
    private Integer numberOfInstancesPerCloud_;

    @Parameter(names = { "--providers", "--clouds" }, converter = CloudProvidersEnumConverter.class, validateWith = CloudProvidersValidator.class, description = "Name of the cloud providers separated by comma (,). Valid values are: Azure, EC2, GCE")
    private CloudProvidersEnum[] clouds_;

    @Parameter(names = { "--df", "--app-deployment", "--adf", "--f", "--file" }, validateWith = FileValidator.class, converter = ApplicationDescriptorConverter.class, description = "The application deployment file")
    private ApplicationDescriptor applicationDescriptor_;

    @Parameter(names = { "--host" }, converter = HostAndPortConverter.class, required = true, description = "Host and port of the server to submit the deployment descriptor")
    private HostAndPort hostAndPort;

    /**
     * @return the numberOfCpuCores
     */
    public Integer getNumberOfCpuCores()
    {
        return numberOfCpuCores_;
    }

    /**
     * @param numberOfCpuCores
     *            the numberOfCpuCores to set
     */
    public DeployCommandOptions setNumberOfCpuCores(Integer numberOfCpuCores)
    {
        this.numberOfCpuCores_ = numberOfCpuCores;
        return this;
    }

    /**
     * @return the memorySize
     */
    public Integer getMemorySize()
    {
        return memorySize_;
    }

    /**
     * @param memorySize
     *            the memorySize to set
     */
    public DeployCommandOptions setMemorySize(Integer memorySize)
    {
        this.memorySize_ = memorySize;
        return this;
    }

    /**
     * @return the maximalCostPerHour
     */
    public BigDecimal getMaximalCostPerHour()
    {
        return maximalCostPerHour_;
    }

    /**
     * @param maximalCostPerHour
     *            the maximalCostPerHour to set
     */
    public DeployCommandOptions setMaximalCostPerHour(BigDecimal maximalCostPerHour)
    {
        this.maximalCostPerHour_ = maximalCostPerHour;
        return this;
    }

    /**
     * @return the numberOfInstancesPerCloud
     */
    public Integer getNumberOfInstancesPerCloud()
    {
        return numberOfInstancesPerCloud_;
    }

    /**
     * @param numberOfInstancesPerCloud
     *            the numberOfInstancesPerCloud to set
     */
    public DeployCommandOptions setNumberOfInstancesPerCloud(Integer numberOfInstancesPerCloud)
    {
        this.numberOfInstancesPerCloud_ = numberOfInstancesPerCloud;
        return this;
    }

    /**
     * @return the clouds
     */
    public CloudProvidersEnum[] getClouds()
    {
        return clouds_;
    }

    /**
     * @param clouds
     *            the clouds to set
     */
    public DeployCommandOptions setClouds(CloudProvidersEnum[] clouds)
    {
        this.clouds_ = clouds;
        return this;
    }

    /**
     * @return the applicationDescriptor
     */
    public ApplicationDescriptor getApplicationDescriptor()
    {
        return applicationDescriptor_;
    }

    /**
     * @param applicationDescriptor
     *            the applicationDescriptor to set
     */
    public DeployCommandOptions setApplicationDescriptor(ApplicationDescriptor applicationDescriptor)
    {
        this.applicationDescriptor_ = applicationDescriptor;
        return this;
    }

    /**
     * @return the hostAndPort
     */
    public HostAndPort getHostAndPort()
    {
        return hostAndPort;
    }

    /**
     * @param hostAndPort
     *            the hostAndPort to set
     */
    public DeployCommandOptions setHostAndPort(HostAndPort hostAndPort)
    {
        this.hostAndPort = hostAndPort;
        return this;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("number-of-cpu-cores", this.getNumberOfCpuCores()).add("memory-size", this.getMemorySize())
                .add("maximum-cost-per-hour", this.getMaximalCostPerHour()).add("number-of-instances-per-cloud", this.getNumberOfInstancesPerCloud())
                .add("cloud-providers", this.getClouds()).add("application-descriptor", this.getApplicationDescriptor()).omitNullValues().toString();
    }

    public Requirements getRequirements()
    {
        Requirements requirements = new Requirements();
        BeanUtils.copyProperties(this, requirements);

        return requirements;
    }

    public static enum CloudProvidersEnum
    {
        AZURE("microsoft"), EC2("amazon"), GCE("google");

        private final String provider_;

        private CloudProvidersEnum(String provider)
        {
            this.provider_ = provider;
        }

        /**
         * @return the provider
         */
        public String getProvider()
        {
            return provider_;
        }

        public static CloudProvidersEnum valueOfFrom(String name)
        {
            for (CloudProvidersEnum provider : values())
            {
                if (provider.getProvider().equalsIgnoreCase(name))
                {
                    return provider;
                }
            }

            return null;
        }
    }
}
