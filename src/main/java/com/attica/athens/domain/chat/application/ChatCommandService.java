package com.attica.athens.domain.chat.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agora.exception.NotParticipateException;
import com.attica.athens.domain.agoraUser.dao.AgoraUserRepository;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.chat.dao.ChatRepository;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.domain.ChatContent;
import com.attica.athens.domain.chat.dto.request.SendChatRequest;
import com.attica.athens.domain.chat.dto.response.SendChatResponse;
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

    public SendChatResponse sendChat(final CustomUserDetails userDetails, final Long agoraId,
                                     final SendChatRequest sendChatRequest) {
        validateAgoraExists(agoraId);

        AgoraUser agoraUser = findValidAgoraUser(agoraId, userDetails.getUserId());
        Chat chat = createAndSaveChat(sendChatRequest, agoraUser);

        return new SendChatResponse(agoraUser, chat);
    }

    private void validateAgoraExists(final Long agoraId) {
        if (!agoraRepository.existsById(agoraId)) {
            throw new NotFoundAgoraException(agoraId);
        }
    }

    private AgoraUser findValidAgoraUser(final Long agoraId, final Long userId) {
        return agoraUserRepository.findByAgoraIdAndUserIdAndSessionIdIsNotNull(agoraId, userId)
                .orElseThrow(NotParticipateException::new)
                .validateSendMessage();
    }

    private Chat createAndSaveChat(final SendChatRequest sendChatRequest, final AgoraUser agoraUser) {
        Chat chat = new Chat(sendChatRequest.type(), new ChatContent(sendChatRequest.message()), agoraUser);

        return chatRepository.save(chat);
    }
}
