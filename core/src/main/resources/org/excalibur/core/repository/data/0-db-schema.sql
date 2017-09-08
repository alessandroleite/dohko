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

create table unit_time
(
  id integer not null primary key, 
  name varchar(10) not null
);

create unique index idx_unit_time_name on unit_time (name);

create table geographic_region
(
  id integer not null primary key,
  name varchar(20) not null
);

create unique index idx_geographic_region_name on geographic_region (name);

create table provider
(
  id IDENTITY  not null primary key,
  name varchar(45) not null,
  class_name varchar(200) not null,
  ub_instances_per_type int not null   
);

create table region
(
  id IDENTITY not null primary key,
  geographic_region_id integer not null references geographic_region(id),
  name varchar(120) not null,
  endpoint varchar(255) not null,
  city_name varchar(60)
);

create unique index idx_region_name on region(name);
create index idx_region_greographic_id on region(geographic_region_id);

create table zone
(
  id IDENTITY not null primary key,
  region_id integer not null references region (id),
  name varchar(30) not null  
);

create index idx_zone_region on zone (region_id);
create unique index idx_zone_name on zone (name);

create table region_provider 
(
  id IDENTITY not null primary key,
  region_id integer not null references region (id),
  provider_id integer not null references provider (id)  
);

create unique index idx_region_provider_k on region_provider (region_id, provider_id);

create table instance_family_type
(
  id integer not null primary key,
  name varchar(100) not null
);

create unique index idx_ift_name on instance_family_type (name);


create table virtual_instance_image
( 
  id IDENTITY not null primary key,
  region_id integer not null references region(id),
  hypervisor_id integer not null,
  virtualization_type_id integer not null,
  os_architecture_id integer not null,
  default_username varchar(20) not null,
  name varchar(20) not null,
  platform varchar(7) not null,
  endpoint varchar(255),
  description varchar(120),  
  check (os_architecture_id in (1,2)) 
);

create unique index idx_vmi_name on virtual_instance_image (name, region_id);
create index idx_vmi_region_id on virtual_instance_image (region_id);

create table instance_type
(
  id IDENTITY not null primary key,
  provider_id integer not null references provider(id),
  family_type_id integer not null references instance_family_type (id),
  name varchar(60) not null,
  number_compute_units int not null,
  number_compute_units_core int not null,
  memory_size_gb decimal(10,6) not null,
-- TODO: rename the name of this column to local_disk_size  
  storage_size_gb decimal(10,6) not null,
-- rename it to number_local_disk
  number_of_disk integer default 0 not null,
  ub_number_instance integer,
  generation_number integer not null,  
  internal_net_throughput decimal(10,6),
  external_net_throughput decimal(10,6),
  sustainable_performance_gflops decimal(10,6),  
-- it can be: 1 -> hvm, 2 -> pvm, and 3 -> any  
  required_virtualization_type_id integer,  
  support_placement_group char(1) default 'N' not null,
  check (number_compute_units > 0),
  check (number_compute_units_core > 0),
  check (generation_number > 0),
  check (memory_size_gb > 0),
  check (storage_size_gb >= 0),
  check (number_of_disk >= 0),
  check (sustainable_performance_gflops >= 0),
  check (internal_net_throughput >= 0),
  check (external_net_throughput >= 0),
  check (support_placement_group in ('Y', 'N'))
);

create unique index idx_instance_type_name on instance_type(provider_id, name);
create index idx_instance_type_provider    on instance_type(provider_id);
create index idx_instance_type_family      on instance_type(family_type_id);

--create index idx_instance_type_family on instance_type(family_type_id);

--create table provider_family_type_mapping
--(  
--  family_type_id integer not null references instance_family_type (id),
--  provider_id integer not null references provider (id),   
--  instance_type_id integer not null references instance_type (id),  
--  primary key(provider_id, instance_type_id, family_type_id)
--);

create table instance_type_cost
(
  id IDENTITY not null primary key,
  instance_type_id integer not null references instance_type (id),
  region_provider_id integer not null references region_provider (id),
  unit_time_id integer not null references unit_time (id),
  minimal_time integer not null,  
  cost_per_unit_time DECIMAL(10,6) not null,   
  start_in timestamp not null,
  finish_in timestamp
);

create unique index idx_itc_k on instance_type_cost(instance_type_id, region_provider_id);

