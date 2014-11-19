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

import java.util.List;
import org.excalibur.core.cloud.api.domain.GeographicRegion;

import static com.google.common.base.Preconditions.*;
import javax.swing.DefaultComboBoxModel;

@SuppressWarnings({ "serial" })
public class GeographicRegionComboBoxModel extends DefaultComboBoxModel<String>
{            
    private final List<GeographicRegion> regions_;
    
    public GeographicRegionComboBoxModel(List<GeographicRegion> regions)
    {
        regions_ = checkNotNull(regions);
        addElement("All");
        
        for (GeographicRegion region: regions)
        {
            this.addElement(region.getName());
        }
        
    }

    @Override
    public String getElementAt(int index) 
    {                
        return index <= 0 ? "All" :  this.regions_.get(index - 1).getName();
    }

    @Override
    public String getSelectedItem() 
    {        
        int index = super.getIndexOf(super.getSelectedItem());        
        return this.getElementAt(index);
    }   
}
