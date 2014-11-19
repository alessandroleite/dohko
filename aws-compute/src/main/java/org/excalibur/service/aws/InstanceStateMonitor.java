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
package org.excalibur.service.aws;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.excalibur.core.cloud.api.VirtualMachine;


public class InstanceStateMonitor
{
    private ConcurrentMap<String, Future<VirtualMachine>> futures = new ConcurrentHashMap<String, Future<VirtualMachine>>();

    public void monitor(String... instancesId)
    {
        for (String id : instancesId)
        {
            Future<VirtualMachine> f = futures.get(id);

            if (f == null)
            {
                Callable<VirtualMachine> eval = new Callable<VirtualMachine>()
                {
                    @Override
                    public VirtualMachine call() throws Exception
                    {
                        return null;
                    }
                };

                FutureTask<VirtualMachine> ft = new FutureTask<VirtualMachine>(eval);
                f = futures.putIfAbsent(id, ft);

                if (f == null)
                {
                    f = ft;
                    ft.run();
                }
                
                try
                {
                    VirtualMachine vm = f.get();
                }
                catch (InterruptedException e)
                {
                    futures.remove(f);
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