create table instance_state_type 
(
  id integer  not null primary key,
  name varchar(30) not null
);

create unique index idx_instance_state_type_name on instance_state_type (name);

create table user
(
  id IDENTITY  not null primary key,
  username varchar(40) not null,
  passwd varchar(32) not null
);

create unique index idx_user_username on user (username);

create table user_key
(
  id IDENTITY  not null  primary key,
  user_id integer  not null references user(id),
  name varchar(100) not null,
  public_key_material text,
  private_key_material text,  
);

create unique index idx_user_key_name on user_key (user_id,name);
create index idx_user_key_user on user_key (user_id);

create table user_provider_credential
(
  id IDENTITY not null primary key,
  user_id integer not null references user(id),
  provider_id integer not null references provider(id),
  access_identity text not null,
  access_credential text not null,
  project_name text
);

create index idx_user_provider_credential_u on user_provider_credential (user_id);
create index idx_user_provider_credential_p on user_provider_credential (provider_id);


create table disk_type
(
  id IDENTITY not null primary key,
  provider_id integer not null references provider (id),
  name varchar(40) not null,
  min_size_gb integer not null,
  max_size_gb integer not null,
  min_iops integer,
  max_iops integer,
  check(min_size_gb > 0 and min_size_gb <= max_size_gb),
  check(max_size_gb > 0 and max_size_gb >= min_size_gb)
);

create index idx_disk_type on disk_type (provider_id);


create table disk
(
  id IDENTITY not null primary key,
  type_id integer not null references disk_type (id),
  zone_id integer not null references zone (id),  
  owner_id integer not null references user (id),
  name varchar(40) not null,
  size_gb integer not null,
  iops integer not null,
  created_in timestamp not null,
  deleted_in timestamp,
  check (size_gb > 0)   
);

create index idx_disk_type_id on disk (type_id);
create index idx_disk_zone on disk (zone_id);
create index idx_disk_owner on disk (owner_id);
create unique index idx_disk_name on disk (name);

create table disk_type_cost
(
  id IDENTITY not null primary key,
  region_provider_id integer not null references region_provider (id),
  unit_time_id integer not null references unit_time (id),
  disk_type_id integer not null references disk_type (id),
  cost_storage_gb decimal(10,6) not null default 0,
  cost_iops decimal (10,6) not null default 0,
  cost_million_request decimal(10,6) not null default 0,
  start_in timestamp not null,
  finish_in timestamp,
  check (cost_storage_gb >= 0),
  check (cost_iops >= 0),
  check (cost_million_request >= 0)
);

create index idx_disk_cost_region_provider on disk_type_cost (region_provider_id);
create index idx_disk_cost_unit_time on disk_type_cost (unit_time_id);
create index idx_disk_type_disk on disk_type_cost (disk_type_id);
create unique index uk_disk_type on disk_type_cost (region_provider_id, disk_type_id, start_in);


create table instance
(
  id IDENTITY  not null primary key,
  instance_type_id integer  not null references instance_type (id),
  zone_id integer not null references zone (id),
  owner_id integer  not null references user (id),
  name varchar(60) not null,  
  image_id varchar(100) not null,
  group_name varchar(60),
  public_ip varchar(15),
  private_ip varchar(15),
  public_dns varchar(200),
  launch_time timestamp,
  platform varchar(7) not null,
  platform_username varchar(30) not null,
  keyname varchar(100) not null,
  user_data text
);

create index idx_instance_instance_type_id on instance (instance_type_id);
create unique index idx_instance_owner on instance (owner_id, name);
create unique index idx_instance_name on instance (name);
create index idx_instance_owner_id on instance (owner_id);
create index idx_instance_zone on instance (zone_id);

create table instance_state_history
(
  id IDENTITY  not null  primary key,
  instance_id integer  not null references instance (id),
  instance_state_type_id integer  not null references instance_state_type (id),
  state_time timestamp not null,
  description text
);

create index idx_inst_hist_state_instance_id on instance_state_history (instance_id);
create index idx_inst_hist_state_type_id on instance_state_history (instance_state_type_id);
create unique index idx_inst_hist_u on instance_state_history (instance_id, instance_state_type_id, state_time);

