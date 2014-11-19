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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.domain.User;
import org.excalibur.core.workflow.domain.WorkflowDescription;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "deployment")
@XmlType(name = "deployment", propOrder = {"uuid_", "description_", "nodes_", "credentials_", "status_" })
public class Deployment
{
    @XmlAttribute(name = "id")
    private Integer id_;

    @XmlAttribute(name = "user", required = true)
    private String username_;

    @XmlElement(name = "node", required = true)
    private final List<Node> nodes_ = new ArrayList<Node>();

    @XmlElement(name = "credential")
    private final List<Credential> credentials_ = new ArrayList<Credential>();

    @XmlElement(name = "description")
    private String description_;

    @XmlElement(name = "status")
    private DeploymentStatus status_;
    
    @XmlElement(name="uuid")
    private String uuid_;

    @XmlTransient
    private Date statusTime_;

    @XmlTransient
    private User user_;

    @XmlTransient
    private WorkflowDescription workflow_;

    @XmlTransient
    private String asText_;

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id_;
    }

    /**
     * @param id
     *            the id to set
     */
    public Deployment setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description_;
    }

    /**
     * @param description
     *            the description to set
     */
    public Deployment setDescription(String description)
    {
        this.description_ = description;
        return this;
    }

    /**
     * @return the credentials
     */
    public List<Credential> getCredentials()
    {
        return credentials_;
    }

    /**
     * @return the nodes
     */
    public List<Node> getNodes()
    {
        return Collections.unmodifiableList(nodes_);
    }

    /**
     * @return the status
     */
    public DeploymentStatus getStatus()
    {
        return status_;
    }

    /**
     * @param status
     *            the status to set
     */
    public Deployment setStatus(DeploymentStatus status)
    {
        this.status_ = status;
        return this;
    }

    /**
     * @return the statusTime
     */
    public Date getStatusTime()
    {
        return statusTime_;
    }

    /**
     * @param statusTime
     *            the statusTime to set
     */
    public Deployment setStatusTime(Date statusTime)
    {
        this.statusTime_ = statusTime;
        return this;
    }

    /**
     * @return the workflow
     */
    public WorkflowDescription getWorkflow()
    {
        return workflow_;
    }

    /**
     * @param workflow
     *            the workflow to set
     */
    public void setWorkflow(WorkflowDescription workflow)
    {
        this.workflow_ = workflow;
    }

    /**
     * @return the user
     */
    public User getUser()
    {
        return this.user_;
    }

    /**
     * @param user
     *            the user to set
     */
    public Deployment setUser(User user)
    {
        this.user_ = user;
        return this;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username_;
    }

    /**
     * @param username
     *            the username to set
     */
    public Deployment setUsername(String username)
    {
        this.username_ = username;
        return this;
    }

    /**
     * @return the asText
     */
    public String getAsText()
    {
        return asText_;
    }

    /**
     * @param asText
     *            the asText to set
     */
    public Deployment setAsText(String asText)
    {
        this.asText_ = asText;
        return this;
    }

    /**
     * @return the uuid
     */
    public String getUuid()
    {
        return uuid_;
    }

    /**
     * @param uuid the uuid to set
     */
    public Deployment setUuid(String uuid)
    {
        this.uuid_ = uuid;
        return this;
    }

    public Map<String, Node> getNodesMap()
    {
        Map<String, Node> nodes = new HashMap<String, Node>();

        for (Node node : this.nodes_)
        {
            nodes.put(node.getName(), node);
        }

        return Collections.unmodifiableMap(nodes);
    }

    /**
     * Returns a read-only {@link Map} with the global credentials. In other words, the credentials defined at the deployment level.
     * 
     * @return a read-only {@link Map} with the credentials.
     */
    public Map<String, Credential> getCredentialMaps()
    {
        Map<String, Credential> credentials = new HashMap<String, Credential>();

        for (Credential credential : this.credentials_)
        {
            credentials.put(credential.getName(), credential);
        }

        return Collections.unmodifiableMap(credentials);
    }

    public Deployment withCredential(Credential credential)
    {
        return this.withCredentials(credential);
    }

    public Deployment withCredentials(Credential... credentials)
    {
        if (credentials != null)
        {
            for (Credential credential : credentials)
            {
                if (credential != null)
                {
                    this.credentials_.add(credential);
                }
            }
        }
        return this;
    }

    public Deployment withDescription(String description)
    {
        this.description_ = description;
        return this;
    }

    public Deployment withId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    public Deployment withNode(Node node)
    {
        if (node != null && !this.nodes_.contains(node))
        {
            this.nodes_.add(node);
        }

        return this;
    }
    
    public Deployment setNodes(Iterable<Node> nodes)
    {
        for (Node node: nodes)
        {
            if (node != null && !this.nodes_.contains(node))
            {
                this.nodes_.add(node);
            }
        }
        return this;
    }

    public Deployment withStatus(DeploymentStatus status)
    {
        this.status_ = status;
        return this;
    }

    public Deployment withStatusTime(Date date)
    {
        this.statusTime_ = date;
        return this;
    }

    public Deployment withUsername(String username)
    {
        this.username_ = username;
        return this;
    }

    public Deployment withText(String text)
    {
        this.asText_ = text;
        return this;
    }

    public Deployment withUser(User user)
    {
        this.user_ = user;
        return this;
    }

    public Deployment withWorkflow(WorkflowDescription workflow)
    {
        this.workflow_ = workflow;
        return this;
    }
}
