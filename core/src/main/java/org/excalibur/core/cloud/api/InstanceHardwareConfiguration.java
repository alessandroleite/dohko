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
package org.excalibur.core.cloud.api;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.ManagedProperty;

import static com.google.common.base.Preconditions.*;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "instance-hardware-configuration")
@XmlType(name = "instance-hardware-configuration")
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class InstanceHardwareConfiguration extends ManagedProperty implements Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -6450014521274660543L;
   
    private static final String COMPUTE_UNIT              = "compute_units";
    private static final String COMPUTE_UNIT_NC           = "compute_units_nc";
    private static final String MEMORY_SIZE_GB            = "memory";
    private static final String TYPE                      = "type";
    private static final String DISK_UNIT                 = "disk_units";
    private static final String DISK_UNIT_TYPE            = "disk_type";
    private static final String DISK_UNIT_SIZE_GB         = "disk_unit_capacity";
    private static final String UB_INSTANCES              = "ubi";
    private static final String GENERATION                = "gn";
    private static final String INT_NETWORK_THROUGHPUT    = "int_network_throughput";
    private static final String EXT_NETWORK_THROUGHPUT    = "ext_network_throughput";
    private static final String NETWORK_LATENCY           = "network_latency";
    private static final String SUST_PERFORMANCE_GFLOPS   = "sustainable_performance";
