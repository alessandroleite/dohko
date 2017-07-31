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
package org.excalibur.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.annotation.Nonnull;

import org.apache.sshd.common.util.SecurityUtils;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.util.io.pem.PemReader;
import org.excalibur.core.domain.UserKey;
import org.excalibur.core.io.utils.IOUtils2;

import ch.ethz.ssh2.crypto.Base64;

import com.jcraft.jsch.HASH;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;

public class SecurityUtils2
{
    public static final String PUBLIC_KEY_START = "-----BEGIN PUBLIC KEY-----";
    public static final String PUBLIC_KEY_END = "-----END PUBLIC KEY-----";
    public static final String PRIVATE_PKCS1_MARKER = "-----BEGIN RSA PRIVATE KEY-----";
    public static final String PRIVATE_PKCS1_END_MARKER = "-----END RSA PRIVATE KEY-----";
    public static final String PRIVATE_PKCS8_MARKER = "-----BEGIN PRIVATE KEY-----";
    public static final String PROC_TYPE_ENCRYPTED = "Proc-Type: 4,ENCRYPTED";
    public static final String PUBLIC_KEY_SSH_RSA = "ssh-rsa";
    
    private static final String[] CHARS = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    
    private static final byte[] PUBLIC_KEY_SSH_RSA_NAME;
    
    static 
    {
        try
        {
            PUBLIC_KEY_SSH_RSA_NAME = PUBLIC_KEY_SSH_RSA.getBytes("US-ASCII");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private SecurityUtils2()
    {
        throw new UnsupportedOperationException();
    }

    public static UserKey generateUserKey() throws NoSuchAlgorithmException, NoSuchProviderException, IOException 
    {
        KeyPairGenerator kpg = SecurityUtils.getKeyPairGenerator("RSA");

        kpg.initialize(1024, new SecureRandom());
        java.security.KeyPair kp = kpg.generateKeyPair();

        String priv = getKeyMaterial(kp.getPrivate());
        
        byte[] encoded = encode((RSAPublicKey) kp.getPublic());
        
//        String pub = getKeyMaterial(kp.getPublic()).replaceAll(PUBLIC_KEY_START, "").replaceAll(PUBLIC_KEY_END, "").trim();

        return new UserKey().setPrivateKeyMaterial(priv)
                .setPublicKeyMaterial(new String(Base64.encode(encoded)))
                .setFingerPrint(getFingerPrint((RSAPublicKey) kp.getPublic()));
    }

    public static byte[] encode(RSAPublicKey key) throws IOException
    {
        try(ByteArrayOutputStream buf = new ByteArrayOutputStream())
        {
            write(PUBLIC_KEY_SSH_RSA_NAME, buf);
            write(key.getPublicExponent().toByteArray(), buf);
            write(key.getModulus().toByteArray(), buf);
            
            return buf.toByteArray();
        }
    }

    private static void write(byte[] str, OutputStream os) throws IOException
    {
        for (int shift = 24; shift >= 0; shift -= 8)
        {
            os.write((str.length >>> shift) & 0xFF);
        }
        
        os.write(str);
    }

    public static String getKeyMaterial(Key key) throws IOException
    {
        checkNotNull(key);

        StringWriter sw = null;
        PEMWriter writer = null;

        try
        {
            sw = new StringWriter();
            writer = new PEMWriter(sw);
            writer.writeObject(key);
            writer.flush();

            String material = sw.toString();
            checkState(!isNullOrEmpty(material));

            return material;
        }
        finally
        {
            IOUtils2.closeQuietly(sw, writer);
        }
    }
    
    public static HASH md5()
    {
        return new com.jcraft.jsch.jce.MD5();
    }
    
    public static String getFingerPrint(RSAPublicKey key) throws IOException
    {
        try(ByteArrayOutputStream buf = new ByteArrayOutputStream())
        {
            write(key.getPublicExponent().toByteArray(), buf);
            write(key.getModulus().toByteArray(), buf);
            
            return getFingerPrint(buf.toByteArray());
        }
    }
    
    public static String getFingerPrint(final byte[] data)
    {
        checkNotNull(data);
        return getFingerPrint(md5(), data);
    }
    
    public static String getFingerPrint(final HASH hash, final byte[] data)
    {
        checkNotNull(hash);
        checkNotNull(data);
        
        try
        {
            hash.init();
            hash.update(data, 0, data.length);
            
            byte[] digest = hash.digest();
            
            StringBuffer sb = new StringBuffer();
            int offset;
            
            for (int i = 0; i < digest.length; i++)
            {
                offset = digest[i] & 0xff;
                sb.append(CHARS[(offset >>> 4) & 0xf]);
                sb.append(CHARS[(offset) & 0xf]);
                
                if (i + 1 < digest.length)
                {
                    sb.append(":");
                }
            }
            return sb.toString();
        }
        catch (Exception exception)
        {
            return "???";
        }
    }
    
    public static PrivateKey readPrivateKey(@Nonnull File key) throws IOException, GeneralSecurityException
    {
        try (PemReader reader = new PemReader(new FileReader(key)))
        {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(reader.readPemObject().getContent());
            KeyFactory kf = SecurityUtils.getKeyFactory("RSA");
            return kf.generatePrivate(keySpec);
        }
    }
    
    public static PublicKey readPublicKey(@Nonnull File key) throws IOException, GeneralSecurityException
    {
        String[] contents = IOUtils2.readLines(key).split(" ");
        checkArgument(contents.length == 3, "Invalid RSA public key");
        org.apache.commons.codec.binary.Base64 b64 = new org.apache.commons.codec.binary.Base64();
        return readPublicKey(b64.decode(contents[1]));
    }
    
    //https://github.com/fommil/openssh-java
    public static PublicKey readPublicKey(byte[] bytes) throws GeneralSecurityException, IOException
    {
        // http://stackoverflow.com/questions/12749858
        // http://tools.ietf.org/html/rfc4716
        // http://tools.ietf.org/html/rfc4251
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes)))
        {
            byte[] sshRsa = new byte[in.readInt()];
            in.readFully(sshRsa);
            
            byte[] exp = new byte[in.readInt()];
            in.readFully(exp);
            
            byte[] mod = new byte[in.readInt()];
            in.readFully(mod);

            RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(mod), new BigInteger(exp));
            return SecurityUtils.getKeyFactory("RSA").generatePublic(spec);
        }
    }
}
