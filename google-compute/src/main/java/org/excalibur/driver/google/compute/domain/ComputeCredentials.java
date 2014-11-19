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
package org.excalibur.driver.google.compute.domain;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import org.excalibur.core.Credentials;
import org.excalibur.core.LoginCredentials;
import org.excalibur.core.util.AnyThrow;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;

public class ComputeCredentials extends Credentials
{
    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder extends Credentials.Builder<ComputeCredentials>
    {
        protected String projectId;
        protected LoginCredentials loginCredentials;
        protected PrivateKey credentialKey;

        @Override
        public ComputeCredentials build()
        {
            if (projectId == null && loginCredentials == null)
            {
                return null;
            }

            return new ComputeCredentials(identity, credential, projectId, loginCredentials, credentialKey);
        }

        public Builder identity(File identity)
        {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new FileReader(identity));
            return Builder.class.cast(super.identity((String) clientSecrets.getWeb().get("client_email")));
        }

        public Builder credential(File credential)
        {
            return Builder.class.cast(super.credential(credential.getAbsolutePath()));
        }

        public Builder credentialKey(PrivateKey key)
        {
            this.credentialKey = key;
            return this;
        }

        public Builder credentialKey(InputStream keyStream)
        {
            try
            {
                credentialKey = SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(), keyStream, "notasecret", "privatekey", "notasecret");
            }
            catch (IOException e)
            {
                AnyThrow.throwUncheked(e);
            }
            catch (GeneralSecurityException e)
            {
                AnyThrow.throwUncheked(e);
            }
            
            return this;
        }

        public Builder loginCredentials(LoginCredentials loginCredentials)
        {
            this.loginCredentials = loginCredentials;
            return this;
        }

        public Builder project(String project)
        {
            this.projectId = project;
            return this;
        }
    }

    private ComputeCredentials(String identity, String credential, String projectId, LoginCredentials loginCredentials, PrivateKey credentialKey)
    {
        super(identity, credential);
        this.projectId_ = projectId;
        this.loginCredentials_ = loginCredentials;
        this.credentialKey_ = credentialKey;
    }

    private final String projectId_;
    private final LoginCredentials loginCredentials_;
    private final PrivateKey credentialKey_;

    /**
     * @return the projectId_
     */
    public String getProjectId()
    {
        return projectId_;
    }

    /**
     * @return the loginCredentials_
     */
    public LoginCredentials getLoginCredentials()
    {
        return loginCredentials_;
    }

    /**
     * @return the credentialKey_
     */
    public PrivateKey getCredentialKey()
    {
        return credentialKey_;
    }
}
