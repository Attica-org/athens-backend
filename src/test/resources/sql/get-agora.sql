SET REFERENTIAL_INTEGRITY FALSE;

ALTER TABLE agora ALTER COLUMN agora_id RESTART WITH 6;

-- 첫 번째 데이터
insert into agora (
    capacity, cons_count, duration, end_vote_count, pros_count, view_count,
    category_id, created_at, end_time, modified_at, start_time, title,
    created_by, modified_by, color, status,
    is_deleted, deleted_at, deleted_by
) values (
             20, 0, 40, 0, 0, 0,
             1, '2024-04-01 23:14:09', '2024-04-05 00:37:32', '2023-10-03 11:23:43', '2023-09-02 18:07:20', 'Games',
             1, 1, 'Green', 'QUEUED',
             false, CURRENT_TIMESTAMP(), 'system'
         );

-- 두 번째 데이터
insert into agora (
    capacity, cons_count, duration, end_vote_count, pros_count, view_count,
    category_id, created_at, end_time, modified_at, start_time, title,
    created_by, modified_by, color, status,
    is_deleted, deleted_at, deleted_by
) values (
             17, 0, 90, 0, 0, 0,
             1, '2023-09-13 14:12:15', '2024-02-09 16:29:58', '2023-10-31 14:54:02', '2023-09-26 20:59:14', 'Tools',
             1, 1, 'Red', 'QUEUED',
             false, CURRENT_TIMESTAMP(), 'system'
         );

-- 세 번째 데이터
insert into agora (
    capacity, cons_count, duration, end_vote_count, pros_count, view_count,
    category_id, created_at, end_time, modified_at, start_time, title,
    created_by, modified_by, color, status, agora_thumbnail_id,
    is_deleted, deleted_at, deleted_by
) values (
             20, 0, 40, 0, 0, 0,
             1, '2024-04-01 23:14:09', '2024-04-05 00:37:32', '2023-10-03 11:23:43', '2023-09-02 18:07:20', 'Games',
             1, 1, 'Green', 'QUEUED', null,
             false, CURRENT_TIMESTAMP(), 'system'
         );

-- 네 번째 데이터
insert into agora (
    capacity, cons_count, duration, end_vote_count, pros_count, view_count,
    category_id, created_at, end_time, modified_at, start_time, title,
    created_by, modified_by, color, status, agora_thumbnail_id,
    is_deleted, deleted_at, deleted_by
) values (
             20, 0, 40, 0, 0, 0,
             1, '2024-04-01 23:14:09', '2024-04-05 00:37:32', '2023-10-03 11:23:43', '2023-09-02 18:07:20', 'Games',
             1, 1, 'Green', 'QUEUED', null,
             false, CURRENT_TIMESTAMP(), 'system'
         );