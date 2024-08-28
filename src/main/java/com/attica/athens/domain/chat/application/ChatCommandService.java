package com.attica.athens.domain.chat.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agora.exception.NotParticipateException;
import com.attica.athens.domain.agoraMember.dao.AgoraMemberRepository;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
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
    private final AgoraMemberRepository agoraMemberRepository;
    private final ChatRepository chatRepository;

    public SendChatResponse sendChat(final CustomUserDetails userDetails, final Long agoraId,
                                     final SendChatRequest sendChatRequest) {
        validateAgoraExists(agoraId);

        AgoraMember agoraMember = findValidAgoraMember(agoraId, userDetails.getUserId());

        Chat chat = createAndSaveChat(sendChatRequest, agoraMember);

        return new SendChatResponse(agoraMember, chat);
    }

    private void validateAgoraExists(final Long agoraId) {
        if (!agoraRepository.existsById(agoraId)) {
            throw new NotFoundAgoraException(agoraId);
        }
    }

    private AgoraMember findValidAgoraMember(final Long agoraId, final Long memberId) {
        return agoraMemberRepository.findByAgoraIdAndMemberIdAndSessionIdIsNotNull(agoraId, memberId)
                .orElseThrow(NotParticipateException::new)
                .validateSendMessage();
    }

    private Chat createAndSaveChat(final SendChatRequest sendChatRequest, final AgoraMember agoraMember) {
        Chat chat = new Chat(sendChatRequest.type(), new ChatContent(sendChatRequest.message()), agoraMember);

        return chatRepository.save(chat);
    }
}
