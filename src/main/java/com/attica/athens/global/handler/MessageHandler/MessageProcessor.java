package com.attica.athens.global.handler.MessageHandler;

import org.springframework.messaging.Message;

public interface MessageProcessor {
    Message<?> process();
}
