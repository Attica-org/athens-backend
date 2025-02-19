package com.attica.athens.global.handler.MessageHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;

@RequiredArgsConstructor
public class DefaultMessageProcessor implements MessageProcessor {
    private final Message<?> message;

    @Override
    public Message<?> process() {
        return message;
    }
}
