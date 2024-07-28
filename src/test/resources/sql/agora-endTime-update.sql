UPDATE agora
SET status   = 'CLOSED',
    end_time = DATEADD('SECOND', 30, CURRENT_TIMESTAMP())
WHERE agora_id = 1;