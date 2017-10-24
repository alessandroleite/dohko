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

USE dohko;

----------------------------------
--          Regions            --- 
----------------------------------

INSERT INTO region (id, geographic_region_id, name, endpoint) VALUES (300, 5, 'local', '');

----------------------------------
--     Regions vs Provider     --- 
----------------------------------

INSERT INTO region_provider (id, provider_id, region_id) VALUES (300, 3, 300);

----------------------------------
--   		  Zones    		   ---
----------------------------------
insert into zone (region_id , name )  values ( (select id from region where name = 'local'), 'local');