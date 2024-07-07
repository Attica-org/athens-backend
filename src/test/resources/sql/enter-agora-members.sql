-- 1번 agora의 모든 AgoraUser들을 참여 상태로 변경하기
UPDATE agora_member
SET session_id = CONCAT('session_', agora_member_id)
WHERE agora_id = 1;
