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
package org.excalibur.core.util;

import org.excalibur.core.compute.monitoring.utils.Objects2;

import com.google.common.base.Function;

import net.vidageek.mirror.dsl.Mirror;

public class CloneIterableFunction<T extends Cloneable> implements Function<Iterable<T>, Iterable<T>> 
{
	@SuppressWarnings("unchecked")
	@Override
	public Iterable<T> apply(final Iterable<T> input) 
	{
		if (input == null) 
		{
			return input;
		}
		
		Iterable<T> result = new Mirror().on(input.getClass()).invoke().constructor().withoutArgs();
		
		for (T t: input)
		{
			new Mirror().on(result).invoke().method("add").withArgs(Objects2.clone(t));
		}
		
		return result;
	}
	
	public static <T extends Cloneable> Iterable<T> cloneIterable(Iterable<T> input)
	{
		return new CloneIterableFunction<T>().apply(input);
	}
}
