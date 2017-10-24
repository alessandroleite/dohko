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
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'us-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m1.small')),
	1, 0.044, CURRENT_TIMESTAMP
);


INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'us-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m1.medium')),
	1, 0.087 , CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'us-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m1.large')),
	1, 0.175 , CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'us-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m1.xlarge')),
	1, 0.350 , CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'us-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('c1.medium')),
	1, 0.130, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'us-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('c1.xlarge')),
	1, 0.520 , CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'us-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('cc2.8xlarge')),
	1, 2.000 , CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'us-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('cg1.4xlarge')),
	1, 2.100 , CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'us-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m2.xlarge')),
	1, 0.245, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'us-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m2.2xlarge')),
	1, 0.490, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'us-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m2.4xlarge')),
	1, 0.980 , CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'us-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('cr1.8xlarge')),
	1, 3.500 , CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'us-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('hi1.4xlarge')),
	1, 3.100 , CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'us-east-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('t1.micro')),
	1, 0.020, CURRENT_TIMESTAMP
);
