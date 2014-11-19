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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "contacts", namespace = "http://www.excalibur.org/types/xmpp")
@XmlType(name = "contacts", namespace = "http://www.excalibur.org/types/xmpp")
public class Contacts implements Iterable<JID>
{
    private final ConcurrentMap<String, JID> contacts_ = new ConcurrentHashMap<String, JID>();
    
    public static Contacts newContacts()
    {
        return new Contacts();
    }
    
    public static Contacts newContacts(Iterable<JID> contacts)
    {
        return newContacts().addAll(contacts);
    }
    
    public static Contacts newContacts(JID [] contacts)
    {
        return newContacts().addAll(Arrays.asList(contacts));
    }

    @Override
    public Iterator<JID> iterator()
    {
        return values().iterator();
    }

    public Collection<JID> values()
    {
        return Collections.unmodifiableCollection(contacts_.values());
    }
    
    @XmlElement(name = "contact")
    protected List<JID> getContacts()
    {
        return new ArrayList<JID>(this.contacts_.values());
    }

    public JID add(JID contact)
    {
        return contact != null ? this.contacts_.put(contact.getId(), contact) : null;
    }

    public Contacts addAll(Iterable<JID> contacts)
    {
        if (contacts != null)
        {
            for (JID jid : contacts)
            {
                add(jid);
            }
        }

        return this;
    }

    public JID remove(JID contact)
    {
        return contact != null ? this.contacts_.remove(contact.getId()) : null;
    }

    public Contacts removeAll(Iterable<JID> contacts)
    {
        if (contacts != null)
        {
            for (JID contact : contacts)
            {
                remove(contact);
            }
        }

        return this;
    }
    
    public JID get(String key)
    {
        return this.contacts_.get(key);
    }

    public boolean isEmpty()
    {
        return this.contacts_.isEmpty();
    }

    public int size()
    {
        return this.contacts_.size();
    }
}
