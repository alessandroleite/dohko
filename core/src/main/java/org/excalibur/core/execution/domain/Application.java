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
package org.excalibur.core.execution.domain;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Function;

import static org.excalibur.core.util.Strings2.*;
import static com.google.common.base.Objects.*;
import static org.excalibur.core.util.YesNoEnum.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "application")
@XmlType(name = "application", propOrder = {"id_", "name_", "commandLine_", "files_" })
public class Application implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 6769888954321427115L;
    
    @XmlTransient
    private Integer internalId_;

    /**
     * Opaque application's id.
     */
    @XmlElement(name = "id", required = true, nillable = false)
    private String id_;
    

    /**
     * Application's name.
     */
    @XmlElement(name = "name", required = true, nillable = false)
    private String name_;

    @XmlElement(name = "command-line", required = true, nillable = false)
    private String commandLine_;

    @XmlElementWrapper(name = "files")
    @XmlElement(name = "file")
    private final List<AppData> files_ = new ArrayList<AppData>();
    
    @XmlTransient
    private ApplicationDescriptor job_;
    
    @XmlTransient
    private TaskStatus status_ = TaskStatus.PENDING;
    
    @XmlTransient
    private String plainText_;
    
    public Application()
    {
        super();
    }
    
    public Application(String id)
    {
        this.id_ = id;
    }

    public Application addData(AppData appData)
    {
        if (appData != null)
        {
            this.files_.add(appData);
        }

        return this;
    }

    public Application addAll(Iterable<AppData> data)
    {
        if (data != null)
        {
            for (AppData datum : data)
            {
                this.addData(datum);
            }
        }

        return this;
    }

    public Application setData(AppData appData)
    {
        return this.addData(appData);
    }

    public Application removeData(AppData appData)
    {
        this.files_.remove(appData);

        return this;
    }
    

    /**
     * @return the internalId
     */
    public Integer getInternalId()
    {
        return internalId_;
    }

    /**
     * @param internalId the internalId to set
     */
    public Application setInternalId(Integer internalId)
    {
        this.internalId_ = internalId;
        return this;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id_;
    }

    /**
     * @param id the id to set
     */
    public Application setId(String id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name_;
    }

    /**
     * @param name
     *            the name to set
     */
    public Application setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the commandLine
     */
    public String getCommandLine()
    {
        return commandLine_;
    }

    /**
     * @param commandLine
     *            the commandLine to set
     */
    public Application setCommandLine(String commandLine)
    {
        this.commandLine_ = commandLine;
        return this;
    }

    /**
     * @return the files
     */
    public List<AppData> getFiles()
    {
        return Collections.unmodifiableList(files_);
    }
    
    /**
     * @return the job
     */
    public ApplicationDescriptor getJob()
    {
        return job_;
    }

    /**
     * @param job the job to set
     */
    public Application setJob(ApplicationDescriptor job)
    {
        this.job_ = job;
        return this;
    }

    /**
     * @return the status
     */
    public TaskStatus getStatus()
    {
        return status_;
    }

    /**
     * @param status the status to set
     */
    public Application setStatus(TaskStatus status)
    {
        this.status_ = status;
        return this;
    }

    /**
     * @return the plainText
     */
    public String getPlainText()
    {
        return plainText_;
    }

    /**
     * @param plainText the plainText to set
     */
    public Application setPlainText(String plainText)
    {
        this.plainText_ = plainText;
        return this;
    }

    @Override
    public Application clone()
    {
        Object cloned;

        try
        {
            cloned = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            cloned = new Application()
                    .addAll(this.getFiles())
                    .setCommandLine(this.commandLine_)
                    .setId(this.getId())
                    .setName(this.getName())
                    .setJob(this.getJob());
        }

        return (Application) cloned;
    }

    @Override
    public String toString()
    {
        return toStringHelper(this)
                .add("id", this.getId())
                .add("name", this.getName())
                .add("command-line", this.getCommandLine())
                .add("files", this.files_)
                .omitNullValues()
                .toString();
    }
    
    @XmlTransient
    final static Function<List<AppData>, Map<String, AppData>> LIST_TO_MAP = new Function<List<AppData>, Map<String, AppData>>()
    {
        @Override
        public Map<String, AppData> apply(List<AppData> input)
        {
            Map<String, AppData> result = new HashMap<String, AppData>();

            for (AppData data : input)
            {
                result.put(data.getName(), data);
            }

            return result;
        }
    };
    
    
    @XmlTransient
    private File outputFile_;

    public String getExecutableCommandLine()
    {
        final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");
        final String copy = commandLine_;
        final Map<String, AppData> data = LIST_TO_MAP.apply(this.getFiles());
        final String[] params = copy.split(SPACE);
        
        StringBuilder sb  = new StringBuilder();
        
        for (int i = 0; i < params.length; i++)
        {
            Matcher matcher = VARIABLE_PATTERN.matcher(params[i]);
            String value = params[i];
            
            while (matcher.find())
            {
                String name = matcher.group(1);
                AppData appData = data.get(name);
                
                if (YES.equals(appData.getGenerated()))
                {
                  outputFile_ = new File(data.get(name).getPath().replaceAll("~", System.getProperty("user.home")));  
                }
                
                value = data.get(name).getPath();
            }
            sb.append(SPACE).append(value);
        }
        
        return sb.toString().replaceAll("~", System.getProperty("user.home"));
    }

    public File getOuputFile()
    {
       return outputFile_;
    }
}
