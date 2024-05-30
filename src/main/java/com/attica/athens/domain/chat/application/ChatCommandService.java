package com.attica.athens.domain.chat.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agoraUser.dao.AgoraUserRepository;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.chat.dao.ChatRepository;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.dto.request.SendChatRequest;
import com.attica.athens.domain.chat.dto.response.SendChatResponse;
import com.attica.athens.domain.chat.dto.response.SendChatResponse.SendChatData;
import com.attica.athens.global.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatCommandService {

    private final AgoraRepository agoraRepository;
    private final AgoraUserRepository agoraUserRepository;
    private final ChatRepository chatRepository;

    public SendChatResponse sendChat(CustomUserDetails userDetails, Long agoraId, SendChatRequest sendChatRequest) {

        if (!existsById(agoraId)) {
            throw new NotFoundAgoraException(agoraId);
        }

        AgoraUser agoraUser = findAgoraUserByAgoraIdAndUserId(agoraId, userDetails.getUserId());

        Chat chat = chatRepository.save(
                Chat.createChat(sendChatRequest.type(), sendChatRequest.message(), agoraUser)
        );

        return new SendChatResponse(
                chat.getType(),
                SendChatData.createSendChatData(chat, agoraUser)
        );
    }

    private AgoraUser findAgoraUserByAgoraIdAndUserId(Long agoraId, Long userId) {
        return agoraUserRepository.findByAgoraIdAndUserId(agoraId, userId)
                .orElseThrow(() -> new IllegalArgumentException("User is not in the agora."));
    }

    private boolean existsById(Long agoraId) {
        return agoraRepository.existsById(agoraId);
    }
}