//    private static final String UNIX_BENCH_SCORE          = "unix_bench_performance";
    
    private static final BigDecimal ONE_COMPUTING_UNIT    = new BigDecimal("4.4");

    public InstanceHardwareConfiguration()
    {
        super(InstanceHardwareConfiguration.class.getName());
    }
    
    public InstanceHardwareConfiguration(InstanceType type)
    {
        super(InstanceHardwareConfiguration.class.getName());
        setVmType(type);
    }

    public InstanceHardwareConfiguration(InstanceType type, Integer numberOfComputeUnits, Integer numberOfCores, Double memorySize)
    {
        this(type);
        this.updateValue(COMPUTE_UNIT, numberOfComputeUnits);
        this.updateValue(COMPUTE_UNIT_NC, numberOfCores);
        this.updateValue(MEMORY_SIZE_GB, memorySize);
    }

    public InstanceHardwareConfiguration(InstanceHardwareConfiguration ref)
    {
        this(ref.getType(), ref.getNumberOfComputeUnits(), ref.getNumberOfCores(), ref.getRamMemorySizeGb());
        this.setDiskSizeGb(ref.getDiskSizeGb());
        this.setDisks(ref.getNumberOfDisks(), ref.getDiskSizeGb(), DiskTechnology.valueOf(ref.getDiskType()));
        this.setMaximumNumberOfInstances(ref.getMaximumNumberOfInstances());
    }

    @XmlElement(name="generation")
    public Integer getGeneration()
    {
        return this.getValue(GENERATION);
    }

    @XmlTransient
    public Integer getId()
    {
        return this.getType().getId();
    }

    @XmlElement(name="compute-units")
    public Integer getNumberOfComputeUnits()
    {
        Integer value = this.getValue(COMPUTE_UNIT);
        return value == null ? 1 : value;
    }

    /**
     * The maximum number of instances allowed for a {@link InstanceType}.
     */
    @XmlElement(name="maximum-instances-allowed")
    public Integer getMaximumNumberOfInstances()
    {
        return this.getValue(UB_INSTANCES);
    }

    @XmlElement(name="cpu-cores")
    public Integer getNumberOfCores()
    {
        return this.getValue(COMPUTE_UNIT_NC);
    }

    @XmlElement(name="ram-size-gb")
    public Double getRamMemorySizeGb()
    {
        return this.getValue(MEMORY_SIZE_GB);
    }

    @XmlElement(name="disk-unit-size-gb")
    public Long getDiskSizeGb()
    {
        Long value = this.getValue(DISK_UNIT_SIZE_GB);
        return value == null ? 0 : value;
    }
    
    @XmlElement(name="disk-type")
    public String getDiskType()
    {
        return this.getValue(DISK_UNIT_TYPE);
    }
    
    @XmlElement(name="disk-numbers")
    public Integer getNumberOfDisks()
    {
        Integer value = this.getValue(DISK_UNIT);
        return value == null ? 0 : value;
    }
    
    /**
     * Returns the external network throughput in Gbps
     * @return The network throughput in Gbps.
     */
    @XmlElement(name = "ext-network-throughput")
    public Double getNetworkThroughput()
    {
        Double value = this.getValue(EXT_NETWORK_THROUGHPUT);
        return value == null ? 0 : value;
    }
    
    /**
     * Returns the internal network throughput in Gbps
     * @return The internal network throughput in Gbps.
     */
    @XmlElement(name = "int-network-throughput")
    public Double getInternalNetworkThroughput()
    {
        return this.getValue(INT_NETWORK_THROUGHPUT, 0.0);
    }
    
    
    @XmlElement(name = "network-latency")
    public Double getNetworkLatency()
    {
        return this.getValue(NETWORK_LATENCY, 0.0);
    }
    
    @XmlElement(name="sustainable-performance-gflops")
    public BigDecimal getSustainablePerformanceGflops()
    {
        BigDecimal defaultValue = BigDecimal.valueOf(this.getNumberOfComputeUnits()).multiply(ONE_COMPUTING_UNIT);
        return this.getValue(SUST_PERFORMANCE_GFLOPS, defaultValue);
    }

    @XmlTransient
    public InstanceType getType()
    {
        return this.getValue(TYPE);
    }

    public InstanceHardwareConfiguration setGeneration(Integer generation)
    {
        checkState(generation != null && generation > 0);
        this.updateValue(GENERATION, generation);
        return this;
    }

    public InstanceHardwareConfiguration setNumberOfComputUnits(Integer value)
    {
        checkState(value != null && value > 0);
        this.updateValue(COMPUTE_UNIT, value);
        return this;
    }

    public InstanceHardwareConfiguration setMaximumNumberOfInstances(Integer value)
    {
        this.updateValue(UB_INSTANCES, value);
        return this;
    }

    public InstanceHardwareConfiguration setNumberOfCores(Integer number)
    {
        checkState(number != null && number > 0);
        this.updateValue(COMPUTE_UNIT_NC, number);
        return this;
    }


    public InstanceHardwareConfiguration setRamMemorySizeGb(double memorySize)
    {
        checkState(memorySize > 0);
        this.updateValue(MEMORY_SIZE_GB, memorySize);
        return this;
    }

    public InstanceHardwareConfiguration setDiskSizeGb(Long diskCapacity)
    {
        this.updateValue(DISK_UNIT_SIZE_GB, diskCapacity);
        return this;
    }
    
    public InstanceHardwareConfiguration setNumberOfDisks(Integer numberOfDisks, DiskTechnology type)
    {
        if (numberOfDisks != null && numberOfDisks > 0 && type != null)
        {
            this.updateValue(DISK_UNIT, numberOfDisks);
            this.updateValue(DISK_UNIT_TYPE, type.name());
        }
        
        return this;
    }
    
    public InstanceHardwareConfiguration setDisks(Integer numberOfDisks, Long diskSize, DiskTechnology type)
    {
        setNumberOfDisks(numberOfDisks, type);
        this.updateValue(DISK_UNIT_SIZE_GB, diskSize);
        
        return this;
    }
    
    public InstanceHardwareConfiguration setVmType(InstanceType type)
    {
        this.updateValue(TYPE, checkNotNull(type));
        return this;
    }
    
    public InstanceHardwareConfiguration setNetworkThroughput(Double throughput)
    {
        this.updateValue(EXT_NETWORK_THROUGHPUT, throughput);
        return this;
    }
    
    public InstanceHardwareConfiguration setInternalNetworkThroughput(Double throughput)
    {
        this.updateValue(INT_NETWORK_THROUGHPUT, throughput);
        return this;
    }
    
    public InstanceHardwareConfiguration setNetworkLatency(Double latency)
    {
        this.updateValue(NETWORK_LATENCY, latency);
        return this;
    }
    
    public InstanceHardwareConfiguration setSustainablePerformanceGflops(BigDecimal value)
    {
        if (value != null && value.intValue() > 0)
        {
            this.updateValue(SUST_PERFORMANCE_GFLOPS, value);
        }
        
        return this;
    }

    @Override
    public InstanceHardwareConfiguration clone()
    {
        return new InstanceHardwareConfiguration(this);
    }
    
    @Override
    public String toString()
    {
       return super.toString(TYPE);
    }
}
