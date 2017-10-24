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
--         Instance cost per region            ---
--------------------------------------------------
 
--------
-- US --
--------

-- standard --
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (204, (select id from instance_type where lower(name) = lower('n1-standard-1')), 3, 1, 0.07, CURRENT_TIMESTAMP);

INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (204, (select id from instance_type where lower(name) = lower('n1-standard-2')), 3, 1, 0.14, CURRENT_TIMESTAMP);

INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (204, (select id from instance_type where lower(name) = lower('n1-standard-4')), 3, 1, 0.28, CURRENT_TIMESTAMP);

INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (204, (select id from instance_type where lower(name) = lower('n1-standard-8')), 3, 1, 0.56, CURRENT_TIMESTAMP);

INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (204, (select id from instance_type where lower(name) = lower('n1-standard-16')), 3, 1, 1.12, CURRENT_TIMESTAMP);

--TODO: This is not correct, because the price is defined by region and not by zone. Please, refactor this.
--
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (205, (select id from instance_type where lower(name) = lower('n1-standard-1')), 3, 1, 0.07, CURRENT_TIMESTAMP);

INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (205, (select id from instance_type where lower(name) = lower('n1-standard-2')), 3, 1, 0.14, CURRENT_TIMESTAMP);

INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (205, (select id from instance_type where lower(name) = lower('n1-standard-4')), 3, 1, 0.28, CURRENT_TIMESTAMP);

INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (205, (select id from instance_type where lower(name) = lower('n1-standard-8')), 3, 1, 0.56, CURRENT_TIMESTAMP);

INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (205, (select id from instance_type where lower(name) = lower('n1-standard-16')), 3, 1, 1.12, CURRENT_TIMESTAMP);----------------------

-- micro --
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (204, (select id from instance_type where lower(name) = lower('f1-micro')), 3, 1, 0.013, CURRENT_TIMESTAMP);
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (204, (select id from instance_type where lower(name) = lower('g1-small')), 3, 1, 0.035, CURRENT_TIMESTAMP);
--
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (205, (select id from instance_type where lower(name) = lower('f1-micro')), 3, 1, 0.013, CURRENT_TIMESTAMP);
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (205, (select id from instance_type where lower(name) = lower('g1-small')), 3, 1, 0.035, CURRENT_TIMESTAMP);----------------------

-- High Memory Machine Types --
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (204, (select id from instance_type where lower(name) = lower('n1-highmem-2')), 3, 1, 0.164, CURRENT_TIMESTAMP);
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (204, (select id from instance_type where lower(name) = lower('n1-highmem-4')), 3, 1, 0.328, CURRENT_TIMESTAMP);
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (204, (select id from instance_type where lower(name) = lower('n1-highmem-8')), 3, 1, 0.656, CURRENT_TIMESTAMP);
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (204, (select id from instance_type where lower(name) = lower('n1-highmem-16')), 3, 1, 1.312, CURRENT_TIMESTAMP);

--
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (205, (select id from instance_type where lower(name) = lower('n1-highmem-2')), 3, 1, 0.164, CURRENT_TIMESTAMP);
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (205, (select id from instance_type where lower(name) = lower('n1-highmem-4')), 3, 1, 0.328, CURRENT_TIMESTAMP);
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (205, (select id from instance_type where lower(name) = lower('n1-highmem-8')), 3, 1, 0.656, CURRENT_TIMESTAMP);
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (205, (select id from instance_type where lower(name) = lower('n1-highmem-16')), 3, 1, 1.312, CURRENT_TIMESTAMP);

-- High CPU Machine Types
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (204, (select id from instance_type where lower(name) = lower('n1-highcpu-2')), 3, 1, 0.088, CURRENT_TIMESTAMP);
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (204, (select id from instance_type where lower(name) = lower('n1-highcpu-4')), 3, 1, 0.176, CURRENT_TIMESTAMP);
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (204, (select id from instance_type where lower(name) = lower('n1-highcpu-8')), 3, 1, 0.352, CURRENT_TIMESTAMP);
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (204, (select id from instance_type where lower(name) = lower('n1-highcpu-16')), 3, 1, 0.704, CURRENT_TIMESTAMP);

--
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (205, (select id from instance_type where lower(name) = lower('n1-highcpu-2')), 3, 1, 0.088, CURRENT_TIMESTAMP);
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (205, (select id from instance_type where lower(name) = lower('n1-highcpu-4')), 3, 1, 0.176, CURRENT_TIMESTAMP);
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (205, (select id from instance_type where lower(name) = lower('n1-highcpu-8')), 3, 1, 0.352, CURRENT_TIMESTAMP);
INSERT INTO instance_type_cost (region_provider_id, instance_type_id, unit_time_id, minimal_time, cost_per_unit_time, start_in) 
VALUES (205, (select id from instance_type where lower(name) = lower('n1-highcpu-16')), 3, 1, 0.704, CURRENT_TIMESTAMP);
