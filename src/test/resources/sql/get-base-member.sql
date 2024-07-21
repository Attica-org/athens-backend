delete from member;
delete from base_member;
alter table base_member AUTO_INCREMENT = 1;

insert into base_member (created_at, created_by, modified_at, modified_by, role, member_uuid)
values (now(), 'system', now(), 'system', 'ROLE_USER', UNHEX(REPLACE(UUID(), '-', '')));

insert into base_member (created_at, created_by, modified_at, modified_by, role, member_uuid)
values (now(), 'system', now(), 'system', 'ROLE_USER', UNHEX(REPLACE(UUID(), '-', '')));

insert into base_member (created_at, created_by, modified_at, modified_by, role, member_uuid)
values (now(), 'system', now(), 'system', 'ROLE_USER', UNHEX(REPLACE(UUID(), '-', '')));

insert into member (member_id, username, password)
values (1, 'EnvironmentalActivist', 'password');

insert into member (member_id, username, password)
values (2, 'PolicyExpert', 'password');

insert into member (member_id, username, password)
values (3, 'TeacherUnion', 'password');