create table instance_disk
(
  id IDENTITY not null primary key,
  instance_id integer not null references instance (id),
  disk_id integer not null references disk (id),
  device_name varchar(30) not null,
  is_boot char(1) not null default 'N',
  check (is_boot in ('Y', 'N'))
);

create unique index idx_instance_disk_uk on instance_disk(instance_id, disk_id);

create table instance_tag
(
  id IDENTITY not null primary key,
  instance_id integer not null references instance (id),
  tag_name varchar(63) not null,
  tag_value varchar(255) not null
);

create index idx_instance_tag_inst_id on instance_tag (instance_id);
create index idx_instance_tag_name on instance_tag (tag_name);

create table script_statement
(
  id IDENTITY not null primary key,
  name varchar(60) not null,
  platform varchar(7) not null,
  statement text not null, 
  parents varchar(100),
  active char(1) not null,
  created_in timestamp not null default current_timestamp,
  check (active in ('Y', 'N'))
);

create unique index idx_script_statement_name on script_statement (name, platform);

-- INSERT INTO instance_configuration_script (statement, parents, state) values ()  

--instance_executed_statement


create table application_execution_description 
(
   id IDENTITY not null primary key,
   user_id integer not null references user (id),
   application_id integer not null references script_statement (id),
   failure_action_id integer not null,
   name varchar(60) not null, 
   resource_name varchar(30) not null, 
   number_of_execution integer not null,
   created_in timestamp not null,
   check (number_of_execution > 0)
);

create index idx_app_exec_desc_user_id on application_execution_description (user_id);
create index idx_app_exec_desc_app_id on application_execution_description (application_id);

create table application_execution_result
(
  id IDENTITY not null primary key,
  instance_id integer not null references instance(id),
  app_description_id integer not null references application_execution_description (id),
  started_in  timestamp not null,
  finished_in timestamp,
  exit_code integer,
  statement_output text,
  statement_error text  
);

--create unique index idx_uk_appl_exec_state_inst on application_execution_history (instance_id, statement_id);
create index        idx_appl_exec_state_inst    on application_execution_result (instance_id);
create index        idx_appl_exec_state_app     on application_execution_result (app_description_id);

-- INSERT INTO instance_executed_statement (id_instance, id_statement, started_in, finished_in, exit_code) VALUES ()

-- SELECT s.id as statement_id, s.statement as statement_script, s.state as statement_state, s.parent as statement_parents
-- FROM script_statement s WHERE state = 'Y' 
-- ORDER BY id

--SELECT s.id as statement_id, s.statement as statement_script, s.state as statement_state, s.parent as statement_parents
--FROM script_statement s
--wHERE state = 'Y' AND s.id not in (SELECT id_statement FROM instance_executed_statement WHERE id_instance = :instanceId);

--SELECT s.id as statement_id, s.statement as statement_script, s.state as statement_state, s.parent as statement_parents
--       ise.id as instance_statement_id, ise.instance_id, ise.started_in, ise.finished_in, ise.exit_code, ise.statement_output, ise.statement_error
--FROM instance_executed_statement ise
--  JOIN script_statement s ON s.id = ise.statement_id



---------------------------------------------------------------------
---                     Spot instance offer                       ---
---------------------------------------------------------------------
create table spot_instance_offer
(
  id IDENTITY not null primary key,
  instance_type_id integer not null references instance_type (id),  
  provider_id integer not null references provider (id),
  region_id integer not null references region (id),
  owner_id integer not null references user (id),
  instance_image_id varchar(50) not null, 
  user_keyname varchar(40) not null,
  offer_price decimal(10,6) not null,  
  offer_from timestamp,
  offer_until timestamp,
  offer_type_id integer not null,
  number_instances integer not null,  
  create_time timestamp not null,  
  check (number_instances > 0),
  check (offer_type_id in (1,2)),
  check (offer_price >= 0)
);

create index idx_spot_req_own on spot_instance_offer (owner_id);
create index idx_spot_req_region on spot_instance_offer (region_id);
create index idx_spot_req_provider on spot_instance_offer (provider_id);

create table spot_instance_offer_status
(
  id IDENTITY not null primary key,
  spot_instance_offer_id integer not null references spot_instance_offer (id),
  instance_id integer references instance (id),
  spot_offer_state_id integer not null, 
  spot_offer_id varchar(30),
  offer_status_code varchar(100),
  status_time timestamp not null,
  status_message text
);

