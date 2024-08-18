package com.attica.athens.domain.chat.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agora.exception.NotParticipateException;
import com.attica.athens.domain.agoraMember.dao.AgoraMemberRepository;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.chat.dao.ChatRepository;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.domain.ChatContent;
import com.attica.athens.domain.chat.domain.ChatType;
import com.attica.athens.domain.chat.dto.request.SendChatRequest;
import com.attica.athens.domain.chat.dto.response.InAndOutNotificationResponse;
import com.attica.athens.domain.chat.dto.response.SendChatResponse;
import com.attica.athens.global.auth.CustomUserDetails;
import com.vane.badwordfiltering.BadWordFiltering;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatCommandService {

    private final AgoraRepository agoraRepository;
    private final AgoraMemberRepository agoraMemberRepository;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final BadWordFiltering badWordFiltering;

    public SendChatResponse sendChat(final CustomUserDetails userDetails, final Long agoraId,
                                     final SendChatRequest sendChatRequest) {
        validateAgoraExists(agoraId);

        AgoraMember agoraMember = findValidAgoraMember(agoraId, userDetails.getUserId());

        Chat chat = createAndSaveChat(sendChatRequest, agoraMember);

        return new SendChatResponse(agoraMember, chat);
    }

    public void sendJoinChat(final Long agoraId, final CustomUserDetails userDetails) {

        InAndOutNotificationResponse notification = getNotification(agoraId, userDetails);

        messagingTemplate.convertAndSend("/topic/agoras/" + agoraId + "/join", notification);
    }

    private InAndOutNotificationResponse getNotification(Long agoraId, CustomUserDetails userDetails) {
        AgoraMember agoraMember = findValidAgoraMember(agoraId, userDetails.getUserId());

        return new InAndOutNotificationResponse(ChatType.META,
                new InAndOutNotificationResponse.InAndOutAgoraMemberData(agoraId, agoraMember));
    }

    public void sendExitChat(final Long agoraId, final CustomUserDetails userDetails) {

        InAndOutNotificationResponse notification = getNotification(agoraId, userDetails);

        messagingTemplate.convertAndSend("/topic/agoras/" + agoraId + "/exit", notification);
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
        ChatContent chatContent = new ChatContent(sendChatRequest.message());
        String filteredChat = chatContent.checkBadWordAndFiltering(chatContent.getContent(), badWordFiltering);

        Chat chat = new Chat(sendChatRequest.type(), new ChatContent(filteredChat), agoraMember);

        return chatRepository.save(chat);
    }
}
