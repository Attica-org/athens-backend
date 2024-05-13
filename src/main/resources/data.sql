use athens;

-- category 테이블 데이터
INSERT INTO category (category_id, level, name, parent_id)
VALUES (1, 1, '사회 이슈', NULL),
       (2, 2, '환경', 1),
       (3, 3, '기후 변화', 2),
       (4, 3, '자원 보존', 2),
       (5, 2, '교육', 1),
       (6, 3, '교육 개혁', 5),
       (7, 3, '학생 권리', 5),
       (8, 2, '경제', 1),
       (9, 3, '경제 정책', 8),
       (10, 3, '고용', 8);

-- agora 테이블 데이터
INSERT INTO agora (capacity, duration, view_count, agora_id, code, created_at, modified_at, title, created_by,
                   modified_by, status)
VALUES (100, 120.0, 5000, 1, 3, NOW(), NOW(), '기후 변화 대책에 대한 토론', 'system', 'system', 'RUNNING'),
       (50, 90.0, 2500, 2, 6, NOW(), NOW(), '교육 개혁 방안 토론', 'system', 'system', 'RUNNING'),
       (80, 180.0, 3800, 3, 9, NOW(), NOW(), '경제 정책 토론', 'system', 'system', 'CLOSED'),
       (60, 120.0, 1200, 4, 1, NOW(), NOW(), '사회 안전망 강화 방안 토론', 'system', 'system', 'RUNNING'),
       (70, 150.0, 4000, 5, 2, NOW(), NOW(), '에너지 정책 토론', 'system', 'system', 'QUEUED');

-- base_user 테이블 데이터
INSERT INTO base_user (created_at, modified_at, user_id, user_uuid, created_by, modified_by, role)
VALUES (NOW(), NOW(), 1, UNHEX(REPLACE(UUID(), '-', '')), 'system', 'system', 'ROLE_USER'),
       (NOW(), NOW(), 2, UNHEX(REPLACE(UUID(), '-', '')), 'system', 'system', 'ROLE_USER'),
       (NOW(), NOW(), 3, UNHEX(REPLACE(UUID(), '-', '')), 'system', 'system', 'ROLE_USER'),
       (NOW(), NOW(), 4, UNHEX(REPLACE(UUID(), '-', '')), 'system', 'system', 'ROLE_USER'),
       (NOW(), NOW(), 5, UNHEX(REPLACE(UUID(), '-', '')), 'system', 'system', 'ROLE_USER'),
       (NOW(), NOW(), 6, UNHEX(REPLACE(UUID(), '-', '')), 'system', 'system', 'ROLE_USER'),
       (NOW(), NOW(), 7, UNHEX(REPLACE(UUID(), '-', '')), 'system', 'system', 'ROLE_USER'),
       (NOW(), NOW(), 8, UNHEX(REPLACE(UUID(), '-', '')), 'system', 'system', 'ROLE_USER'),
       (NOW(), NOW(), 9, UNHEX(REPLACE(UUID(), '-', '')), 'system', 'system', 'ROLE_USER'),
       (NOW(), NOW(), 10, UNHEX(REPLACE(UUID(), '-', '')), 'system', 'system', 'ROLE_TEMP_USER'),
       (NOW(), NOW(), 11, UNHEX(REPLACE(UUID(), '-', '')), 'system', 'system', 'ROLE_TEMP_USER'),
       (NOW(), NOW(), 12, UNHEX(REPLACE(UUID(), '-', '')), 'system', 'system', 'ROLE_TEMP_USER'),
       (NOW(), NOW(), 13, UNHEX(REPLACE(UUID(), '-', '')), 'system', 'system', 'ROLE_TEMP_USER'),
       (NOW(), NOW(), 14, UNHEX(REPLACE(UUID(), '-', '')), 'system', 'system', 'ROLE_TEMP_USER'),
       (NOW(), NOW(), 15, UNHEX(REPLACE(UUID(), '-', '')), 'system', 'system', 'ROLE_TEMP_USER');

-- agora_user 테이블 데이터
INSERT INTO agora_user (photo_number, agora_id, agora_user_id, created_at, modified_at, user_id, nickname, created_by,
                        modified_by, type)
