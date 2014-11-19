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

insert into user (id, username, passwd) values (1, 'root','<a password hashed with the md5 algorithm>');
insert into user_provider_credential (user_id, provider_id, access_identity, access_credential) 
VALUES (2, 1,'<your access key>', '<your secret key>');
insert into user_provider_credential (user_id, provider_id, access_identity, access_credential, project_name) 
VALUES (2, 2,'compute-service-account.json', 'compute-service-privatekey.p12', '<your project name on GCE>');

