-- 유효한 채팅 메시지 전송
-- 아고라 상태를 RUNNING으로 변경
UPDATE agora
SET status = 'RUNNING'
WHERE agora_id = 1;