create index idx_spot_req_st_s on spot_instance_offer_status (spot_instance_offer_id);
create index idx_spot_req_st_i on spot_instance_offer_status (instance_id);

create table workflow
(
  id IDENTITY  not null  primary key,
  user_id integer  not null references user(id),
  start_activity_id integer,   
  created_in datetime not null, 
  finished_in datetime,
  name varchar(150) not null
);

create index idx_workflow_user_id on workflow(user_id);

create table workflow_activity
(
  id IDENTITY  not null primary key,
  activity_id integer not null,  
  workflow_id integer  not null references workflow(id),
  label varchar(150) not null,  
  type varchar(200) not null,    
  parents varchar(60)
);

create index idx_workflow_activity_workflow_id on workflow_activity (workflow_id);
create unique index idx_workflow_activity_act_id on workflow_activity(workflow_id, activity_id);

create table workflow_activity_state_history
(
  id IDENTITY  not null primary key,
  workflow_activity_id integer not null references workflow_activity(id),
  state varchar(150) not null,
  state_time timestamp not null,
  message text
);

create index idx_workflow_activity_state on workflow_activity_state_history (workflow_activity_id);

create table workflow_activity_task
(
  id IDENTITY not null primary key,
  workflow_activity_id integer not null references workflow_activity(id),  
  executable text,
  type text  
);

create index idx_workflow_activity_task_act on workflow_activity_task (workflow_activity_id);

-- INSERT INTO workflow_activity_task (workflow_activity_id, command, type) VALUES ()

create table workflow_activity_task_data
(
   id IDENTITY not null primary key,
   activity_task_id integer not null references workflow_activity_task(id),     
   type char(1) not null,
   is_dynamic char(1) not null default 'N',
   name varchar(20) not null,
   path varchar(200) not null,
   size_gb numeric (8,6) not null,
   is_splittable char(1) default 'N' not null,
   description text,
   check (type in ('I', 'O')),
   check (is_dynamic in ('Y', 'N')),
   check (size_gb >= 0),
   check (is_splittable in ('Y', 'N'))   
);

create index idx_workflow_data_task_id on workflow_activity_task_data (activity_task_id);
create unique index idx_u_workflow_data_name on workflow_activity_task_data (activity_task_id, name);


create table workflow_activity_task_state
(
   id IDENTITY not null primary key,
   activity_task_id integer not null references workflow_activity_task(id),
   node_id integer not null references instance (id),
   state integer not null,
   state_time timestamp not null,
   state_message text
);

create index idx_task_state_activity on workflow_activity_task_state (activity_task_id);
create index idx_task_state_node on workflow_activity_task_state (node_id);

create table service
(
  id IDENTITY  not null  primary key,
  name varchar(100) not null,
  uri varchar(200) not null,
  protocol varchar(10) not null,
  media_type varchar(120) not null
);

create index idx_service_name on service (name);

create table deployment 
(
  id IDENTITY  not null  primary key,
  user_id integer not null references user (id),
  username varchar(40) not null,
  workflow_id integer references workflow (id), 
  status varchar(10) not null, 
  status_time datetime not null, 
  description varchar(200), 
  data text
);

create index idx_deployment_user_id on deployment (user_id);
create index idx_deployment_workflow_id on deployment (workflow_id);
create index idx_deployment_username on deployment (username);


create table jid_account 
(
 id IDENTITY not null primary key, 
 owner_id integer not null references user (id), 
 jid varchar(200) not null, 
 domain varchar(180) not null, 
 passwd varchar(32) not null, 
 name varchar(160),
 status char(1) not null default 'A',
 created_in timestamp default current_timestamp,
 date_status timestamp not null,
 resource text,
 attributes text,
 check (status in ('A', 'I'))
);

create index idx_jid_account_user_id on jid_account (owner_id);
create unique index idx_jid_account_jid on jid_account (jid);

create table job
(
  id IDENTITY not null primary key,
  user_id integer not null references user (id),
  uuid varchar(100) not null,
  name varchar(100) not null,
  description text not null,
  created_in bigint not null,
  finished_in bigint
);

create unique index idx_job_uuid on job(uuid);
create index idx_job_user_id on job(user_id);

create table task
(
  id IDENTITY not null primary key,
  job_id integer not null references job (id),
  uuid varchar(100) not null,
  name varchar(50) not null,
  commandline text not null
);

