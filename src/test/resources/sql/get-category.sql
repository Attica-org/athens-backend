set REFERENTIAL_INTEGRITY false;

alter table category alter column category_id restart with 1;

insert into category (parent_id, level, name) values (NULL, 0, '전체')