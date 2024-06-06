package com.attica.athens.domain.chat.api;

import com.attica.athens.domain.chat.application.ChatQueryService;
import com.attica.athens.domain.chat.dto.Cursor;
import com.attica.athens.domain.chat.dto.response.GetChatParticipants;
import com.attica.athens.domain.chat.dto.response.GetChatResponse;
import com.attica.athens.domain.common.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/open")
public class ChatOpenController {

    private final ChatQueryService chatQueryService;

    @GetMapping("/agoras/{agoraId}/chats")
    public ResponseEntity getChatHistory(@PathVariable("agoraId") Long agoraId,
                                         @RequestBody Cursor cursor) {

        GetChatResponse response = chatQueryService.getChatHistory(agoraId, cursor);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @GetMapping("/agoras/{agoraId}/users")
    public ResponseEntity getChatParticipants(@PathVariable("agoraId") Long agoraId) {

        GetChatParticipants response = chatQueryService.getChatParticipants(agoraId);

        return ResponseEntity.ok(ApiUtil.success(response));
    }
}
