alter table base_member alter column member_id restart with 1;

insert into base_member (created_at, created_by, modified_at, modified_by, role, member_uuid)
values (CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system', 'ROLE_USER', RANDOM_UUID());

insert into base_member (created_at, created_by, modified_at, modified_by, role, member_uuid)
values (CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system', 'ROLE_USER', RANDOM_UUID());

insert into base_member (created_at, created_by, modified_at, modified_by, role, member_uuid)
values (CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system', 'ROLE_USER', RANDOM_UUID());

insert into member (member_id, username, password)
values (1, 'EnvironmentalActivist', 'password');

insert into member (member_id, username, password)
values (2, 'PolicyExpert', 'password');

insert into member (member_id, username, password)
values (3, 'TeacherUnion', 'password');