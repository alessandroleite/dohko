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
package org.excalibur.fm.configuration.io.util;

import java.io.IOException;
import java.io.OutputStream;

import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.InstanceTypes;

public class ExportInstanceTypes
{
    private final InstanceTypes instanceTypes_;
    private final OutputStream output_;

    public ExportInstanceTypes(InstanceTypes instanceTypes, OutputStream output)
    {
        this.instanceTypes_ = instanceTypes;
        this.output_ = output;
    }

    public String export() throws IOException
    {
        StringBuilder sb = new StringBuilder();
        String[] header = { "provider", "type", "n_cores", "memory", "cost", "family", "region" };

        appendSeparatedByTabular(header, sb);

        for (InstanceType type : this.instanceTypes_)
        {
            String[] values = 
            { 
                    type.getProvider().getName(), 
                    type.getName(),
                    type.getConfiguration().getNumberOfCores().toString(),
                    type.getConfiguration().getRamMemorySizeGb().toString(),
                    type.getCost().toPlainString(), 
                    type.getFamilyType().name(), 
                    type.getRegion().getName() 
            };

            appendSeparatedByTabular(values, sb);
        }
        
        output_.write(sb.toString().getBytes());
        
        return sb.toString();
    }

    private void appendSeparatedByTabular(String[] values, StringBuilder sb)
    {
        for (int i = 0; i < values.length; i++)
        {
            if (i == 0)
            {
                sb.append(values[i]);
            }
            else
            {
                sb.append("\t").append(values[i]);
            }
        }
        sb.append("\n");
    }
}
