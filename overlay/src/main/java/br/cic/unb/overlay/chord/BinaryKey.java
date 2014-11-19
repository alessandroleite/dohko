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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.overlay.Key;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class BinaryKey implements Key
{
    private static final Logger LOG = LoggerFactory.getLogger(BinaryKey.class.getName());

    /**
     * File to be serialize
     */
    private final File fileToSerialize;

    public BinaryKey(final File fileToSerialize)
    {
        checkNotNull(fileToSerialize, "File to serialize may not be null!");
        checkState(fileToSerialize.exists(), "File " + fileToSerialize.getName() + " to serializa does not exist!");

        this.fileToSerialize = fileToSerialize;
    }

    @Override
    public byte[] getBytes()
    {

        FileInputStream fis = null;
        DataInputStream dis = null;
        ByteArrayOutputStream os = null;
        DataOutputStream daos = null;

        try
        {
            fis = new FileInputStream(fileToSerialize);
            dis = new DataInputStream(fis);

            os = new ByteArrayOutputStream();
            daos = new DataOutputStream(os);

            int i;
            while ((i = dis.read()) != -1)
            {
                daos.write(i);
            }
            return os.toByteArray();

        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }
        finally
        {
            try
            {
                if (fis != null)
                {
                    fis.close();
                }

                if (dis != null)
                {
                    dis.close();
                }

                if (os != null)
                {
                    os.close();
                }

                if (daos != null)
                {
                    daos.close();
                }
            }
            catch (IOException exception)
            {
                LOG.warn("Error ob close resource  {}", exception.getMessage(), exception);
            }
        }

    }
}
