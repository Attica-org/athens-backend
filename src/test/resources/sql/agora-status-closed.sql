-- 1번 agora의 status를 CLOSED로 변경하기
UPDATE agora
SET status   = 'CLOSED',
    end_time = CURRENT_TIMESTAMP
WHERE agora_id = 1;