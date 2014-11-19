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
package org.excalibur.bio.tools.infernal.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.excalibur.core.converters.Converter;
import org.excalibur.core.converters.StringToDoubleConverter;
import org.excalibur.core.converters.StringToIntegerConverter;

import com.google.common.base.Preconditions;

@SuppressWarnings("rawtypes")
public final class HitTableRecord implements Serializable, Iterable<Field>
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
	private static final long serialVersionUID = 7780179651880561658L;

	/**
     * Labels of the hit table as defined in the <a href="ftp://selab.janelia.org/pub/software/infernal/Userguide.pdf">Tabular output formats</a>,
     * page 63.
     */
    private static final FieldDefinition[] FIELDS_DEFINITIONS = new FieldDefinition[18];

    private static class FieldDefinition<T extends Serializable>
    {
        private final String name_;
        private final Converter<String, T> converter_;

        private FieldDefinition(String name, Converter<String, T> converter)
        {
            this.name_ = name;
            this.converter_ = converter;
        }

        @SuppressWarnings("unchecked")
        private FieldDefinition(String name)
        {
            this(name, (Converter<String, T>) new NullConverter());
        }

        private Field<T> create(String value)
        {
            return new Field<T>(this.name_, converter_.convert(value));
        }

        private static class NullConverter implements Converter<String, String>
        {
            @Override
            public String convert(String input)
            {
                return input;
            }
        }
    }

    static
    {
        int i = 0;

        FIELDS_DEFINITIONS[i++] = new FieldDefinition<String>("target_name");
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<String>("target_accession");
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<String>("query_name");
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<String>("accession");
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<String>("mdl");
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<Integer>("mdl_from", new StringToIntegerConverter());
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<Integer>("mdl_to", new StringToIntegerConverter());
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<Integer>("seq_from", new StringToIntegerConverter());
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<Integer>("seq_to", new StringToIntegerConverter());
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<String>("strand");
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<String>("trunc");
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<Integer>("pass", new StringToIntegerConverter());
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<Double>("gc", new StringToDoubleConverter());
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<Double>("bias", new StringToDoubleConverter());
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<Double>("score", new StringToDoubleConverter());
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<Double>("evalue", new StringToDoubleConverter());
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<String>("inc");
        FIELDS_DEFINITIONS[i++] = new FieldDefinition<String>("target_description");
    }

    /**
     * The fields of the table of hits.
     */
    private final Map<String, Field> fields_ = new LinkedHashMap<String, Field>();

    /**
     * The {@link String} that represents a tabular record.
     */
    private String recordOrigin_;

    /**
     * Creates and returns a new {@link HitTableRecord} converted the {@link String} in the correspondent {@link Field}.
     * 
     * @param value
     *            string with the columns as defined in the infernal tabular output specification. Might not be <code>null</code>.
     * @return A new {@link HitTableRecord} instance.
     * @throws NullPointerException
     *             If the {@code value} is <code>null</code>.
     * @throws IllegalArgumentException
     *             If the {@code value} does not have 18 space delimited fields.
     */
    public static HitTableRecord valueOf(String value)
    {
        String[] values = Preconditions.checkNotNull(value).split("\\s+");
        Preconditions.checkArgument(values.length == FIELDS_DEFINITIONS.length);
        HitTableRecord record = new HitTableRecord();
        record.recordOrigin_ = value;

        for (int i = 0; i < FIELDS_DEFINITIONS.length; i++)
        {
            record.fields_.put(FIELDS_DEFINITIONS[i].name_, FIELDS_DEFINITIONS[i].create(values[i]));
        }
        return record;
    }

    /**
     * Private constructor to force the use of the factory method.
     * 
     * @see HitTableRecord#valueOf(String)
     */
    private HitTableRecord()
    {
        super();
    }

    /**
     * Returns a {@link Field} that has the given name or <code>null</code> if it does not exist.
     * 
     * @param name
     *            The name of the {@link Field} to be returned.
     * @return The {@link Field} with the given name or <code>null</code> if it does not exist.
     */
    public Field get(String name)
    {
        return fields_.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getFieldValue(String name)
    {
        Field<T> field = (Field<T>) this.fields_.get(name);
        return field != null ? field.getValue() : null;
    }

    /**
     * Returns an unmodified {@link List} with all fields.
     * 
     * @return A read-only list with the fields.
     */
    public List<Field> getAllFields()
    {
        return Collections.unmodifiableList(new ArrayList<Field>(fields_.values()));
    }

    /**
     * Returns a read-only {@link Map} that the key is the name of a {@link Field} and the value the {@link Field} instance.
     * 
     * @return A map with all fields.
     */
    public Map<String, Field> getFieldMap()
    {
        return Collections.unmodifiableMap(this.fields_);
    }

    @Override
    public Iterator<Field> iterator()
    {
        return fields_.values().iterator();
    }

    @Override
    public String toString()
    {
        return this.recordOrigin_;
    }
}
