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
package org.excalibur.core.cloud.service.json.serializers;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.reflections.Reflections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonFactory
{
    @SuppressWarnings("rawtypes")
    public static Gson create()
    {
        GsonBuilder builder = new GsonBuilder();
        Reflections reflections = new Reflections("org.excalibur");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(XmlRootElement.class);
        
        for(Class<?> type: annotated)
        {
            builder.registerTypeAdapter(type, new AnnotationJsonDeserializer());
        }
        
        return builder.create();
    }
}
