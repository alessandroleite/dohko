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

import br.cic.unb.chord.communication.Node;

public class RemoteNodeEntryListener implements EntryInsertedListener
{
    private final Node node_;
    
    public RemoteNodeEntryListener(Node node)
    {
        this.node_  = node;
    }
    
    @Override
    public void onEntryInserted(EntryInsertedEvent event)
    {
    }

    /**
     * @return the node_
     */
    public Node getRemoteNode()
    {
        return node_;
    }
}
