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
package org.excalibur.core.cloud.service.xmpp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;

public class AccountBuilder
{
    private static final String CHAR_AT = "@";
    private static final String CHAR_FORWARD_SLASH = "/";
    
    private final Map<String, String> attributes_ = new HashMap<String, String>();
    
    private String domain_;
    private String id_;
    private String password_;
    private String resource_;
    private Integer ownerId_;
    private Date createIn_;
    private Date dateLastStatus_;

    public AccountBuilder withAttribute(String attrName, String attrValue)
    {
        if (!(attrName == null))
        {
            this.attributes_.put(attrName, attrValue);
        }

        return this;
    }

    public AccountBuilder jid(JID user)
    {
        String[] parts = user.getId().split(CHAR_AT);
        
//        if (parts.length != 2)
//        {
//            "entity must be of format node@domain/resource"
//        }
        
        this.id_ = parts[0];
        this.domain_ = parts[1];
        
        if (parts[1].contains(CHAR_FORWARD_SLASH))
        {
            int flash = parts[1].indexOf(CHAR_FORWARD_SLASH);
            this.domain_ = parts[1].substring(0, flash);
            this.resource_ = parts[1].substring(flash + 1);
        }
        
        return this;
    }

    public AccountBuilder password(String password)
    {
        this.password_ = password;
        return this;
    }

    public AccountBuilder domain(String domain)
    {
        this.domain_ = domain;
        return this;
    }

    public AccountBuilder name(String name)
    {
        this.id_ = name;
        return this;
    }
    
    public AccountBuilder ownerId(Integer id)
    {
        this.ownerId_ = id;
        return this;
    }
    
    public AccountBuilder createdIn(Date date)
    {
        this.createIn_ = date;
        return this;
    }
    
    public AccountBuilder lastStatusDate(Date date)
    {
        this.dateLastStatus_ = date;
        return this;
    }
    

    public Account build()
    {
        checkState(!isNullOrEmpty(domain_));
        checkState(!isNullOrEmpty(password_));
//        checkState(ownerId_ > 0);
        
        if (createIn_ == null)
        {
            createIn_ = new Date();
            dateLastStatus_ = createIn_;
        }
        
        if (dateLastStatus_ == null)
        {
            dateLastStatus_ = createIn_;
        }
        
        return new Account(id_, domain_, password_, resource_, ownerId_, attributes_, createIn_, dateLastStatus_);
    }
}
