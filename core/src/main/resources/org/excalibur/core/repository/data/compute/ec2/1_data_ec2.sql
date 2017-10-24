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

------------------------------------ 
---          Regions             ---
------------------------------------
insert into region (id, geographic_region_id, name, endpoint, city_name) values (100, 5, 'us-east-1',   	'https://ec2.us-east-1.amazonaws.com', 		'Virginia');
insert into region (id, geographic_region_id, name, endpoint, city_name) values (101, 5, 'us-west-2', 		'https://ec2.us-west-2.amazonaws.com', 		'Oregon');
insert into region (id, geographic_region_id, name, endpoint, city_name) values (102, 5, 'us-west-1', 		'https://ec2.us-west-1.amazonaws.com', 		'California');
insert into region (id, geographic_region_id, name, endpoint, city_name) values (103, 4, 'eu-west-1', 		'https://ec2.eu-west-1.amazonaws.com', 		'Ireland');
insert into region (id, geographic_region_id, name, endpoint, city_name) values (104, 2, 'ap-southeast-1', 	'https://ec2.ap-southeast-1.amazonaws.com', 'Singapore');
insert into region (id, geographic_region_id, name, endpoint, city_name) values (105, 2, 'ap-southeast-2', 	'https://ec2.ap-southeast-2.amazonaws.com', 'Sydney');
insert into region (id, geographic_region_id, name, endpoint, city_name) values (106, 2, 'ap-northeast-1', 	'https://ec2.ap-northeast-1.amazonaws.com', 'Tokyo');
insert into region (id, geographic_region_id, name, endpoint, city_name) values (107, 6, 'sa-east-1', 		'https://ec2.sa-east-1.amazonaws.com', 		'São Paulo');


----------------------------------
--     Regions vs Provider     --- 
----------------------------------
INSERT INTO region_provider (id, provider_id, region_id) VALUES (100, 1, 100);
INSERT INTO region_provider (id, provider_id, region_id) VALUES (101, 1, 101);
INSERT INTO region_provider (id, provider_id, region_id) VALUES (102, 1, 102);
INSERT INTO region_provider (id, provider_id, region_id) VALUES (103, 1, 103);
INSERT INTO region_provider (id, provider_id, region_id) VALUES (104, 1, 104);
INSERT INTO region_provider (id, provider_id, region_id) VALUES (105, 1, 105);
INSERT INTO region_provider (id, provider_id, region_id) VALUES (106, 1, 106);
INSERT INTO region_provider (id, provider_id, region_id) VALUES (107, 1, 107);

----------------------------------
--            Zones            --- 
----------------------------------
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'ap-northeast-1'), 'ap-northeast-1a');
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'ap-northeast-1'), 'ap-northeast-1b');
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'ap-northeast-1'), 'ap-northeast-1c');
--
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'ap-southeast-1'), 'ap-southeast-1a');
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'ap-southeast-1'), 'ap-southeast-1b');
--
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'ap-southeast-2'), 'ap-southeast-2a');
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'ap-southeast-2'), 'ap-southeast-2b');
--
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'eu-west-1'), 'eu-west-1a');
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'eu-west-1'), 'eu-west-1b');
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'eu-west-1'), 'eu-west-1c');
--
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'sa-east-1'), 'sa-east-1a');
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'sa-east-1'), 'sa-east-1b');
--
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'us-east-1'), 'us-east-1a');
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'us-east-1'), 'us-east-1b');
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'us-east-1'), 'us-east-1c');
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'us-east-1'), 'us-east-1d');
--
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'us-west-2'), 'us-west-2a');
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'us-west-2'), 'us-west-2b');
insert into zone (region_id, name) values ((select r.id from region r where r.name = 'us-west-2'), 'us-west-2c');

------------------------------------ 
--- Instance types Amazon AWS    ---
------------------------------------

