package com.attica.athens.global.handler.MessageHandler;

import com.attica.athens.global.decorator.HeartBeatManager;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

@RequiredArgsConstructor
public class HeartbeatMessageProcessor implements MessageProcessor {
    private final Message<?> message;
    private final StompHeaderAccessor accessor;
    private final HeartBeatManager heartBeatManager;

    @Override
    public Message<?> process() {
        String sessionId = accessor.getSessionId();
        heartBeatManager.handleHeartbeat(sessionId);
        return message;
    }
}
