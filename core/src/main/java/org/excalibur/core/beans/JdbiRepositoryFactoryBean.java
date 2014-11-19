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
package org.excalibur.core.beans;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.FactoryBean;

import static com.google.common.base.Preconditions.checkNotNull;

public class JdbiRepositoryFactoryBean<T> implements FactoryBean<T>
{
    private final Class<T> repositoryClass_;
    
    private final org.skife.jdbi.v2.DBI dbi_;
    
    public JdbiRepositoryFactoryBean(@Nonnull org.skife.jdbi.v2.DBI dbi, @Nonnull Class<T> repositoryClazz)
    {
        this.dbi_ = checkNotNull(dbi);
        this.repositoryClass_ = checkNotNull(repositoryClazz);
    }

    @Override
    public T getObject() throws Exception
    {
        return dbi_.open(repositoryClass_);
    }

    @Override
    public Class<?> getObjectType()
    {
        return repositoryClass_;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }
}
