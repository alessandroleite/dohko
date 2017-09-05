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
