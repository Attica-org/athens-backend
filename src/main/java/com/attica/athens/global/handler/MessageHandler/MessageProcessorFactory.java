package com.attica.athens.global.handler.MessageHandler;

import com.attica.athens.global.auth.application.AuthService;
import com.attica.athens.global.decorator.HeartBeatManager;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageProcessorFactory {
    private final AuthService authService;
    private final HeartBeatManager heartBeatManager;

    public MessageProcessor create(Message<?> message, StompHeaderAccessor accessor) {
        if (accessor.getCommand() == StompCommand.CONNECT) {
            return new ConnectMessageProcessor(message, accessor, authService);
        }

        if (isHeartbeat(message, accessor)) {
            return new HeartbeatMessageProcessor(message, accessor, heartBeatManager);
        }

        return new DefaultMessageProcessor(message);
    }

    private boolean isHeartbeat(Message<?> message, StompHeaderAccessor accessor) {
        return accessor.getCommand() == null &&
                message.getPayload() instanceof byte[] &&
                ((byte[]) message.getPayload()).length == 1 &&
                ((byte[]) message.getPayload())[0] == '\n';
    }
}