-- General Purpose - Current Generation
INSERT INTO instance_type 
(
   id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
   generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES 
(
	100, 1, 'm3.medium', 1, 3, 3.75, 4, 1, 20, 3, 1, 9.65, 0.3
);

INSERT INTO instance_type 
(	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES 
(
	101, 1, 'm3.large', 2, 6.5, 7.5, 32, 1, 20, 3, 1, 20.35, 0.7
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES 
(
	102, 1, 'm3.xlarge', 4, 13, 15, 40, 2, 20, 3, 1, 32.70, 0.9
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES 
(
	103, 1, 'm3.2xlarge', 8, 26, 30, 80, 2, 20, 3, 1, 72.12, 1.0
);

-- Compute Optimized - Current Generation
INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES
(
	104, 1, 'c3.large', 2, 7, 3.75, 16, 2, 20, 3, 2, 21.60, 9.3
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES 
(
	105, 1, 'c3.xlarge', 4, 14, 7.5, 40, 2, 20, 3, 2, 18.18, 9.3
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES 
(
	106, 1, 'c3.2xlarge', 8, 28, 15, 80, 2, 20, 3, 2, 87.61, 9.3
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES 
(	
	107, 1, 'c3.4xlarge', 16, 55, 30, 160, 2, 20, 3, 2, 72.74, 9.3
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES
(
	108, 1, 'c3.8xlarge', 32, 108, 60, 320, 2, 20, 3, 2, 9.3
);

-- GPU Instances - Current Generation
INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES 
(
	109, 1, 'g2.2xlarge', 8, 26, 15, 60, 1, 4, 2, 3, 9.3
);

--Memory Optimized - Current Generation
INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES 
(
	110, 1, 'r3.large', 2, 6.5, 15, 32, 1, 20, 3, 4, 9.3
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES 
(
	111, 1, 'r3.xlarge', 4, 13, 30.5, 80, 1, 20, 3, 4, 9.3
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES 
(
	112, 1, 'r3.2xlarge', 8, 26, 61, 160, 1, 20, 3, 4, 84.4528, 9.3
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES 
(
	113, 1, 'r3.4xlarge', 16, 52, 122, 320, 1, 20, 3, 4, 138.86, 9.3
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES 
(
	114, 1, 'r3.8xlarge', 32, 104, 244, 320, 2, 20, 3, 4, 9.3
);

-- Storage Optimized - Current Generation
INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES
(
	115, 1, 'i2.xlarge', 4, 14, 30.5, 800, 1, 20, 2, 5, 9.3
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES 
(
	116, 1, 'i2.2xlarge', 8, 27, 61, 800, 2, 20, 2, 5, 9.3
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES
(
	117, 1, 'i2.4xlarge', 16, 53, 122, 800, 4, 2, 2, 5, 9.3
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES
(
	118, 1, 'i2.8xlarge', 32, 104, 244, 800, 8, 4, 2, 5, 9.3
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES
(
	119, 1, 'hs1.8xlarge', 16, 35, 117, 2048, 24, 2, 1, 5, 1.2
);

-- Micro instances - general purpose
INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES
(
	120, 1, 't2.micro', 1, 1, 1, 0, 0, 20, 2, 1, 0.4
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES 
(
	121, 1, 't2.small', 1, 1, 2, 0, 0, 20, 2, 1, 0.4
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES
(
	122, 1, 't2.medium', 2, 2, 4, 0, 0, 20, 2, 1, 0.4
);


--------------------------------------------------
--         AWS previous generations            ---
--------------------------------------------------


-- General purpose
INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES 
(
	123, 1, 'm1.small', 1, 1, 1.7, 160, 1, 20, 1, 1, 0.2
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES 
(
	124, 1, 'm1.medium', 1, 2, 3.75, 410, 1, 20, 1, 1, 0.7
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES
(
	125, 1, 'm1.large', 2, 4, 7.5, 420, 2, 20, 1, 1, 0.7
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES
(
	126, 1, 'm1.xlarge', 4, 8, 15, 420, 4, 20, 1, 1, 1
);

-- Compute optimized
INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES
(
	127, 1, 'c1.medium', 2, 5, 1.7, 350, 1, 20, 1, 2, 0.7
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES 
(
	128, 1, 'c1.xlarge', 8, 20, 7, 420, 4, 20, 1, 2, 1
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES 
(
	129, 1, 'cc2.8xlarge', 32, 88, 60.5, 840, 4, 20, 1, 2, 9.3
);


-- GPU optimized
INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES 
(
	130, 1, 'cg1.4xlarge', 16, 33.5, 22.5, 840, 2, 4, 1, 3, 9.3
);

-- Memory Optimized
INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES 
(
	131, 1, 'm2.xlarge', 2, 6.5, 17.1, 420, 1, 20, 1, 4, 0.7
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES 
(
	132, 1, 'm2.2xlarge', 4, 13, 34.2, 850, 1, 20, 1, 4, 0.7
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES
(
	133, 1, 'm2.4xlarge', 8, 26, 68.4, 840, 2, 20, 1, 4, 1
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES 
(
	134, 1, 'cr1.8xlarge', 32, 88, 244, 120, 2, 20, 1, 4, 9.3
);

-- Storage optimized
INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
	generation_number, family_type_id, external_net_throughput
)
VALUES 
(
	135, 1, 'hi1.4xlarge', 16, 35, 60.5, 1024, 2, 2, 1, 5, 9.3
);

-- Micro
INSERT INTO instance_type 
(
  id, provider_id, name, number_compute_units_core, number_compute_units, memory_size_gb, storage_size_gb, number_of_disk, ub_number_instance, 
  generation_number, family_type_id, external_net_throughput
)
VALUES 
(
	136, 1, 't1.micro', 1, 1, 0.615, 0, 0, 20, 1, 6, 0.2
);


-- hvm excludes these instance types
update instance_type set required_virtualization_type_id = 2 
where name in 
(
	't1.micro', 'm1.small', 'm1.medium', 'm1.large', 'm1.xlarge', 'c1.medium', 'c1.xlarge', 'm2.xlarge', 'm2.2xlarge', 'm2.4xlarge'
);
  
-- paravirtual extends these instance types
update instance_type set required_virtualization_type_id = 1 
where name in 
(
	't2.micro', 't2.small', 't2.medium', 'cc2.8xlarge', 'cc1.4xlarge', 'g2.2xlarge', 'cg1.4xlarge', 'r3.large', 'r3.xlarge', 'r3.2xlarge',
	'r3.4xlarge', 'r3.8xlarge', 'cr1.8xlarge', 'i2.xlarge', 'i2.2xlarge', 'i2.4xlarge', 'i2.8xlarge'
);
  


--------------------------------------------------
--   Instance type vs Family type (purpose)    ---
--------------------------------------------------
-- General purpose
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 1, 100);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 1, 101);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 1, 102);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 1, 103);
--Compute optimized
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 2, 104);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 2, 105);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 2, 106);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 2, 107);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 2, 108);
--GPU optimized
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 3, 109);
--Memory optimized
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 4, 110);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 4, 111);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 4, 112);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 4, 113);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 4, 114);
-- Storage optimized
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 5, 115);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 5, 116);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 5, 117);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 5, 118);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 5, 119);
-- Micro and Small Instances
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 6, 120);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (1, 6, 121);
