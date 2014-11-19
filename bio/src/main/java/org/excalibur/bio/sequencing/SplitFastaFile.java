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
package org.excalibur.bio.sequencing;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.google.common.io.Files.*;



public class SplitFastaFile
{
    public static void main(String[] args) throws IOException
    {
        File fasta = new File(args[0]);
        File ouputDir = new File(args[1]);
        
        try (FastaReader reader = new FastaReader(fasta))
        {
            List<FastaEntry> entries = reader.all();
            
            createParentDirs(ouputDir);
            for (FastaEntry entry: entries)
            {
                int i = entry.getHeader().indexOf("|");
                int f = entry.getHeader().lastIndexOf("|");
                String name =  entry.getHeader().substring(i + 1, f);
                write(entry.toString().trim().getBytes(), new File(ouputDir, String.format("%s.fasta", name)));
            }
        }
    }
}
