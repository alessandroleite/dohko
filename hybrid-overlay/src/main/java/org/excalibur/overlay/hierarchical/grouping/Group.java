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
package org.excalibur.overlay.hierarchical.grouping;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import br.cic.unb.chord.data.Peer;

import com.google.common.collect.Lists;

public class Group extends ReceiverAdapter implements Closeable
{
    private final JChannel channel_;
    
    private List<Peer> peers_ = Lists.newCopyOnWriteArrayList();

    public Group(JChannel channel)
    {
        this.channel_ = checkNotNull(channel);
        channel_.setReceiver(this);
    }
    
    @Override
    public void viewAccepted(View view)
    {
        super.viewAccepted(view);
        
        for (Address address: view.getMembers())
        {
            System.out.println(address);
        }
        
        System.out.printf("Number of members %s\n", view.getMembers().size());
    }
    
    @Override
    public void receive(Message msg)
    {
        System.out.printf("Receive the message %s from %s\n", msg.getBuffer(), msg.getSrc());
    }

    @Override
    public void close() throws IOException
    {
        this.channel_.close();
    }
}
