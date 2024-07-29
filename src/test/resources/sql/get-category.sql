set FOREIGN_KEY_CHECKS = 0;

alter table category AUTO_INCREMENT = 1;

insert into category (parent_id, level, name) values (NULL, 0, '전체');