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
package br.cic.unb.overlay;

/**
 * Key under which an object is stored in the chord network. This may either be a unique identifier if the object to be stored is unique (e.g. for
 * white pages) or a known keyword or metadata information under which the object should be retrieved together with others (e.g. for yellow pages).
 * 
 * Note that this key is different to the Chord ID, since the ID is calculated by applying a hash function on this key. Thus, this key may return an
 * arbitrary long byte array with uniquely identifies the object to be stored.
 * 
 */
public interface Key
{
    /**
     * Returns the byte for this key which is then used to calculate a unique ID for storage in the chord network.
     * 
     * @return Byte representation of the key.
     */
    public abstract byte[] getBytes();

}
