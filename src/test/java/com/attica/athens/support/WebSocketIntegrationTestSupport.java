package com.attica.athens.support;

import com.attica.athens.config.TestSecurityConfig.TestCustomUserDetailsServiceConfig;
import com.attica.athens.global.auth.CustomUserDetails;
import com.attica.athens.global.auth.jwt.JwtUtils;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class WebSocketIntegrationTestSupport extends IntegrationTestSupport {

    @Autowired
    private TestCustomUserDetailsServiceConfig testCustomUserDetailsServiceConfig;

    @Autowired
    private JwtUtils jwtUtils;

    @LocalServerPort
    protected int port;

    protected WebSocketStompClient stompClient;

    private final String WEBSOCKET_URI = "ws://localhost:{port}/ws";

    protected void setupStompClient() {
        // TODO: 후에 SockJs 지원하도록 테스트 수정
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        this.stompClient = new WebSocketStompClient(webSocketClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    protected StompSession connectAndSubscribe(final String topic, final String username, final Long agoraId,
                                               final CompletableFuture<String> resultFuture, Class<?> payloadType
    ) throws Exception {
        StompSession session = connectToWebSocket(username, agoraId);
        subscribeToTopic(topic, session, resultFuture, payloadType);
        return session;
    }

    private StompSession connectToWebSocket(String username, Long agoraId)
            throws Exception {
        CustomUserDetails userDetails = (CustomUserDetails) testCustomUserDetailsServiceConfig.testCustomUserDetailsService()
                .loadUserByUsername(username);
        String token = jwtUtils.createJwtToken(username, userDetails.getUserId(),
                userDetails.getAuthorities().iterator().next().getAuthority());

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);
        connectHeaders.add("AgoraId", agoraId.toString());

        return stompClient.connect(
                WEBSOCKET_URI.replace("{port}", String.valueOf(port)),
                new WebSocketHttpHeaders(),
                connectHeaders,
                new StompSessionHandlerAdapter() {
                }
        ).get(5, TimeUnit.SECONDS);
    }

    private <T> void subscribeToTopic(final String topic, final StompSession session,
                                      CompletableFuture<String> resultFuture, Class<T> payloadType) {
        session.subscribe(topic,
                createStompFrameHandler(payloadType, resultFuture));
    }

    private <T> StompFrameHandler createStompFrameHandler(Class<T> payloadType,
                                                          CompletableFuture<String> resultFuture) {
        return new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return payloadType;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    T typedPayload = payloadType.cast(payload);
                    resultFuture.complete(typedPayload.toString());
                } catch (ClassCastException e) {
                    resultFuture.completeExceptionally(e);
                }
            }
        };
    }
}
