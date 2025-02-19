package com.attica.athens.global.WebSocket.HeartBeat;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.attica.athens.global.auth.application.AuthService;
import com.attica.athens.global.decorator.HeartBeatManager;
import com.attica.athens.global.handler.MessageHandler.MessageProcessorFactory;
import com.attica.athens.global.interceptor.JwtChannelInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;

@DisplayName("하트비트를 이용한 세션 관리 테스트")
public class HeartBeatInterceptorTest {
    @Mock
    private AuthService authService;

    @Mock
    private HeartBeatManager heartBeatManager;

    @Mock
    private Message<?> message;

    @Mock
    private MessageChannel channel;

    private MessageProcessorFactory messageProcessorFactory;
    private JwtChannelInterceptor interceptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        messageProcessorFactory = new MessageProcessorFactory(authService, heartBeatManager);
        interceptor = new JwtChannelInterceptor(messageProcessorFactory);
    }

    @Nested
    @DisplayName("하트비트 인터셉트 테스트")
    class InterceptorTest {

        @Test
        @DisplayName("알맞은 accessor command 하트비트 핸들링 테스트")
        void 성공_하트비트인터셉트_메세지전송직전() {
            // given
            SimpMessageHeaderAccessor accessor = StompHeaderAccessor.createForHeartbeat();
            accessor.setSessionId("test-session-id");
            byte[] payload = new byte[]{'\n'};
            Message<?> message = MessageBuilder.createMessage(payload, accessor.getMessageHeaders());

            // when
            interceptor.preSend(message, null);

            // then
            verify(heartBeatManager, times(1)).handleHeartbeat("test-session-id");
        }

        @Test
        @DisplayName("알맞지 않은 accessor command 하트비트 핸들링 테스트")
        void 실패_하트비트인터셉트_메세지전송직전() {
            // given
            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
            accessor.setSessionId("test-session-id");
            Message<?> message = MessageBuilder.createMessage("Some payload", accessor.getMessageHeaders());

            // when
            interceptor.preSend(message, null);

            // then
            verify(heartBeatManager, never()).handleHeartbeat(anyString());
        }

        @Test
        @DisplayName("널 accessor 테스트")
        void 실패_하트비트인터셉트_accessor_null아닌값() {
            // given
            when(MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class)).thenReturn(null);

            // when
            interceptor.preSend(message, channel);

            // then
            verify(heartBeatManager, never()).handleHeartbeat(anyString());
        }
    }
}