VALUES (1, 1, 1, NOW(), NOW(), 1, 'EnvironmentalActivist', 'system', 'system', 'PROS'),
       (2, 1, 2, NOW(), NOW(), 2, 'BusinessLeader', 'system', 'system', 'CONS'),
       (3, 1, 3, NOW(), NOW(), 3, 'PolicyExpert', 'system', 'system', 'OBSERVER'),
       (4, 2, 4, NOW(), NOW(), 4, 'TeacherUnion', 'system', 'system', 'PROS'),
       (5, 2, 5, NOW(), NOW(), 5, 'ParentGroup', 'system', 'system', 'CONS'),
       (6, 2, 6, NOW(), NOW(), 6, 'EducationResearcher', 'system', 'system', 'OBSERVER'),
       (7, 3, 7, NOW(), NOW(), 7, 'EconomistPro', 'system', 'system', 'PROS'),
       (8, 3, 8, NOW(), NOW(), 8, 'SmallBusinessOwner', 'system', 'system', 'CONS'),
       (9, 3, 9, NOW(), NOW(), 9, 'PolicyAnalyst', 'system', 'system', 'OBSERVER'),
       (10, 4, 10, NOW(), NOW(), 10, 'SocialActivist', 'system', 'system', 'PROS'),
       (11, 4, 11, NOW(), NOW(), 11, 'PolicyMaker', 'system', 'system', 'CONS'),
       (12, 4, 12, NOW(), NOW(), 12, 'ResearchFellow', 'system', 'system', 'OBSERVER'),
       (13, 5, 13, NOW(), NOW(), 13, 'EnvironmentExpert', 'system', 'system', 'PROS'),
       (14, 5, 14, NOW(), NOW(), 14, 'IndustryLeader', 'system', 'system', 'CONS'),
       (15, 5, 15, NOW(), NOW(), 15, 'EnergyAnalyst', 'system', 'system', 'OBSERVER');

-- chat 테이블 데이터
INSERT INTO chat (agora_user_id, chat_id, created_at, modified_at, created_by, modified_by, content, type)
VALUES (1, 1, NOW(), NOW(), 'EnvironmentalActivist', 'EnvironmentalActivist', '기후 변화 대책으로 화석 연료 사용을 점진적으로 중단해야 합니다.',
        'CHAT'),
       (2, 2, NOW(), NOW(), 'BusinessLeader', 'BusinessLeader', '그렇게 되면 경제에 타격이 갈 것입니다. 현실적인 대안이 필요합니다.', 'CHAT'),
       (3, 3, NOW(), NOW(), 'PolicyExpert', 'PolicyExpert', '양측의 입장을 고려하여 균형 잡힌 정책을 수립해야 할 것 같습니다.', 'CHAT'),
       (4, 4, NOW(), NOW(), 'TeacherUnion', 'TeacherUnion', '교사의 처우 개선과 교육 환경 개선을 위한 개혁이 필요합니다.', 'CHAT'),
       (5, 5, NOW(), NOW(), 'ParentGroup', 'ParentGroup', '하지만 교육비 부담도 고려해야 합니다. 무작정 예산을 늘리기는 어렵습니다.', 'CHAT'),
       (6, 6, NOW(), NOW(), 'EducationResearcher', 'EducationResearcher', '교육 격차 해소와 학생 중심 교육 방식 도입이 필요할 것 같습니다.',
        'CHAT'),
       (7, 7, NOW(), NOW(), 'EconomistPro', 'EconomistPro', '경제 활성화를 위해 규제 완화와 인센티브 제공이 필요합니다.', 'CHAT'),
       (8, 8, NOW(), NOW(), 'SmallBusinessOwner', 'SmallBusinessOwner', '하지만 중소기업 보호 대책도 함께 마련되어야 합니다.', 'CHAT'),
       (9, 9, NOW(), NOW(), 'PolicyAnalyst', 'PolicyAnalyst', '거시경제 지표와 산업 동향을 종합적으로 분석하여 정책을 수립해야 할 것 같습니다.', 'CHAT'),
       (10, 10, NOW(), NOW(), 'SocialActivist', 'SocialActivist', '사회 안전망 강화를 통해 취약 계층을 보호해야 합니다.', 'CHAT'),
       (11, 11, NOW(), NOW(), 'PolicyMaker', 'PolicyMaker', '하지만 재정 건전성도 고려해야 합니다. 지속 가능한 정책이 필요합니다.', 'CHAT'),
       (12, 12, NOW(), NOW(), 'ResearchFellow', 'ResearchFellow', '복지 정책의 효과와 비용을 면밀히 분석하여 최적의 방안을 마련해야 할 것 같습니다.',
        'CHAT'),
       (13, 13, NOW(), NOW(), 'EnvironmentExpert', 'EnvironmentExpert', '환경 보호를 위해 재생 에너지 사용을 확대해야 합니다.', 'CHAT'),
       (14, 14, NOW(), NOW(), 'IndustryLeader', 'IndustryLeader', '하지만 산업 경쟁력 유지를 위해 에너지 비용도 고려해야 합니다.', 'CHAT'),
       (15, 15, NOW(), NOW(), 'EnergyAnalyst', 'EnergyAnalyst', '에너지 수요와 공급 예측을 바탕으로 장기적인 에너지 정책을 수립해야 할 것 같습니다.',
        'CHAT');

