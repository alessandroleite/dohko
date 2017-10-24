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
--

USE dohko;

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('t2.micro')),
	1, 0.020, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('t2.small')),
	1, 0.040, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('t2.medium')),
	1, 0.080, CURRENT_TIMESTAMP
);

--

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m3.medium')),
	1, 0.101, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m3.large')),
	1, 0.203, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m3.xlarge')),
	1, 0.405, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('m3.2xlarge')),
	1, 0.810, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('c3.large')),
	1, 0.128, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('c3.xlarge')),
	1, 0.255, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('c3.2xlarge')),
	1, 0.511, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('c3.4xlarge')),
	1, 1.021, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('c3.8xlarge')),
	1, 2.043, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('g2.2xlarge')),
	1, 0.898, CURRENT_TIMESTAMP
);


INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('r3.large')),
	1, 0.210, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('r3.xlarge')),
	1, 0.420, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('r3.2xlarge')),
	1, 0.840, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('r3.4xlarge')),
	1, 1.680, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('r3.8xlarge')),
	1, 3.360, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('i2.xlarge')),
	1, 1.001, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('i2.2xlarge')),
	1, 2.001, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('i2.4xlarge')),
	1, 4.002, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('i2.8xlarge')),
	1, 8.004, CURRENT_TIMESTAMP
);

INSERT INTO instance_type_cost (region_provider_id, unit_time_id, instance_type_id, minimal_time, cost_per_unit_time, start_in)
VALUES
(
	(select rp.id from region_provider rp join region r on r.id = rp.region_id and r.name = 'ap-northeast-1'),
	(select id from unit_time where lower(name) = 'hour'),
	(select id from instance_type where lower(name) = lower('hs1.8xlarge')),
	1, 5.400, CURRENT_TIMESTAMP
);