--
--     Copyright (C) 2013-2014  the original author or authors.
--
--     This program is free software: you can redistribute it and/or modify
--     it under the terms of the GNU General Public License as published by
--     the Free Software Foundation, either version 3 of the License,
--     any later version.
--
--     This program is distributed in the hope that it will be useful,
--     but WITHOUT ANY WARRANTY; without even the implied warranty of
--     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--     GNU General Public License for more details.
--
--     You should have received a copy of the GNU General Public License
--     along with this program.  If not, see <http://www.gnu.org/licenses/>
--

insert into user (id, username, passwd) values (1, 'vagrant','vagrant');
--insert into user_provider_credential (user_id, provider_id, access_identity, access_credential) 
--VALUES (3, 1,'<your access key>', '<your secret key>');
--insert into user_provider_credential (user_id, provider_id, access_identity, access_credential, project_name) 
--VALUES (2, 2,'compute-service-account.json', 'compute-service-privatekey.p12', '<your project name on GCE>');
insert into user_provider_credential (user_id, provider_id, access_identity, access_credential) VALUES (1, 3,'local', 'local');

INSERT INTO user_key (user_id, name, public_key_material, private_key_material) 
VALUES (1, 'vagrant', 
'ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQCuaVnVvtcMJrYKsV26TU5TwLD09vHJVLI4tOa5JEhUBuzQBxSGWNtp6aAZnPlOzR1YjBLYdFBfbWOAcTyzSsZStW18u2maqdF/B0VAWGK23PCRpHHCH8qIpRYRbpSYSzg6xVE1gSy9xaOUTEYzNaoF/pmoS6EjJPb+SKJzcqy2j+yoNyzAE+qbjlCu35LKQ6hVAmqPu3+dBFX1C3vbKbZrshvuNOo74H11ycQeV09llPMAyjKqDnv5FcHqJClBbhcd3tqkldFRU8OAKjp+/QkILXRogc5dzw+1GNVisWMvJGTh1sELCE7SQd/aO+p/kd6BLNnM/QvwC1EfDSsGJbGtL7CWqpFrDZB6Pmhlj9PNoe3Es2k5gp2QDH+2TSVsS+6G0nZ4qswV4IhP4g25NUw9Zq1ruL1iGWoO/a0CjNYNlxdn10M7fvA48S8FZXf2HvZt63H8QibM+sLwHJ9WoQm9J9O0ibsHvP/8GpO5RpDlTEo9PmFopKs1nTKIv6LPcnkwJsXlV9yu7XW99GW6/fKhEqXX/ufyptCqPfJnr/s+JHmUgi126TA/hP3UZG5KvVvYw41jnRRbF/QZQNlOpGOQyWR1Jp9Hp1z8n9uvDPbVMPszoskG9TbrWpOimohtE5ML2szuYdLAXyHWbzhtCXKomCfE6vAOaWwPh1qlls/jfQ== vagrant@dohko-lab', 
'-----BEGIN RSA PRIVATE KEY-----
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
-----END RSA PRIVATE KEY-----');
