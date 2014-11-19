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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class AnnotationJsonDeserializer<T> implements JsonSerializer<T>, JsonDeserializer<T>
{
    public AnnotationJsonDeserializer()
    {
    }
    
    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        return null;
    }

    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context)
    {
        Field[] fields = src.getClass().getDeclaredFields();

        JsonObject element = new JsonObject();

        for (Field field : fields)
        {
           if (Modifier.isStatic(field.getModifiers()))
           {
               continue;
           }
           
            field.setAccessible(true);
            final String property = getProperty(field);

            if (property == null)
            {
                continue;
            }

            final Object value = getValue(field, src);

            if (!isComplexType(field.getType()))
            {
                element.addProperty(property, value == null ? null: value.toString());
            }
            else
            {
                element.add(property, context.serialize(value));
            }
        }

        return element;
    }

    private Object getValue(Field field, T obj)
    {
        try
        {
            return field.get(obj);
        }
        catch (IllegalArgumentException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String getProperty(Field field)
    {
        Annotation[] annotations = field.getAnnotations();
        String property = field.getName();

        if (annotations != null)
        {
            for (Annotation annotation : annotations)
            {
                if (XmlTransient.class.equals(annotation.annotationType()))
                {
                    property = null;
                    break;
                }
                else if (XmlElement.class.equals(annotation.annotationType()))
                {
                    property = ((XmlElement) annotation).name();
                }
                else if (XmlAttribute.class.equals(annotation.annotationType()))
                {
                    property = ((XmlAttribute) annotation).name();
                }
            }
        }
        return property;
    }

    public static boolean isComplexType(Class<?> clazz)
    {
        return (String.class.equals(clazz) || clazz.isPrimitive() || clazz.isEnum() || (clazz.getPackage() != null && ((clazz.getPackage().getName()
                .startsWith("java") && !isCollection(clazz))
                || clazz.getPackage().getName().startsWith("javax") || clazz.getPackage().getName().startsWith("sun")))) ? false : true;
    }

    public static boolean isCollection(Class<?> type)
    {
        return (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type));
    }
}
