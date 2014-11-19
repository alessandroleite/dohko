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
package org.excalibur.core.io.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ZipUtil
{
    private static final Logger LOG = LoggerFactory.getLogger(ZipUtil.class.getName());

    private ZipUtil()
    {
        throw new UnsupportedOperationException();
    }

    public static byte[] compress(String plain, String charset)
    {
        if (plain == null)
        {
            return new byte[0];
        }

        return compress(plain.getBytes(Charset.forName(charset)));
    }

    public static byte[] compress(String plain)
    {
        return compress(plain, "UTF-8");
    }

    public static byte[] compress(byte[] data)
    {
        if (data == null)
        {
            return new byte[0];
        }

        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);
        compressor.setInput(data);
        compressor.finish();

        try
        {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length))
            {
                byte[] bufffer = new byte[1024];

                while (!compressor.finished())
                {
                    int count = compressor.deflate(bufffer);
                    bos.write(bufffer, 0, count);
                }

                return bos.toByteArray();
            }
        }
        catch (IOException exception)
        {
            LOG.error("Error on compressing the data. Error message: [{}]", exception.getMessage(), exception);
        }

        return new byte[0];
    }

    public static byte[] uncompress(byte[] data)
    {
        byte[] result = new byte[0];

        if (data != null)
        {
            Inflater decompressor = new Inflater();
            decompressor.setInput(data);

            ByteArrayOutputStream bos = null;

            try
            {
                bos = new ByteArrayOutputStream(data.length);
                byte[] buffer = new byte[1024];

                while (!decompressor.finished())
                {
                    try
                    {
                        int count = decompressor.inflate(buffer);
                        bos.write(buffer, 0, count);
                    }
                    catch (DataFormatException e)
                    {
                        return data;
                    }
                }
                result = bos.toByteArray();
            }
            finally
            {
                IOUtils2.closeQuietly(bos);
            }
            
        }
        return result;
    }
}
