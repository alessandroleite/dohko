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
package org.excalibur.core.services;

import java.util.List;

import org.excalibur.core.cloud.api.KeyPair;
import org.excalibur.core.cloud.api.KeyPairs;
import org.excalibur.core.cloud.api.Provider;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.UserKey;
import org.excalibur.core.domain.UserProviderCredentials;
import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.domain.repository.UserRepository;
import org.excalibur.core.util.SecurityUtils2;
import org.excalibur.core.util.SystemUtils2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;
import static org.excalibur.core.cipher.TripleDESUtils.*;


@Service
public class UserService
{
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class.getName());
    
    private final Object LOCK_ = new Object();
    
    @Autowired
    private UserRepository userRepository_;
    
    @Autowired
    private RegionRepository regionRepository_;

    public void insertUser(User user)
    {
        checkNotNull(user);

        Integer userId = this.userRepository_.insert(user);
        user.setId(checkNotNull(userId));

        for (UserKey key : user.getKeys())
        {
            Integer keyId = this.userRepository_.addUserKey(key);
            key.setId(checkNotNull(keyId));
        }
    }
    
    public UserKey findUserKeyByName(String keyname)
    {
        checkState(!isNullOrEmpty(keyname));

        UserKey key = this.userRepository_.findUserKeyByName(keyname);

        if (key != null)
        {
            key.setUser(this.findUserById(key.getUser().getId()));
        }

        return key;
    }
    
    public void insertUserKey (User user, KeyPairs keys)
    {
        UserKey alreadyExists = findUserKeyByName(keys.getName());
        
        if (alreadyExists == null)
        {
            this.insertUserKey(UserKey.valueOf(keys).setUser(user));
        }
        else 
        {
            boolean updated = false;
            
            if (isNullOrEmpty(alreadyExists.getPrivateKeyMaterial()) && keys.getPrivateKey() != null && 
                !isNullOrEmpty(keys.getPrivateKey().getKeyMaterial()))
            {
                alreadyExists.setPrivateKeyMaterial(keys.getPrivateKey().getKeyMaterial());
                updated = true;
            }
            
            if (isNullOrEmpty(alreadyExists.getPublicKeyMaterial()) && keys.getPublicKey() != null && 
                !isNullOrEmpty(keys.getPublicKey().getKeyMaterial()))
            {
                alreadyExists.setPublicKeyMaterial(keys.getPublicKey().getKeyMaterial());
                updated = true;
            }
            
            if (updated)
            {
                this.userRepository_.updateUserKey(alreadyExists);
            }
        }
    }

    public void insertUserKey(User user, KeyPair keyPair)
    {
        UserKey alreadyExists = findUserKeyByName(keyPair.getKeyName());
        if (alreadyExists == null)
        {
            UserKey key = UserKey.valueOf(keyPair).setUser(user);
            this.insertUserKey(key);
        }
        else
        {
            if (!alreadyExists.getPrivateKeyMaterial().equals(keyPair.getKeyMaterial().trim()))
            {
                LOG.debug("The keys are different database value: [{}] instance value [{}]", 
                        alreadyExists.getPrivateKeyMaterial(),
                        keyPair.getKeyMaterial());
            }
        }
    }
    
    public void insertUserKey(UserKey userKey)
    {
        checkNotNull(userKey);
        checkNotNull(userKey.getUser());
        checkState(!isNullOrEmpty(userKey.getName()));
        
        UserKey clone = userKey.clone();
        
        if (clone.getPrivateKeyMaterial() != null && !clone.isPrivateKeyCipher())
        {
            clone.setPrivateKeyMaterial(cipher(userKey.getPrivateKeyMaterial()));
        }
        
        if (clone.getPublicKeyMaterial() != null && !clone.isPublicKeyCipher())
        {
            clone.setPublicKeyMaterial(cipher(userKey.getPublicKeyMaterial()));
        }
        
        userKey.setId(this.userRepository_.addUserKey(clone));
    }

    public User findUserById(Integer userId)
    {
        checkNotNull(userId);
        return userWithKeys(this.userRepository_.findUserById(userId));
    }

    public User findUserByUsername(String username)
    {
        checkState(!isNullOrEmpty(username));
        return userWithKeys(this.userRepository_.findUserByUsername(username));
    }

    public List<UserKey> getKeys(User user)
    {
        checkNotNull(user);
        checkArgument(user.getId() != null);

        return this.userRepository_.getUserKeys(user.getId());
    }
    
    public UserKey generateKeyForUserIfItDoesNotExist(User user, String keyname) throws Exception
    {
        synchronized (LOCK_)
        {
            UserKey userkey = this.userRepository_.findUserKeyByName(keyname);

            if (userkey == null)
            {
                userkey = generateKeyForUser(user, keyname);
                userkey.setPrivateKeyMaterial(cipher(userkey.getPrivateKeyMaterial()))
                       .setPublicKeyMaterial(cipher(userkey.getPublicKeyMaterial()));
                
                this.insertUserKey(userkey);
                
                SystemUtils2.writeUserSshKey(userkey);
                SystemUtils2.writeUserkey(userkey);
            }
            return userkey;
        }
    }
    
    public UserKey generateKeyForUser(User user, String keyName) throws Exception
    {
        synchronized (LOCK_)
        {
            checkNotNull(user);
            checkState(user.getId() != null);
            checkArgument(!isNullOrEmpty(keyName));
            
            User user2 = this.findUserByUsername(user.getUsername());

            UserKey userKey = SecurityUtils2.generateUserKey();
            checkNotNull(userKey).setUser(user).setName(keyName);
            userKey.setPublicKeyMaterial(String.format("ssh-rsa %s %s", userKey.getPublicKeyMaterial(), user2.getUsername()));

            user.addKey(userKey);
            return userKey;
        }
    }

    protected User userWithKeys(User user)
    {
        if (user != null && user.getId() != null)
        {
            user.addKeys(this.userRepository_.getUserKeys(user.getId()));
        }
        
        return user;
    }

    public User createIfDoesNotExist(User user, KeyPairs keyPairs)
    {
        User result = this.findUserByUsername(user.getUsername());
        
        if (result == null)
        {
            result = new User().setPassword(user.getPassword()).setUsername(user.getUsername());
            this.insertUser(result);
        }
        
        result.addKeys(this.getKeys(result));
        
        if (result.getKey(keyPairs.getPrivateKey().getKeyName()) == null)
        {
            UserKey userKey = new UserKey().setUser(result).setName(keyPairs.getPrivateKey().getKeyName())
                    .setPrivateKeyMaterial(keyPairs.getPrivateKey().getKeyMaterial())
                    .setPublicKeyMaterial(keyPairs.getPublicKey().getKeyMaterial());
            
            this.insertUserKey(userKey);
            result.addKey(userKey);
        }
        
        return result;
    }
    
    public UserProviderCredentials findUserProviderCredentials(User user, Provider provider)
    {
        checkNotNull(user, "User is null");
        checkNotNull(provider, "Provider is null");
        
        checkArgument(!isNullOrEmpty(provider.getName()) && !isNullOrEmpty(provider.getName()));
        return this.userRepository_.findLoginCredentialsOfUserForProvider(user.getUsername(), provider.getName());
    }
    
    
    public UserProviderCredentials getUserProviderCredentials(String username, String provider, String zoneName, String keyname)
    {
        checkArgument(!isNullOrEmpty(username));
        checkArgument(!isNullOrEmpty(provider));
        checkArgument(!isNullOrEmpty(zoneName));
        
        UserProviderCredentials credentials = this.userRepository_.findLoginCredentialsOfUserForProvider(username, provider);
        
        if (credentials != null)
        {
            credentials.setLoginCredentials(credentials.getLoginCredentials().toBuilder().credentialName(keyname).authenticateAsSudo(true).build());
            credentials.setRegion(this.regionRepository_.findZoneByName(zoneName).getRegion());
        }
        
        return credentials;
    }
}
