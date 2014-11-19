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
package org.excalibur.fm.configuration.ui.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.table.AbstractTableModel;

import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.InstanceTypes;

@SuppressWarnings("serial")
public class InstanceTypeTableModel extends AbstractTableModel
{

    static final String[] COLUMN_TITLES = 
    { 
        "", "Instance type", "vCPU", "Memory (GB)",
        "Cost (USD)", "Family type", "Provider", "GFlops",
        "Network throughput"
    };
    
    private final InstanceTypes types_;

    private boolean[] selectedRows;

    public InstanceTypeTableModel(InstanceTypes types)
    {
        this.types_ = types;
        selectedRows = new boolean[types.size()];
    }

    @Override
    public int getRowCount()
    {
        return this.types_.size();
    }

    @Override
    public int getColumnCount()
    {
        return COLUMN_TITLES.length;
    }

    @Override
    public String getColumnName(int column)
    {
        return COLUMN_TITLES[column];
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        if (columnIndex == 0)
        {
            return true;
        }
        
        return super.isCellEditable(rowIndex, columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        if (columnIndex == 0)
        {
            this.selectedRows[rowIndex] = (boolean) aValue;
        }
        else
        {
            super.setValueAt(aValue, rowIndex, columnIndex);
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        Object value = null;

        if (!this.types_.isEmpty())
        {
            InstanceType row = this.types_.get(rowIndex);
            
            String provider;
            
            switch(row.getProvider().getName())
            {
            case "amazon": provider = "EC2"; break;
            default: provider = "GCE"; break;
            }

            switch (columnIndex)
            {
            case 0:
                return selectedRows[rowIndex];
            case 1:
                value = row.getName();
                break;
            case 2:
                value = row.getConfiguration().getNumberOfCores();
                break;
            case 3:
                value = row.getConfiguration().getRamMemorySizeGb();
                break;
            case 4:
                value = row.getCost().toPlainString();
                break;
            case 5:
                value = row.getFamilyType().name();
                break;
            case 6:
                value = row.getRegion().getName() + " (" + provider + ")" ;
                break;
            case 7:
                value = row.getConfiguration().getSustainablePerformanceGflops().setScale(4, RoundingMode.HALF_EVEN);
                break;
            case 8:
                value = row.getConfiguration().getNetworkThroughput();
                break;
            }
        }
        return value;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        switch (columnIndex)
        {
        case 0:
            return Boolean.class;
        case 3:
            return Double.class;
        case 7: 
            return BigDecimal.class;
        default:
            return String.class;
        }
    }
    
    public InstanceTypes getSelectedRows()
    {
        InstanceTypes instanceTypes = new InstanceTypes();
        
        for (int i = 0; i < this.selectedRows.length; i++)
        {
            if (selectedRows[i])
            {
                instanceTypes.add(this.types_.get(i));
            }
        }
        return instanceTypes;
    }
    
    public InstanceTypes getRows()
    {
        return this.types_;
    }
}
