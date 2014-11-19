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
package org.excalibur.core.test.workflow.repository;

import java.io.IOException;
import java.util.List;

import org.excalibur.core.domain.UserKey;
import org.excalibur.core.domain.repository.UserRepository;
import org.excalibur.core.test.TestSupport;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import static org.excalibur.core.cipher.TripleDESUtils.*;

public class UserRepositoryTest extends TestSupport
{
    private UserRepository userRepository;
    
    @Before
    public void setup() throws IOException 
    {
        super.setup();
        userRepository = openRepository(UserRepository.class);
    }
    
    @Test
    public void must_insert_one_userkey()
    {
        UserKey key = new UserKey().setName("foo").setPrivateKeyMaterial(cipher("bar")).setPublicKeyMaterial(cipher("moo")).setUser(user);
        Integer userKeyId = userRepository.addUserKey(key);
        
        UserKey userKey = userRepository.findUserKeyById(userKeyId);
        assertThat(userKeyId, equalTo(userKey.getId()));
        
        List<UserKey> userKeys = userRepository.getUserKeys(user.getId());
        assertThat(1, equalTo(userKeys.size()));
        
    }
}
