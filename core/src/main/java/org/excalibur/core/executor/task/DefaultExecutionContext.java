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
package org.excalibur.core.executor.task;

import static com.google.common.base.Preconditions.checkNotNull;

import org.excalibur.core.domain.repository.UserRepository;
import org.excalibur.core.executor.Context;
import org.excalibur.core.executor.ExecutionEnvironment;
import org.excalibur.core.task.impl.AbstractContextBase;

public class DefaultExecutionContext extends AbstractContextBase implements ExecutionContext
{
    private final ExecutionEnvironment executionEnvironment_;
    private final UserRepository userRepository_;

    public DefaultExecutionContext(Context parent, ExecutionEnvironment environment, UserRepository userRepository)
    {
        super(parent);
        this.executionEnvironment_ = checkNotNull(environment);
        this.userRepository_ = checkNotNull(userRepository);
    }
    
    public DefaultExecutionContext(Context parent, UserRepository userRepository, ExecutionEnvironment environment)
    {
        this(parent, environment, userRepository);
    }

    @Override
    public ExecutionEnvironment getExecutionEnvironment()
    {
        return executionEnvironment_;
    }

    @Override
    public UserRepository getUserRepository()
    {
        return userRepository_;
    }
}
