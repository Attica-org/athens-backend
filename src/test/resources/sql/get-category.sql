delete from category;
alter table category AUTO_INCREMENT = 1;

insert into category (parent_id, level, name) values (NULL, 0, '전체');