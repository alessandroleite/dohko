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
import java.util.Arrays;

import org.excalibur.bio.sequencing.FastaEntry;
import org.excalibur.bio.sequencing.Sequences;
import org.excalibur.bio.sequencing.converters.FastaEntryToSequenceConverter;
import org.excalibur.jackson.databind.JsonJaxbObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import static junit.framework.Assert.*;


public class SequenceJsonSerializerTest
{
    private static final String[] FASTA_ENTRIES = 
    {
            ">ERR135910.3 2405:1:1101:1234:1973:Y/1", "NAAGGGTTTGAGTAAGAGCATAGCTGTTGGGACCCGAAAGATGGTGAACT",
            ">ERR135910.5 2405:1:1101:1170:1994:Y/1", "NTCAACGAGGAATTCCTAGTAAGCGNAAGTCATCANCTTGCGTTGAATAC",
            ">ERR135910.6 2405:1:1101:1272:1972:Y/1", "NTAGTACTATGGTTGGAGACAACATGGGAATCCGGGGTGCTGTAGGCTTG"
    }; 
    
    private static final FastaEntryToSequenceConverter CONVERTER = new FastaEntryToSequenceConverter();
    private static final JsonJaxbObjectMapper JSON_MAPPER = new JsonJaxbObjectMapper();
    
    FastaEntry[] entries;
    
    @Before
    public void setUp()
    {
        entries = new FastaEntry[FASTA_ENTRIES.length / 2];
        
        for (int i = 0, j = 0; i < FASTA_ENTRIES.length; i += 2, j++)
        {
            entries[j] = FastaEntry.valueOf(Arrays.asList(new String[] { FASTA_ENTRIES[i], FASTA_ENTRIES[i + 1] }));
        }
    }
    
    @Test
    public void must_serialize_two_biological_sequences_in_json() throws JsonProcessingException
    {
        Sequences sequences = new Sequences();
        
        for (FastaEntry entry: entries)
        {
            sequences.addSequence(CONVERTER.convert(entry));
        }
        
        String json = JSON_MAPPER.writeValueAsString(sequences);
        assertNotNull(json);
    }
}
