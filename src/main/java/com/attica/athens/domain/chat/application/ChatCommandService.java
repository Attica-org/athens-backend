package com.attica.athens.domain.chat.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agoraUser.dao.AgoraUserRepository;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.chat.dao.ChatRepository;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.dto.request.SendChatRequest;
import com.attica.athens.domain.chat.dto.response.SendChatResponse;
import com.attica.athens.domain.chat.dto.response.SendChatResponse.SendChatData;
import com.attica.athens.domain.user.dao.BaseUserRepository;
import com.attica.athens.domain.user.dao.UserRepository;
import com.attica.athens.domain.user.domain.BaseUser;
import com.attica.athens.global.security.CustomUserDetails;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatCommandService {

    private final BaseUserRepository baseUserRepository;
    private final UserRepository userRepository;
    private final AgoraRepository agoraRepository;
    private final AgoraUserRepository agoraUserRepository;
    private final ChatRepository chatRepository;

    public SendChatResponse sendChat(CustomUserDetails userDetails, Long agoraId, SendChatRequest sendChatRequest) {

        Agora agora = findAgoraById(agoraId);

        Long userId = userDetails.getUserId();

        AgoraUser agoraUser = findAgoraUserByAgoraIdAndUserId(agora.getId(), userId);

        Chat chat = Chat.createChat(sendChatRequest.type(), sendChatRequest.message(), agoraUser);
        chat = chatRepository.save(chat);

        SendChatData sendChatData = SendChatData.createSendChatData(chat, agoraUser);

        return new SendChatResponse(chat.getType(), sendChatData);
    }

    private AgoraUser findAgoraUserByAgoraIdAndUserId(Long agoraId, Long userId) {
        return agoraUserRepository.findByAgoraIdAndUserId(agoraId, userId)
                .orElseThrow(() -> new IllegalArgumentException("User is not in the agora."));
    }

    private Agora findAgoraById(Long agoraId) {
        return agoraRepository.findById(agoraId)
                .orElseThrow(() -> new IllegalArgumentException("Agora is not exist."));
    }

    private BaseUser findUserByUuid(String userUuid) {
        return baseUserRepository.findByUuid(UUID.fromString(userUuid))
                .orElseThrow(() -> new IllegalArgumentException("User is not exist."));
    }

    private BaseUser findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User is not exist."));
    }
}
