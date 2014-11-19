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
import javax.swing.DefaultComboBoxModel;
import org.excalibur.core.cloud.api.Provider;


@SuppressWarnings("serial")
public class ProviderComboBoxModel extends DefaultComboBoxModel<String>
{
    private final List<Provider> providers_;
    
    public ProviderComboBoxModel(List<Provider> providers)
    {
        this.providers_ = providers;
        
        this.addElement("All");
        
        for(Provider provider: providers)
        {
            this.addElement(provider.getName());
        }
    }

    @Override
    public String getElementAt(int index) 
    {
        return index <= 0 ? "All" : this.providers_.get(index - 1).getName();
    }

    @Override
    public String getSelectedItem() 
    {        
        return this.getElementAt(this.getIndexOf(super.getSelectedItem()));
    }      
}
