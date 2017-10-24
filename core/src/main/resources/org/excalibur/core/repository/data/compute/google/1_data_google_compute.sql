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

INSERT INTO region (id, geographic_region_id, name, endpoint) VALUES (200, 2, 'asia-east1-a',   'https://www.googleapis.com/compute/v1/projects/poised-bot-553/zones/asia-east1-a');
INSERT INTO region (id, geographic_region_id, name, endpoint) VALUES (201, 2, 'asia-east1-b',   'https://www.googleapis.com/compute/v1/projects/poised-bot-553/zones/asia-east1-b');
INSERT INTO region (id, geographic_region_id, name, endpoint) VALUES (202, 4, 'europe-west1-a', 'https://www.googleapis.com/compute/v1/projects/poised-bot-553/zones/europe-west1-a');
INSERT INTO region (id, geographic_region_id, name, endpoint) VALUES (203, 4, 'europe-west1-b', 'https://www.googleapis.com/compute/v1/projects/poised-bot-553/zones/europe-west1-b');
INSERT INTO region (id, geographic_region_id, name, endpoint) VALUES (204, 5, 'us-central1-a',  'https://www.googleapis.com/compute/v1/projects/poised-bot-553/zones/us-central1-a');
INSERT INTO region (id, geographic_region_id, name, endpoint) VALUES (205, 5, 'us-central1-b',  'https://www.googleapis.com/compute/v1/projects/poised-bot-553/zones/us-central1-b');


----------------------------------
--     Regions vs Provider     --- 
----------------------------------
INSERT INTO region_provider (id, provider_id, region_id) VALUES (200, 2, 200);
INSERT INTO region_provider (id, provider_id, region_id) VALUES (201, 2, 201);
INSERT INTO region_provider (id, provider_id, region_id) VALUES (202, 2, 202);
INSERT INTO region_provider (id, provider_id, region_id) VALUES (203, 2, 203);
INSERT INTO region_provider (id, provider_id, region_id) VALUES (204, 2, 204);
INSERT INTO region_provider (id, provider_id, region_id) VALUES (205, 2, 205);


----------------------------------
--       Instance types        --- 
----------------------------------

-- Standard marchine types
INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units, number_compute_units_core, memory_size_gb, storage_size_gb, ub_number_instance, generation_number, 
	family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES 