create unique index idx_task_uuid on task(uuid);
create index idx_task_job_id on task(job_id);

create table task_status_type 
(
  id integer not null primary key,
  name varchar(15) not null
);

create unique index idx_task_status_type_name on task_status_type (name);

create table task_status
(
  id IDENTITY not null primary key,  
  task_id integer not null references task(id),
  task_status_type_id integer not null references task_status_type(id),
  status_time timestamp not null default current_timestamp,
  worker_id varchar(60),
  pid integer
);

create index idx_task_status_type on task_status(task_status_type_id);
create index idx_task_status_task on task_status(task_id);

create table task_output_type 
(
  id integer not null primary key,
  name varchar(6) not null
);

create index task_output_type_name on task_output_type(name);

create table task_output
(
  id IDENTITY not null primary key,  
  task_id integer not null references task(id),
  uuid varchar(100) not null,
  task_output_type_id integer not null references task_output_type(id),
  value text
);

create unique index idx_task_output_uuid on task_output (uuid);

create table resource_type 
(
  id integer not null primary key,
  name varchar(6) not null
);

create unique index idx_resource_type_name on resource_type (name);

create table task_resource_usage 
(
  id IDENTITY not null primary key,
  task_id integer not null references task (id),
  resource_type_id integer not null references resource_type (id),
  pid integer not null,
  datetime timestamp not null,
  value numeric(8,6) not null
);

create index idx_tk_resource_usage_tk on task_resource_usage (task_id);
create index idx_tk_resource_usage_rt on task_resource_usage (resource_type_id);
create unique index idx_tk_resource_usage_uq on task_resource_usage (task_id, resource_type_id, pid, datetime);

create table metric_type 
(
  id integer not null primary key,
  name varchar(30) not null
);

create table metric 
(
  id IDENTITY not null primary key,
  type_id integer not null references metric_type (id),
  name varchar(60) not null
);

create unique index idx_metric_name on metric(name);
create index idx_metric_type on metric (type_id);

create table metric_unit
(
  id IDENTITY not null primary key,
  name varchar(30) not null,
  symbol varchar(6) not null
);

create unique index idx_metric_unit_name on metric_unit (name);

--create view vw_instance as
--select 
--   i.id as instance_id, instance_type_id, it.name as instance_type_name, provider_id, p.name as provider_name, owner_id, 
--   i.name, image_id, public_ip, private_ip,public_dns, launch_time, platform,platform_username, keyname
--from instance i
--  join instance_type it on it.id = i.instance_type_id
--  join provider p on p.id = it.provider_id
--  join user u on u.id = owner_id;

------------------------------------ 
---            Views             ---
------------------------------------

create or replace view vw_instance as
SELECT
  i.id as instance_id, i.instance_type_id, it.name as instance_type_name, i.name, i.image_id, i.public_ip, i.private_ip, i.public_dns,
  i.launch_time, i.platform, i.platform_username, i.keyname, i.owner_id, i.zone_id, i.group_name, i.user_data,
  it.number_compute_units, it.number_compute_units_core, it.memory_size_gb, it.storage_size_gb, it.number_of_disk as number_of_disk,     
  it.ub_number_instance, it.generation_number as instance_type_generation, it.sustainable_performance_gflops, 
  it.internal_net_throughput, it.external_net_throughput, it.support_placement_group, it.required_virtualization_type_id,  
  ft.name as family_type_name, ft.id as family_type_id, 
  r.id as region_id, r.name as region_name, r.endpoint as region_endpoint, r.city_name as region_city_name,
  z.name as zone_name, r.geographic_region_id, gr.name as geographic_region_name,
  p.id as provider_id, p.name as provider_name, p.class_name as provider_class_name,
  itc.cost_per_unit_time,
  (SELECT isd.instance_state_type_id FROM instance_state_history isd where state_time =
	  (SELECT max(state_time) FROM instance_state_history st WHERE st.instance_id = i.id) and isd.instance_id = i.id) as instance_state_type_id,
  (SELECT isd.state_time FROM instance_state_history isd where state_time = 
	  (SELECT max(state_time) FROM instance_state_history st WHERE st.instance_id = i.id) and isd.instance_id = i.id) as instance_state_time  
