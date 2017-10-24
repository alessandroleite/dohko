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
  (select id from provider where name = 'amazon'), 
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
   (select id from provider where name = 'amazon'), 
   'standard', 
   1, 
   1024,
   100,
   null
);

-- References: http://aws.amazon.com/ebs/pricing/effective-july-2014/ and http://aws.amazon.com/ebs/pricing/

--------------------------------------------------
---           Disk cost per region             ---
--------------------------------------------------
-- General Purpose (SSD) volumes
--------------------------------------------------
insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'us-east-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),                                          
  (SELECT id FROM disk_type where name = 'gp2' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.10, 
  0,
  0,
  '2012-01-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'us-west-2')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),                                        
  (SELECT id FROM disk_type where name = 'gp2' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.10, 
  0,
  0,
  '2012-01-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'us-west-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'gp2' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.12, 
  0,
  0,
  '2012-01-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'eu-west-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'gp2' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.11, 
  0,
  0,
  '2012-01-01',
  null
);


insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'ap-southeast-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'gp2' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.12, 
  0,
  0,
  '2012-01-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'ap-southeast-2')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'gp2' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.12, 
  0,
  0,
  '2012-01-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'ap-northeast-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'gp2' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.12, 
  0,
  0,
  '2012-01-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'sa-east-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'gp2' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.19, 
  0,
  0,
  '2012-01-01',
  null
);
--------------------------------------------------------------------------------------------------------------------------------------------------
-- EBS Provisioned IOPS (SSD) volumes
--------------------------------------------------
insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'us-east-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),                                          
  (SELECT id FROM disk_type where name = 'io1' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.125, 
  0.065,
  0,
  '2014-07-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'us-west-2')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),                                        
  (SELECT id FROM disk_type where name = 'io1' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.125, 
  0.065,
  0,
  '2014-07-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'us-west-1')),
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'io1' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.138, 
  0.072,
  0,
  '2014-07-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'eu-west-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'io1' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.138, 
  0.072,
  0,
  '2014-07-01',
  null
);


insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'ap-southeast-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'io1' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.138, 
  0.072,
  0,
  '2014-07-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'ap-southeast-2')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'io1' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.138, 
  0.072,
  0,
  '2014-07-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'ap-northeast-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'io1' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.142, 
  0.074,
  0,
  '2014-07-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'sa-east-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'io1' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.238, 
  0.091,
  0,
  '2014-07-01',
  null
);

--------------------------------------------------------------------------------------------------------------------------------------------------
-- EBS Magnetic volumes
--------------------------------------------------
insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'us-east-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),                                          
  (SELECT id FROM disk_type where name = 'standard' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.05, 
  0,
  0.05,
  '2012-01-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'us-west-2')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),                                        
  (SELECT id FROM disk_type where name = 'standard' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.05, 
  0,
  0.05,
  '2012-01-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'us-west-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'standard' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.08, 
  0,
  0.08,
  '2012-01-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'eu-west-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'standard' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.055, 
  0,
  0.055,
  '2012-01-01',
  null
);


insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'ap-southeast-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'standard' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.08, 
  0,
  0.08,
  '2012-01-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'ap-southeast-2')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'standard' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.08, 
  0,
  0.08,
  '2012-01-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'ap-northeast-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'standard' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.080, 
  0,
  0.080,
  '2012-01-01',
  null
);

insert into disk_type_cost (region_provider_id, unit_time_id, disk_type_id, cost_storage_gb, cost_iops, cost_million_request, start_in, finish_in)
VALUES
(
  (SELECT id FROM region_provider where provider_id = (SELECT id from provider where lower(name) = 'amazon') AND 
                                          region_id = (SELECT id from region where name = 'sa-east-1')), 
  (SELECT id FROM unit_time where lower(name) = 'month'),
  (SELECT id FROM disk_type where name = 'standard' AND provider_id = (SELECT id from provider where lower(name) = 'amazon')),
  0.12, 
  0,
  0.12,
  '2012-01-01',
  null
);