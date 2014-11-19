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
package org.excalibur.core;

import java.io.File;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

import static org.excalibur.core.util.SecurityUtils2.*;

public class LoginCredentials extends Credentials implements Cloneable
{
    public static LoginCredentials fromCredentials(Credentials that)
    {
        if (that == null)
        {
            return null;
        }
        if (that instanceof LoginCredentials)
        {
            return LoginCredentials.class.cast(that);
        }
        
        return builder(that).build();
    }

    public static Builder builder(Credentials creds)
    {
        if (creds == null)
        {
            return builder();
        }
        
        if (creds instanceof LoginCredentials)
        {
            return LoginCredentials.class.cast(creds).toBuilder();
        }
        else
        {
            return builder().identity(creds.identity).credential(creds.credential);
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder extends Credentials.Builder<LoginCredentials>
    {
        private boolean authenticateAsSudo;
        private Optional<String> password;
        private Optional<String> publicKey;
        private Optional<String> privateKey;
        private Optional<String> credentialName;

        @Override
        public Builder identity(String identity)
        {
            return Builder.class.cast(super.identity(identity));
        }

        public Builder user(String user)
        {
            return this.identity(user);
        }

        public Builder password(String password)
        {
            this.password = Optional.fromNullable(password);
            if (this.privateKey == null)
            {
                noPrivateKey();
            }
            return this;
        }

        public Builder privateKey(String privateKey)
        {
            this.privateKey = Optional.fromNullable(privateKey);
            if (this.password == null)
            {
                noPassword();
            }
            
            return this;
        }
        
        public Builder publicKey(String publicKey)
        {
            this.publicKey = Optional.fromNullable(publicKey);
            return this;
        }

        public Builder privateKey(File sshKey)
        {
            return privateKey(sshKey.getAbsolutePath());
        }

        public Builder noPassword()
        {
            this.password = Optional.absent();
            return this;
        }

        public Builder noPrivateKey()
        {
            this.privateKey = Optional.absent();
            return this;
        }
        
        public Builder noCredentialName()
        {
            this.credentialName = Optional.absent();
            
            return this;
        }

        @Override
        public Builder credential(String credential)
        {
            if (isPrivateKeyCredential(credential))
            {
                noPassword().privateKey(credential);
            }
            else if (credential != null)
            {
                password(credential).noPrivateKey();
            }
            return this;
        }
        
        /**
         * <p>
         * Assigns a name for this credential.
         * </p>
         * <p>
         * <strong>Warning:</strong> This is not the name of the user or his/her identity.
         * 
         * @param name The name of this credential.
         * @return The same instance.
         */
        public Builder credentialName(String name)
        {
            this.credentialName = Optional.fromNullable(name);
            return this;
        }

        public Builder authenticateAsSudo(boolean authenticateSudo)
        {
            this.authenticateAsSudo = authenticateSudo;
            return this;
        }

        public LoginCredentials build()
        {
            if (this.identity == null && password == null && privateKey == null && !authenticateAsSudo)
            {
                return null;
            }
            
            return new LoginCredentials(identity, password, publicKey, privateKey, authenticateAsSudo, credentialName);
        }
    }

    private final boolean authenticateAsSudo;
    private final Optional<String> password;
    private final Optional<String> publicKey;
    private final Optional<String> privateKey;
    private final Optional<String> credentialName;

    private LoginCredentials(String username, @Nullable Optional<String> password,
            @Nullable Optional<String> publicKey, @Nullable Optional<String> privateKey, boolean authenticateSudo, Optional<String> credentialName)
    {
        super(username, privateKey != null && privateKey.isPresent() && isPrivateKeyCredential(privateKey.get()) ? privateKey.get()
                : password != null && password.isPresent() ? password.get() : null);
        
        this.password = password;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.authenticateAsSudo = authenticateSudo;
        this.credentialName = credentialName;
    }

    private static boolean isPrivateKeyCredential(String credential)
    {
        return credential != null && (credential.startsWith(PRIVATE_PKCS1_MARKER) || credential.startsWith(PRIVATE_PKCS8_MARKER));
    }

    /**
     * @return the login user
     */
    public String getUser()
    {
        return identity;
    }

    /**
     * @return the password of the login user or <code>null</code>
     */
    @Nullable
    public String getPassword()
    {
        return (password != null) ? password.orNull() : null;
    }

    /**
     * @return the optional password of the user or <code>null</code>
     */
    @Nullable
    public Optional<String> getOptionalPassword()
    {
        return password;
    }

    /**
     * @return the private ssh key of the user or <code>null</code>
     */
    @Nullable
    public String getPrivateKey()
    {
        return (privateKey != null) ? privateKey.orNull() : null;
    }
    
    /**
     * @return the public ssh key of the user or <code>null</code>.
     */
    @Nullable
    public String getPublicKey()
    {
        return (publicKey != null) ? publicKey.orNull() : null;
    }
    
    /**
     * Returns the name of this credential.
     * 
     * @return This credential name.
     */
    public String getCredentialName()
    {
        return (credentialName != null) ? credentialName.orNull() : null;
    }

    /**
     * @return <code>true</code> if there is a private key attached that is not encrypted
     */
    public boolean hasUnencryptedPrivateKey()
    {
        return getPrivateKey() != null && !getPrivateKey().isEmpty() && !getPrivateKey().contains(PROC_TYPE_ENCRYPTED);
    }

    /**
     * @return the optional private ssh key of the user or <code>null</code>
     */
    @Nullable
    public Optional<String> getOptionalPrivateKey()
    {
        return privateKey;
    }

    /**
     * Secures access to root requires a password. This password is required to access either the console or run sudo as root.
     * 
     * @return if a password is required to access the root user.
     */
    public boolean shouldAuthenticateAsSudo()
    {
        return authenticateAsSudo;
    }

    public Builder toBuilder()
    {
        Builder builder = new Builder().user(identity).authenticateAsSudo(authenticateAsSudo);
        
        if (password != null)
        {
            if (password.isPresent())
            {
                builder = builder.password(password.get());
            }
            else
            {
                builder = builder.noPassword();
            }
        }
        if (privateKey != null)
        {
            if (privateKey.isPresent())
            {
                builder = builder.privateKey(privateKey.get());
            }
            else
            {
                builder = builder.noPrivateKey();
            }
        }
        
        if (credentialName != null)
        {
            if (credentialName.isPresent())
            {
                builder = builder.credentialName(credentialName.get());
            }
            else 
            {
                builder = builder.noCredentialName();
            }
        }
        
        return builder;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("user", getUser())
                .add("passwordPresent", this.password.isPresent())
                .add("privateKeyPresent", this.privateKey.isPresent())
                .add("shouldAuthenticateSudo", authenticateAsSudo)
                .omitNullValues()
                .toString();
    }
    
    @Override
    public LoginCredentials clone() 
    {
       return this.toBuilder().build();
    }
}
