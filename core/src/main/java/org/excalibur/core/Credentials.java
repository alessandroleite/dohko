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
package org.excalibur.core;

public class Credentials implements Cloneable
{
    public static class Builder<T extends Credentials>
    {
        protected String identity;
        protected String credential;

        public Builder<T> identity(String identity)
        {
            this.identity = identity;
            return this;
        }

        public Builder<T> credential(String credential)
        {
            this.credential = credential;
            return this;
        }

        @SuppressWarnings("unchecked")
        public T build()
        {
            return (T) new Credentials(identity, credential);
        }
    }

    public final String identity;
    public final String credential;

    public Credentials(String identity, String credential)
    {
        super();
        this.identity = identity;
        this.credential = credential;
    }

    /**
     * @return the identity
     */
    public String getIdentity()
    {
        return identity;
    }

    /**
     * @return the credential
     */
    public String getCredential()
    {
        return credential;
    }
    
    @Override
    public Credentials clone() 
    {
        Credentials clone;
        
        try
        {
            clone = (Credentials) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new Credentials(this.identity, this.credential);
        }
        
        return clone;
    }
}
