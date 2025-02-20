package com.attica.athens.global.interceptor;

import com.attica.athens.global.handler.MessageHandler.MessageProcessor;
import com.attica.athens.global.handler.MessageHandler.MessageProcessorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final MessageProcessorFactory messageProcessorFactory;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            log.warn("StompHeaderAccessor is null");
            return message;
        }

        log.debug("Processing message: command={}, sessionId={}",
                accessor.getCommand(), accessor.getSessionId());

        try {
            MessageProcessor processor = messageProcessorFactory.create(message, accessor);
            return processor.process();
        } catch (Exception e) {
            log.error("Error processing message", e);
            return message;
        }
    }
}
