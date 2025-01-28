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
        // 새로운 구조에서는 MessageProcessorFactory를 통해 프로세서를 생성합니다
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
            // 하트비트 메시지 생성
            SimpMessageHeaderAccessor accessor = StompHeaderAccessor.createForHeartbeat();
            accessor.setSessionId("test-session-id");
            byte[] payload = new byte[]{'\n'};
            Message<?> message = MessageBuilder.createMessage(payload, accessor.getMessageHeaders());

            // when
            // 메시지 처리를 수행합니다
            interceptor.preSend(message, null);

            // then
            // 하트비트 매니저가 적절히 호출되었는지 확인합니다
            verify(heartBeatManager, times(1)).handleHeartbeat("test-session-id");
        }

        @Test
        @DisplayName("알맞지 않은 accessor command 하트비트 핸들링 테스트")
        void 실패_하트비트인터셉트_메세지전송직전() {
            // given
            // 일반 SEND 메시지 생성 (하트비트가 아님)
            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
            accessor.setSessionId("test-session-id");
            Message<?> message = MessageBuilder.createMessage("Some payload", accessor.getMessageHeaders());

            // when
            // 메시지 처리를 수행합니다
            interceptor.preSend(message, null);

            // then
            // 하트비트 처리가 호출되지 않았는지 확인합니다
            verify(heartBeatManager, never()).handleHeartbeat(anyString());
        }

        @Test
        @DisplayName("널 accessor 테스트")
        void 실패_하트비트인터셉트_accessor_null아닌값() {
            // given
            // null accessor 상황을 시뮬레이션합니다
            when(MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class)).thenReturn(null);

            // when
            // 메시지 처리를 수행합니다
            interceptor.preSend(message, channel);

            // then
            // 하트비트 처리가 호출되지 않았는지 확인합니다
            verify(heartBeatManager, never()).handleHeartbeat(anyString());
        }
    }
}