(
	200, 2, 'n1-standard-1', 2.75, 1, 3.75, 10, 20, 1, 1, 21.82, 0.7
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units, number_compute_units_core, memory_size_gb, storage_size_gb, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES 
(
	201, 2, 'n1-standard-2', 5.50, 2, 7.50, 10, 20, 1, 1, 19.34, 0.7
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units, number_compute_units_core, memory_size_gb, storage_size_gb, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES
(
	202, 2, 'n1-standard-4', 11, 4, 15, 10, 20, 1, 1, 22.85, 0.7
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units, number_compute_units_core, memory_size_gb, storage_size_gb, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES
(
	203, 2, 'n1-standard-8', 22, 8, 30, 10, 20, 1, 1, 74.65, 0.8
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units, number_compute_units_core, memory_size_gb, storage_size_gb, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES
(
	204, 2, 'n1-standard-16', 44, 16, 60, 10, 20, 1, 1, 77.83, 0.8
);


-----------------

----------------------------------
-- High Memory Machine Types
----------------------------------
INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units, number_compute_units_core, memory_size_gb, storage_size_gb, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES
(
	205, 2, 'n1-highmem-2', 5.50, 2, 13.0, 10, 20, 1, 4, 19.94, 0.7
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units, number_compute_units_core, memory_size_gb, storage_size_gb, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES
(
	206, 2, 'n1-highmem-4', 11, 4, 26.0, 10, 20, 1, 4, 37.16, 0.7
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units, number_compute_units_core, memory_size_gb, storage_size_gb, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES
(
	207, 2, 'n1-highmem-8', 22, 8, 52.0, 10, 20, 1, 4, 72.6, 0.7
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units, number_compute_units_core, memory_size_gb, storage_size_gb, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES
(
	208, 2, 'n1-highmem-16', 44, 16, 104, 10, 20, 1, 4, 79.06, 0.7
);

----------------------------------
-- High CPU Machine Types
----------------------------------
INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units, number_compute_units_core, memory_size_gb, storage_size_gb, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES 
(
	209, 2, 'n1-highcpu-2', 5.50, 2, 1.8, 10, 20, 1, 2, 19.98, 0.6
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units, number_compute_units_core, memory_size_gb, storage_size_gb, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES 
(
	210, 2, 'n1-highcpu-4', 11, 4, 3.6, 10, 20, 1, 2,  38.62, 0.8
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units, number_compute_units_core, memory_size_gb, storage_size_gb, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES
(
	211, 2, 'n1-highcpu-8', 22, 8, 7.2, 10, 20, 1, 2, 66.8, 0.8
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units, number_compute_units_core, memory_size_gb, storage_size_gb, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES
(
	212, 2, 'n1-highcpu-16', 44, 16, 14.4, 10, 20, 1, 2, 81.60, 0.8
);

----------------------------------
-- Shared-Core Machine Types
----------------------------------

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units, number_compute_units_core, memory_size_gb, storage_size_gb, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES
(
	213, 2, 'f1-micro', 1.00, 1, 0.6, 10, 20, 1, 6, 1, 0.5 
);

INSERT INTO instance_type 
(
	id, provider_id, name, number_compute_units, number_compute_units_core, memory_size_gb, storage_size_gb, ub_number_instance, 
	generation_number, family_type_id, sustainable_performance_gflops, external_net_throughput
)
VALUES 
(
	214, 2, 'g1-small', 1.40, 1, 1.7, 10, 20, 1, 6, 1, 0.5
);

insert into virtual_instance_image
(
	region_id, hypervisor_id, virtualization_type_id, os_architecture_id, default_username, name, platform, endpoint
)
values 
(
	200, 2, 1, 2, 
	'debian', 'debian-7-wheezy', 
	'Linux', 'https://www.googleapis.com/compute/v1/projects/debian-cloud/global/images/debian-7-wheezy-v20140408'
);


----------------------------------
--    Virtual machine images    --
----------------------------------

insert into virtual_instance_image (region_id, hypervisor_id, virtualization_type_id, os_architecture_id, default_username, name, platform, endpoint)
values (201, 2, 1, 2, 'ubuntu', 'debian-7-wheezy', 'Linux', 'https://www.googleapis.com/compute/v1/projects/debian-cloud/global/images/debian-7-wheezy-v20140408');

insert into virtual_instance_image (region_id, hypervisor_id, virtualization_type_id, os_architecture_id, default_username, name, platform, endpoint)
values (202, 2, 1, 2, 'ubuntu', 'debian-7-wheezy', 'Linux', 'https://www.googleapis.com/compute/v1/projects/debian-cloud/global/images/debian-7-wheezy-v20140408');

insert into virtual_instance_image (region_id, hypervisor_id, virtualization_type_id, os_architecture_id, default_username, name, platform, endpoint)
values (203, 2, 1, 2, 'ubuntu', 'debian-7-wheezy', 'Linux', 'https://www.googleapis.com/compute/v1/projects/debian-cloud/global/images/debian-7-wheezy-v20140408');

insert into virtual_instance_image (region_id, hypervisor_id, virtualization_type_id, os_architecture_id, default_username, name, platform, endpoint)
values (204, 2, 1, 2, 'ubuntu', 'debian-7-wheezy', 'Linux', 'https://www.googleapis.com/compute/v1/projects/debian-cloud/global/images/debian-7-wheezy-v20140408');

insert into virtual_instance_image (region_id, hypervisor_id, virtualization_type_id, os_architecture_id, default_username, name, platform, endpoint)
values (205, 2, 1, 2, 'ubuntu', 'debian-7-wheezy', 'Linux', 'https://www.googleapis.com/compute/v1/projects/debian-cloud/global/images/debian-7-wheezy-v20140408');


--------------------------------------------------
--   Zones    ---
--------------------------------------------------

insert into zone (region_id , name )  values ( (select id from region where name = 'us-central1-a'), 'us-central1-a');
insert into zone (region_id , name )  values ( (select id from region where name = 'us-central1-b'), 'us-central1-b');

insert into zone (region_id , name )  values ( (select id from region where name = 'asia-east1-a'), 'asia-east1-a');
insert into zone (region_id , name )  values ( (select id from region where name = 'asia-east1-b'), 'asia-east1-b');


insert into zone (region_id , name )  values ( (select id from region where name = 'europe-west1-a'), 'europe-west1-a');
insert into zone (region_id , name )  values ( (select id from region where name = 'europe-west1-b'), 'europe-west1-b');


--------------------------------------------------
--   Instance type vs Family type (purpose)    ---
--------------------------------------------------
--general
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (2, 1, 200);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (2, 1, 201);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (2, 1, 202);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (2, 1, 203);
-- Memory
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (2, 4, 204);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (2, 4, 205);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (2, 4, 206);
--CPU
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (2, 2, 207);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (2, 2, 208);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (2, 2, 209);
--Shared
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (2, 6, 210);
--INSERT INTO provider_family_type_mapping (provider_id, family_type_id, instance_type_id) VALUES (2, 6, 211);
