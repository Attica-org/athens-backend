UPDATE refresh_token
SET expiration = DATEADD('DAY', -2, CURRENT_TIMESTAMP())
WHERE user_id = 10;