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
package org.excalibur.core.domain.repository;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.cloud.api.ProviderSupport;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.UserKey;
import org.excalibur.core.domain.UserProviderCredentials;
import org.excalibur.core.domain.repository.ProviderRepository.ProviderRowMapper;
import org.excalibur.core.domain.repository.UserRepository.UserResultSetRowMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.google.common.base.Strings;

import io.dohko.jdbi.stereotype.Repository;

//import static org.excalibur.core.cipher.TripleDESUtils.*;

@Repository
@RegisterMapper(UserResultSetRowMapper.class)
public interface UserRepository extends Closeable
{
    @SqlUpdate("INSERT INTO user (username, passwd) VALUES (:username, :password)")
    @GetGeneratedKeys
    Integer insert(@BindBean User user);

    @SqlQuery("SELECT id as user_id, username, passwd FROM user WHERE id = :userId")
    User findUserById(@Bind("userId") Integer id);

    @SqlQuery("SELECT id as user_id, username, passwd FROM user WHERE username = :username")
    User findUserByUsername(@Bind("username") String username);

    @SqlQuery("SELECT id as user_id, username, passwd FROM user ORDER BY id")
    List<User> getAllUsers();

    @SqlUpdate("INSERT INTO user_key (user_id, name, public_key_material, private_key_material) VALUES (:user.id, :name, :publicKeyMaterial, :privateKeyMaterial)")
    @GetGeneratedKeys
    Integer addUserKey(@BindBean UserKey key);
    
    @SqlBatch("INSERT INTO user_key (user_id, name, public_key_material, private_key_material) VALUES (:user.id, :name, :publicKeyMaterial, :privateKeyMaterial)")
    @BatchChunkSize(10)
    void addUserKeys(@BindBean Iterable<UserKey> keys);
 
    @SqlUpdate("UPDATE user_key SET public_key_material = :publicKeyMaterial, private_key_material = :privateKeyMaterial\n" +
               "WHERE id = :id AND user_id = :user.id")
    void updateUserKey(@BindBean UserKey userKey);

    @RegisterMapper(UserProviderIdentityMapper.class)
    @SqlQuery("SELECT id as user_key_id, user_id, name, public_key_material, private_key_material FROM user_key WHERE id = :userKeyId")
    UserKey findUserKeyById(@Bind("userKeyId") Integer userKeyId);

    @RegisterMapper(UserProviderIdentityMapper.class)
    @SqlQuery("SELECT id as user_key_id, user_id, name, public_key_material, private_key_material FROM user_key WHERE user_id = :userId")
    List<UserKey> getUserKeys(@Bind("userId") Integer userId);
    
    @RegisterMapper(UserProviderIdentityMapper.class)
    @SqlQuery("SELECT id as user_key_id, user_id, name, public_key_material, private_key_material FROM user_key WHERE lower(name) = lower(:keyname)")
    UserKey findUserKeyByName(@Bind("keyname") String keyname);
    
    String SQL_QUERY_USERS_LOGIN_CREDENTIAL = "SELECT up.id as credential_id, up.user_id, up.provider_id, up.access_identity,\n" +
    		"up.access_credential, up.project_name, p.id as provider_id, p.name as provider_name, p.class_name, p.ub_instances_per_type\n" + 
            "FROM user_provider_credential up\n" +
            "JOIN provider p on p.id = up.provider_id\n";

    @SqlQuery(SQL_QUERY_USERS_LOGIN_CREDENTIAL + "WHERE user_id = :userId AND provider_id = :providerId")
    @RegisterMapper(UserLoginCredentialsMapper.class)
    UserProviderCredentials findLoginCredentialsOfUserForProvider(@Bind("userId") Integer userId, @Bind("providerId") Integer providerId);
    
    @SqlQuery(SQL_QUERY_USERS_LOGIN_CREDENTIAL + "WHERE user_id = (select u.id FROM user u WHERE lower(u.username) = lower (:username))\n" +
              " AND lower(p.name) = lower(:providerName)")
    @RegisterMapper(UserLoginCredentialsMapper.class)
    UserProviderCredentials findLoginCredentialsOfUserForProvider(@Bind("username") String username, @Bind("providerName") String providerName);
    
    @SqlQuery(SQL_QUERY_USERS_LOGIN_CREDENTIAL + "WHERE provider_id = :providerId")
    @RegisterMapper(UserLoginCredentialsMapper.class)
    List<UserProviderCredentials> listUsersLoginCredentialsOfProvider(@Bind("providerId") Integer providerId);

    @SqlQuery(SQL_QUERY_USERS_LOGIN_CREDENTIAL + " WHERE user_id = :userId AND lower(p.name) = lower(:providerName)")
    @RegisterMapper(UserLoginCredentialsMapper.class)
    UserProviderCredentials findLoginCredentialsOfUserForProvider(@Bind("userId") Integer userId, @Bind("providerName") String providerName);
    
    public static final class UserResultSetRowMapper implements ResultSetMapper<User>
    {
        @Override
        public User map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new User().setId(r.getInt("user_id")).setUsername(r.getString("username")).setPassword(r.getString("passwd"));
        }
    }

    public static final class UserProviderIdentityMapper implements ResultSetMapper<UserKey>
    {
        @Override
        public UserKey map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new UserKey()
                    .setId(r.getInt("user_key_id"))
                    .setName(r.getString("name"))
                    .setUser(new User().setId(r.getInt("user_id")))
//                    .setPrivateKeyMaterial(decrypt(r.getString("private_key_material")))
//                    .setPublicKeyMaterial(decrypt(r.getString("public_key_material")));
                    .setPrivateKeyMaterial(r.getString("private_key_material"))
                    .setPublicKeyMaterial(r.getString("public_key_material"));
        }
    }

    public static final class UserLoginCredentialsMapper implements ResultSetMapper<UserProviderCredentials>
    {
        @Override
        public UserProviderCredentials map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            LoginCredentials credentials = new LoginCredentials.Builder()
                    .credential(r.getString("access_credential"))
                    .identity(r.getString("access_identity"))
                    .authenticateAsSudo(true)
                    .build();

            return new UserProviderCredentials()
                    .setId(r.getInt("credential_id"))
                    .setLoginCredentials(credentials)
                    .setProject(Strings.nullToEmpty(r.getString("project_name")))
                    .setProvider((ProviderSupport) new ProviderRowMapper().map(index, r, ctx))
                    .setUserId(r.getInt("user_id"));
        }
    }
    
    void close();

    
}
