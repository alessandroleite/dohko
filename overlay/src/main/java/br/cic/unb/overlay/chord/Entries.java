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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.communication.Entry;
import br.cic.unb.chord.data.ID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Stores entries for the local node in a local hash table and provides methods for accessing them. It IS allowed, that multiple objects of type
 * {@link Entry} with same {@link ID} are stored!
 */
final class Entries
{
    /**
     * Object LOG.
     */
    private final static Logger logger = LoggerFactory.getLogger(Entries.class.getName());

    /**
     * Local hash table for entries.
     */
    private Map<ID, Set<Entry>> entries = null;

    /**
     * Creates an empty repository for entries.
     */
    Entries()
    {
        this.entries = Collections.synchronizedMap(new TreeMap<ID, Set<Entry>>());
    }

    /**
     * Stores a set of entries to the local hash table.
     * 
     * @param entriesToAdd
     *            Set of entries to add to the repository.
     * @throws NullPointerException
     *             If set reference is <code>null</code>.
     */
    final synchronized void addAll(Set<Entry> entriesToAdd)
    {
        checkNotNull(entriesToAdd, "The entries to be added must not be null!");

        for (Entry nextEntry : entriesToAdd)
        {
            this.add(nextEntry);
        }
    }

    /**
     * Stores one entry to the local hash table.
     * 
     * @param entryToAdd
     *            Entry to add to the repository.
     * @throws NullPointerException
     *             If entry to add is <code>null</code>.
     */
    final synchronized void add(Entry entryToAdd)
    {
        checkNotNull(entryToAdd, "The entry to add may not be null!");

        Set<Entry> values;
        synchronized (this.entries)
        {
            if (this.entries.containsKey(entryToAdd.getId()))
            {
                values = this.entries.get(entryToAdd.getId());
            }
            else
            {
                values = new HashSet<Entry>();
                this.entries.put(entryToAdd.getId(), values);
            }
            values.add(entryToAdd);
        }

        logger.debug("Entry was added: {}", entryToAdd);
    }

    /**
     * Removes the given entry from the local hash table.
     * 
     * @param entryToRemove
     *            Entry to remove from the hash table.
     * @throws NullPointerException
     *             If entry to remove is <code>null</code>.
     */
    final void remove(Entry entryToRemove)
    {
        checkNotNull(entryToRemove, "Entry to remove must not be null!");

        synchronized (this.entries)
        {
            if (this.entries.containsKey(entryToRemove.getId()))
            {
                Set<Entry> values = this.entries.get(entryToRemove.getId());
                values.remove(entryToRemove);

                if (values.isEmpty())
                {
                    this.entries.remove(entryToRemove.getId());
                }
            }
        }
        logger.debug("Entry was removed: {}", entryToRemove);
    }

    /**
     * Returns a set of entries matching the given ID. If no entries match the given ID, an empty set is returned.
     * 
     * @param id
     *            ID of entries to be returned.
     * @throws NullPointerException
     *             If given ID is <code>null</code>.
     * @return Set of matching entries. Empty Set if no matching entries are available.
     */
    final Set<Entry> getEntries(ID id)
    {
        checkNotNull(id);
        synchronized (this.entries)
        {
            if (this.entries.containsKey(id))
            {
                Set<Entry> entriesForID = this.entries.get(id);
                return new HashSet<Entry>(entriesForID);
            }
        }
        logger.debug("No entries available for [{}]. Returning an empty set.", id);
        return new HashSet<Entry>();
    }

    /**
     * Returns all entries in interval, excluding lower bound, but including upper bound
     * 
     * @param fromID
     *            Lower bound of IDs; entries matching this ID are NOT included in result.
     * @param toID
     *            Upper bound of IDs; entries matching this ID ARE included in result.
     * @throws NullPointerException
     *             If either or both of the given ID references have value <code>null</code>.
     * @return Set of matching entries.
     */
    final Set<Entry> getEntriesInInterval(ID fromID, ID toID)
    {
        checkArgument(fromID != null && toID != null, "Neither of the given IDs may have value null!");
        Set<Entry> result = new HashSet<Entry>();

        synchronized (this.entries)
        {
            for (ID nextID : this.entries.keySet())
            {
                if (nextID.isInInterval(fromID, toID))
                {
                    Set<Entry> entriesForID = this.entries.get(nextID);
                    for (Entry entryToAdd : entriesForID)
                    {
                        result.add(entryToAdd);
                    }
                }
            }
        }

        result.addAll(this.getEntries(toID));

        return result;
    }

    /**
     * Removes the given entries from the local hash table.
     * 
     * @param toRemove
     *            Set of entries to remove from local hash table.
     * @throws NullPointerException
     *             If the given set of entries is <code>null</code>.
     */
    final void removeAll(Set<Entry> toRemove)
    {
        checkNotNull(toRemove, "The entries must not be null!");

        synchronized (this.entries)
        {
            for (Entry nextEntry : toRemove)
            {
                this.remove(nextEntry);
            }
        }
    }

    /**
     * Returns an unmodifiable map of all stored entries.
     * 
     * @return Unmodifiable map of all stored entries.
     */
    final synchronized Map<ID, Set<Entry>> getEntries()
    {
        return Collections.unmodifiableMap(this.entries);
    }

    /**
     * Returns the number of entries.
     * 
     * @return Number of stored entries.
     */
    final int getNumberOfStoredEntries()
    {
        synchronized (this.entries)
        {
            return this.entries.size();
        }
    }

    /**
     * Returns a formatted string of all entries stored in the local hash table.
     * 
     * @return String representation of all stored entries.
     */
    public final String toString()
    {
        StringBuilder result = new StringBuilder("Entries:\n");

        for (Map.Entry<ID, Set<Entry>> entry : this.entries.entrySet())
        {
            result.append("  key = ").append(entry.getKey().toString()).append(", value = ").append(entry.getValue()).append("\n");
        }

        return result.toString();
    }
}
