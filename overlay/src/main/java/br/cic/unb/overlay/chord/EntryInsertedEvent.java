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
package br.cic.unb.overlay.chord;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.EventObject;

import br.cic.unb.chord.communication.Entry;
import br.cic.unb.chord.communication.net.RemoteNodeInfo;

import br.cic.unb.chord.data.Peer;

public class EntryInsertedEvent extends EventObject
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -4481184017571595937L;

    private final Entry entry;

    private final long time;

    public EntryInsertedEvent(RemoteNodeInfo source, Entry entry)
    {
        this(checkNotNull(source), entry, System.currentTimeMillis());
    }

    public EntryInsertedEvent(RemoteNodeInfo source, Entry entry, long time)
    {
        super(source);
        this.entry = checkNotNull(entry);
        this.time = time;
    }

    /**
     * @return the entry
     */
    public Entry getEntry()
    {
        return entry;
    }

    public Peer getNodeInfo()
    {
        return Peer.valueOf(this.getSource().getNodeID(), this.getSource().getNodeURL());
    }

    /**
     * @return the time
     */
    public long getTime()
    {
        return time;
    }

    @Override
    public RemoteNodeInfo getSource()
    {
        return (RemoteNodeInfo) super.getSource();
    }
}
