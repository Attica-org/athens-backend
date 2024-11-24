package com.attica.athens.support;

import com.attica.athens.config.TestSecurityConfig.TestCustomUserDetailsServiceConfig;
import com.attica.athens.global.auth.domain.CustomUserDetails;
import com.attica.athens.global.auth.jwt.JwtUtils;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
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
                                               final CompletableFuture<String> resultFuture
    ) throws Exception {
        StompSession session = connectToWebSocket(username, agoraId);
        subscribeToTopic(topic, session, resultFuture);
        return session;
    }

    private StompSession connectToWebSocket(String username, Long agoraId)
            throws Exception {
        CustomUserDetails userDetails = (CustomUserDetails) testCustomUserDetailsServiceConfig.testCustomUserDetailsService()
                .loadUserByUsername(username);
        String token = jwtUtils.createAccessToken(userDetails.getUserId(),
                userDetails.getAuthorities().iterator().next().getAuthority());

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);
        connectHeaders.add("AgoraId", agoraId.toString());

        return stompClient.connectAsync(
                WEBSOCKET_URI.replace("{port}", String.valueOf(port)),
                new WebSocketHttpHeaders(),
                connectHeaders,
                new StompSessionHandlerAdapter() {
                }
        ).get(3, TimeUnit.SECONDS);
    }

    private void subscribeToTopic(final String topic, final StompSession session,
                                  CompletableFuture<String> resultFuture) {
        session.subscribe(topic,
                createStompFrameHandler(resultFuture));
    }

    private StompFrameHandler createStompFrameHandler(CompletableFuture<String> resultFuture) {
        return new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return byte[].class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    if (payload instanceof byte[]) {
                        String strPayload = new String((byte[]) payload, StandardCharsets.UTF_8);
                        resultFuture.complete(strPayload);
                    } else {
                        resultFuture.completeExceptionally(new IllegalArgumentException("Unexpected payload type"));
                    }
                } catch (Exception e) {
                    resultFuture.completeExceptionally(e);
                }
            }
        };
    }
}
