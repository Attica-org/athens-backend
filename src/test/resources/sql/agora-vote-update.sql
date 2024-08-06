-- 1번 agora의 prosCount와 consCount를 update
UPDATE agora_member
SET vote_type = 'PROS'
WHERE agora_id = 1
  AND agora_member_id = 1;

UPDATE agora_member
SET vote_type = 'CONS'
WHERE agora_id = 1
  AND agora_member_id = 2;

UPDATE agora_member
SET vote_type = 'PROS'
WHERE agora_id = 1
  AND agora_member_id = 3;