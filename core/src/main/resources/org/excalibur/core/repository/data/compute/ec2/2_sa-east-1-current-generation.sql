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

-- Sao Paulo - current generation --
USE dohko;

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('t2.micro')),
	1, 0.027, CURRENT_TIMESTAMP
);


INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('t2.small')),
	1, 0.054, CURRENT_TIMESTAMP
);


INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('t2.medium')),
	1, 0.108, CURRENT_TIMESTAMP
);


INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m3.medium')),
	1, 0.095, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m3.large')),
	1, 0.190, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m3.xlarge')),
	1, 0.381, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m3.2xlarge')),
	1, 0.761, CURRENT_TIMESTAMP
);

--

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('c3.large')),
	1, 0.163, CURRENT_TIMESTAMP
);


INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('c3.xlarge')),
	1, 0.325, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('c3.2xlarge')),
	1, 0.650, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('c3.4xlarge')),
	1, 1.300, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('c3.8xlarge')),
	1, 2.600, CURRENT_TIMESTAMP
);