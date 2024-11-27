package com.attica.athens.domain.vote;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.attica.athens.domain.agora.vote.application.AgoraVoteService;
import com.attica.athens.domain.agora.vote.dto.request.KickVoteRequest;
import com.attica.athens.domain.agora.vote.dto.response.SendKickResponse;
import com.attica.athens.domain.chat.domain.ChatType;
import com.attica.athens.support.WebSocketIntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AgoraVoteWebSocketApiIntegerationTest extends WebSocketIntegrationTestSupport {

    @Autowired
    private AgoraVoteService agoraVoteService;

    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;


    @Test
    @Sql(scripts = {
            "/sql/get-category.sql",
            "/sql/get-agora.sql",
            "/sql/get-base-member.sql"
    })
    @DisplayName("추방 투표 및 성공 메시지 확인")
    void 성공_추방투표_및_메시지전송() {
        // given
        Long agoraId = 1L;
        Long targetMemberId = 4L;
        String expectedDestination = "/topic/agoras/" + agoraId;
        KickVoteRequest request = new KickVoteRequest(targetMemberId, 3);

        // when
        agoraVoteService.kickVote(agoraId, 1L,request);
        agoraVoteService.kickVote(agoraId, 3L, request);

        // then
        ArgumentCaptor<String> destination = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SendKickResponse> message = ArgumentCaptor.forClass(SendKickResponse.class);

        verify(simpMessagingTemplate, times(1)).convertAndSend(destination.capture(), message.capture());
        SendKickResponse sendKickResponse = message.getValue();

        assertAll("Kick vote message verification",
                () -> assertEquals(expectedDestination, destination.getValue()),
                () -> assertEquals(ChatType.KICK, sendKickResponse.type()),
                () -> assertEquals(targetMemberId, sendKickResponse.kickVoteInfo().targetMemberId()),
                () -> assertEquals("사용자를 추방합니다. memberId: " + targetMemberId, sendKickResponse.kickVoteInfo().message())
        );
    }
}
