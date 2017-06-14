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

INSERT INTO user_key (user_id, name, public_key_material, private_key_material) VALUES (1, 'vagrant', 'ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQCuaVnVvtcMJrYKsV26TU5TwLD09vHJVLI4tOa5JEhUBuzQBxSGWNtp6aAZnPlOzR1YjBLYdFBfbWOAcTyzSsZStW18u2maqdF/B0VAWGK23PCRpHHCH8qIpRYRbpSYSzg6xVE1gSy9xaOUTEYzNaoF/pmoS6EjJPb+SKJzcqy2j+yoNyzAE+qbjlCu35LKQ6hVAmqPu3+dBFX1C3vbKbZrshvuNOo74H11ycQeV09llPMAyjKqDnv5FcHqJClBbhcd3tqkldFRU8OAKjp+/QkILXRogc5dzw+1GNVisWMvJGTh1sELCE7SQd/aO+p/kd6BLNnM/QvwC1EfDSsGJbGtL7CWqpFrDZB6Pmhlj9PNoe3Es2k5gp2QDH+2TSVsS+6G0nZ4qswV4IhP4g25NUw9Zq1ruL1iGWoO/a0CjNYNlxdn10M7fvA48S8FZXf2HvZt63H8QibM+sLwHJ9WoQm9J9O0ibsHvP/8GpO5RpDlTEo9PmFopKs1nTKIv6LPcnkwJsXlV9yu7XW99GW6/fKhEqXX/ufyptCqPfJnr/s+JHmUgi126TA/hP3UZG5KvVvYw41jnRRbF/QZQNlOpGOQyWR1Jp9Hp1z8n9uvDPbVMPszoskG9TbrWpOimohtE5ML2szuYdLAXyHWbzhtCXKomCfE6vAOaWwPh1qlls/jfQ== vagrant@dohko-lab', '');
