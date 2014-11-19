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
package org.excalibur.jackson.databind;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

public class JsonYamlObjectMapper extends ObjectMapper
{
    /**
     * Serial code version <code></code> for serialization.
     */
    private static final long serialVersionUID = 2452118007833072309L;

    public JsonYamlObjectMapper()
    {
        super(new YAMLFactory());
        
        this.configure(SerializationFeature.INDENT_OUTPUT, true);
        this.registerModule(new JaxbAnnotationModule());
    }
}
