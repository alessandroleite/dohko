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

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m1.small')),
	1, 0.058, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m1.medium')),
	1, 0.117, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m1.large')),
	1, 0.233, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m1.xlarge')),
	1, 0.467, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('c1.medium')),
	1, 0.179, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('c1.xlarge')),
	1, 0.718, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m2.xlarge')),
	1, 0.323, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m2.2xlarge')),
	1, 0.645, CURRENT_TIMESTAMP
);


INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m2.4xlarge')),
	1, 1.291, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'sa-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('t1.micro')),
	1, 0.027, CURRENT_TIMESTAMP
);

