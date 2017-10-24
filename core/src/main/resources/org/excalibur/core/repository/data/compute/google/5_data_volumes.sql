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

--------------------------------------------------
---                 Disk type                  ---
--------------------------------------------------
INSERT INTO disk_type (provider_id, name, min_size_gb, max_size_gb, min_iops, max_iops) 
VALUES 
(
  (select id from provider where name = 'google'), 
  'gp2', 
  1, 
  1024,
  3,
  3000
);

INSERT INTO disk_type (provider_id, name, min_size_gb, max_size_gb, min_iops, max_iops) 
VALUES 
(
  (select id from provider where name = 'amazon'), 
  'io1', 
  10, 
  1024,
  100,
  4000  
);


INSERT INTO disk_type (provider_id, name, min_size_gb, max_size_gb, min_iops, max_iops) 
VALUES 
(
   (select id from provider where name = 'google'), 
   'standard', 
   1, 
   1024,
   100,
   null
);