-- user 테이블 데이터
INSERT INTO user (user_id, username, password)
VALUES (1, 'EnvironmentalActivist', '{bcrypt}$2a$10$eDsxzX6KQPbqNNdZo/4j8.ys2uQO1d4vyvLBWxpi0Q5UhLLXjw1xm'),
       (2, 'BusinessLeader', '{bcrypt}$2a$10$SKnWANgwFEPu8WuI5SUwPeRJgw2akmMlEEWiQUSyZSzrfvBnlLJA2'),
       (3, 'PolicyExpert', '{bcrypt}$2a$10$fkuiSe3dYeV8Z7PFGOZcGOd9SyEcx3rV1MmRY9rHiIlxSB4EkRw8e'),
       (4, 'TeacherUnion', '{bcrypt}$2a$10$y/rTxNnZJ5wbkrLDRtVd0uB/LZ1iyuCjhklpGQlbLdXC3ZG6j9wJa'),
       (5, 'ParentGroup', '{bcrypt}$2a$10$XDL6L6PtlgXDUoBBMxVbJO45kAUkIdO80ZjztYVSNGDMzM3OeP1DG'),
       (6, 'EducationResearcher', '{bcrypt}$2a$10$uP3tSQgQXxrQDiSFdwGF1OXLNUHrQ6ZnFnvNnHOZf.zTjnmTElNSK'),
       (7, 'EconomistPro', '{bcrypt}$2a$10$yIYgG.Jz3DyVNmkWrT7CnehOWE0/8CvXUBvfnZrBqgdpPM4y2xKd6'),
       (8, 'SmallBusinessOwner', '{bcrypt}$2a$10$fh5cZbrMVWXS8bpEULdUVu1YUwkIxOtALj3oSnGJhyHM8KKnpJQYC'),
       (9, 'PolicyAnalyst', '{bcrypt}$2a$10$oKHXwQ7I7uIWBmXfDSLZlurDy8XG9x/Y1p94.NHnMv5o1zKdP6zZS'),
       (10, 'SocialActivist', '{bcrypt}$2a$10$Ux7UQt6TlNcxDxcI8UaowedQkpIcfIpZeWVAYCrxJOaHQfqsOPnoW'),
       (11, 'PolicyMaker', '{bcrypt}$2a$10$3/yQDpLGGwMxihYgitFIEeN4nq1Y2zZe/rRKyW7O7p9FDScr0NYSu'),
       (12, 'ResearchFellow', '{bcrypt}$2a$10$zVQXbGqhZMuwEY1vfDiJSOSHMSQkKO4uXtGsE7.OQlKKDJ5qJV6/a'),
       (13, 'EnvironmentExpert', '{bcrypt}$2a$10$lLZbvuEjCO3oCYAGJuCK6OuX7hWzZjWNdoQrZnKCMWYXK8JlNmFxu'),
       (14, 'IndustryLeader', '{bcrypt}$2a$10$FUBaodMYlGvQsYGZcFLWQuCbv/aQWJQN1.wdreFspLaJMx4okUG3u'),
       (15, 'EnergyAnalyst', '{bcrypt}$2a$10$XWk/2T3JnhfBg5QXjLR5TuuH5mUm9ZpB9QVPNYTc7fCpEHhb.e/4q');