FROM instance i 
  join instance_type it                 on it.id = i.instance_type_id
  join provider p                       on p.id = it.provider_id
  join user u                           on u.id = i.owner_id
  join zone z on z.id = i.zone_id
  join region r 				     	on r.id = z.region_id
  join geographic_region gr             on gr.id = r.geographic_region_id
--  join provider_family_type_mapping ftm on ftm.instance_type_id = i.instance_type_id AND ftm.provider_id = it.provider_id
  join instance_family_type ft          on ft.id = it.family_type_id
  join region_provider rp 		     on rp.provider_id = it.provider_id AND rp.region_id = z.region_id
  join instance_type_cost itc 		on itc.region_provider_id = rp.id AND itc.instance_type_id = i.instance_type_id;  

  

--select id, instance_id, instance_state_type_id, date 
--from instance_state_history
--where date = (select max(date) from instance_hist_state ihs where ihs.instance_id = 1);


------------------------------------ 
---         Unit time            ---
------------------------------------
INSERT INTO unit_time (id, name) VALUES (1, 'Second');
INSERT INTO unit_time (id, name) VALUES (2, 'Minute');
INSERT INTO unit_time (id, name) VALUES (3, 'Hour');
INSERT INTO unit_time (id, name) VALUES (4, 'Day');
INSERT INTO unit_time (id, name) VALUES (5, 'Week');
INSERT INTO unit_time (id, name) VALUES (6, 'Month');
INSERT INTO unit_time (id, name) VALUES (7, 'Year');

------------------------------------ 
---       Task status type       ---
------------------------------------
INSERT INTO task_status_type(id, name) VALUES(1, 'PENDING');
INSERT INTO task_status_type(id, name) VALUES(2, 'RUNNING');
INSERT INTO task_status_type(id, name) VALUES(3, 'FAILED');
INSERT INTO task_status_type(id, name) VALUES(4, 'FINISHED');

------------------------------------ 
---       Task output type       ---
------------------------------------
INSERT INTO task_output_type (id, name) VALUES(1, 'SYSOUT');
INSERT INTO task_output_type (id, name) VALUES(2, 'SYSERR');
INSERT INTO task_output_type (id, name) VALUES(3, 'FILE');

------------------------------------ 
---       Resource Type          ---
------------------------------------
INSERT INTO resource_type (id, name) VALUES(1, 'CPU');
INSERT INTO resource_type (id, name) VALUES(2, 'RAM');
INSERT INTO resource_type (id, name) VALUES(3, 'IO');

------------------------------------ 
---     Geographic regions       ---
------------------------------------

insert into geographic_region (id, name) values (1, 'Africa');
insert into geographic_region (id, name) values (2, 'Asia');
insert into geographic_region (id, name) values (3, 'Central America');
insert into geographic_region (id, name) values (4, 'European');
insert into geographic_region (id, name) values (5, 'North America');
insert into geographic_region (id, name) values (6, 'South America');

------------------------------------ 
--- Instance family type         ---
------------------------------------

insert into instance_family_type values (1, 'General');
insert into instance_family_type values (2, 'Compute');
insert into instance_family_type values (3, 'GPU');
insert into instance_family_type values (4, 'Memory');
insert into instance_family_type values (5, 'Storage');
insert into instance_family_type values (6, 'Bootstrap');

  
------------------------------------ 
---    Instance state type       ---
------------------------------------

insert into instance_state_type values (1, 'PENDING');
insert into instance_state_type values (2, 'PROVISIONING');
insert into instance_state_type values (3, 'STAGING');
insert into instance_state_type values (4, 'RUNNING');
insert into instance_state_type values (5, 'SHUTTING-DOWN');
insert into instance_state_type values (6, 'STOPPING');
insert into instance_state_type values (7, 'STOPPED');
insert into instance_state_type values (8, 'TERMINATED');

------------------------------------ 
---      Cloud providers         ---
------------------------------------

insert into provider (id, name, class_name, ub_instances_per_type) values (1, 'amazon', 'org.excalibur.service.aws.ec2.EC2', 20);
insert into provider (id, name, class_name, ub_instances_per_type) values (2, 'google', 'org.excalibur.driver.google.compute.GoogleCompute', 20);
insert into provider (id, name, class_name, ub_instances_per_type) values (3, 'local',  'org.excalibur.core.cloud.api.compute.local.LocalCompute', 1);