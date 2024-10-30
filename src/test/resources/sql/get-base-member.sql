alter table base_member alter column member_id restart with 1;

-- insert into base_member (created_at, created_by, modified_at, modified_by, role, member_uuid)
-- values (CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system', 'ROLE_USER', RANDOM_UUID());
--
-- insert into base_member (created_at, created_by, modified_at, modified_by, role, member_uuid)
-- values (CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system', 'ROLE_USER', RANDOM_UUID());
--
-- insert into base_member (created_at, created_by, modified_at, modified_by, role, member_uuid)
-- values (CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system', 'ROLE_USER', RANDOM_UUID());
--
-- insert into member (member_id, username, password)
-- values (1, 'EnvironmentalActivist', 'password');
--
-- insert into member (member_id, username, password)
-- values (2, 'PolicyExpert', 'password');
--
-- insert into member (member_id, username, password)
-- values (3, 'TeacherUnion', 'password');

insert into base_member (created_at, created_by, modified_at, modified_by, role, member_uuid)
values (CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system', 'ROLE_USER', RANDOM_UUID());

insert into base_member (created_at, created_by, modified_at, modified_by, role, member_uuid)
values (CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system', 'ROLE_USER', RANDOM_UUID());

insert into base_member (created_at, created_by, modified_at, modified_by, role, member_uuid)
values (CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system', 'ROLE_USER', RANDOM_UUID());

insert into member (member_id, username, password, auth_provider)
values (1, 'EnvironmentalActivist', 'password', 'LOCAL');

insert into member (member_id, username, password, auth_provider)
values (2, 'PolicyExpert', 'password', 'LOCAL');

insert into member (member_id, username, password, auth_provider)
values (4, 'TeacherUnion', 'password', 'LOCAL');

insert into agora_member (type, vote_type, nickname, session_id, photo_number,
                          end_voted, is_opinion_voted, disconnect_type, socket_disconnect_time, agora_id, member_id,
                          created_at, modified_at, created_by, modified_by
) values (
             'PROS', 'DEFAULT', 'EnvironmentalActivist', 'session123', 1,
             false, false, false, '2023-11-01 12:00:00', 1, 1,
             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1', '1'
         );

insert into agora_member (type, vote_type, nickname, session_id, photo_number,
                          end_voted, is_opinion_voted, disconnect_type, socket_disconnect_time, agora_id, member_id,
                          created_at, modified_at, created_by, modified_by
) values (
             'PROS', 'DEFAULT', 'PolicyExpert', 'session123', 1,
             false, false, false, '2023-11-01 12:00:00', 1, 2,
             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '2', '2'
         );

insert into agora_member (type, vote_type, nickname, session_id, photo_number,
                          end_voted, is_opinion_voted, disconnect_type, socket_disconnect_time, agora_id, member_id,
                          created_at, modified_at, created_by, modified_by
) values (
             'PROS', 'DEFAULT', 'TeacherUnion', 'session123', 1,
             false, false, false, '2023-11-01 12:00:00', 1, 3,
             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '3', '3'
         );
