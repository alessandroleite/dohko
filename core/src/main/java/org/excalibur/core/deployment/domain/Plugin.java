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
package org.excalibur.core.deployment.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "plugin")
@XmlType(name = "plugin", propOrder = { "script_", "params_" })
public class Plugin
{
    @XmlAttribute(name = "script", required = true)
    private String script_;

    @XmlElement(name = "param")
    private final List<Param> params_ = new ArrayList<Param>();

    public Plugin withScript(String script)
    {
        this.script_ = script;
        return this;
    }

    public Plugin withParam(Param param)
    {
        return this.withParams(param);
    }

    public Plugin withParams(Param... params)
    {
        for (Param param : params)
        {
            this.params_.add(param);
        }

        return this;
    }

    public Plugin removeParam(Param param)
    {
        this.params_.remove(param);
        return this;
    }

    public Plugin removeParams(Param... params)
    {
        if (params != null)
        {
            for (Param param : params)
            {
                this.params_.remove(param);
            }
        }
        return this;
    }

    /**
     * @return the script
     */
    public String getScript()
    {
        return script_;
    }

    /**
     * @param script
     *            the script to set
     */
    public void setScript(String script)
    {
        this.script_ = script;
    }

    /**
     * @return the params
     */
    public List<Param> getParams()
    {
        return Collections.unmodifiableList(params_);
    }

}
