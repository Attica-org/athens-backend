package com.attica.athens.domain.chat.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agora.exception.NotParticipateException;
import com.attica.athens.domain.agoraMember.dao.AgoraMemberRepository;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.chat.dao.ChatRepository;
import com.attica.athens.domain.chat.dao.ReactionRepository;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.domain.ChatContent;
import com.attica.athens.domain.chat.domain.Reaction;
import com.attica.athens.domain.chat.domain.ReactionType;
import com.attica.athens.domain.chat.dto.request.SendChatRequest;
import com.attica.athens.domain.chat.dto.request.SendReactionRequest;
import com.attica.athens.domain.chat.dto.response.SendChatResponse;
import com.attica.athens.domain.chat.dto.response.SendReactionResponse;
import com.attica.athens.domain.chat.exception.NotFoundChatException;
import com.attica.athens.domain.chat.exception.WriterReactionException;
import com.attica.athens.global.auth.CustomUserDetails;
import java.util.EnumMap;
import java.util.List;
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
    private final ReactionRepository reactionRepository;

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

    @Transactional
    public SendReactionResponse sendReaction(final CustomUserDetails userDetails, final Long agoraId, Long chatId,
                                             final SendReactionRequest sendReactionRequest) {
        validateAgoraExists(agoraId);
        AgoraMember agoraMember = findValidAgoraMember(agoraId, userDetails.getUserId());

        Chat chat = findChat(chatId);
        validIsNotWriter(userDetails.getUserId(), chat.getAgoraMember().getId());

        ReactionType reactionType = sendReactionRequest.reactionType();

        if (findReactionExists(chat.getId(), reactionType, agoraMember.getId())) {
            reactionRepository.deleteByChatIdAndAgoraMemberIdAndType(chatId, agoraMember.getId(), reactionType);
        } else {
            Reaction reaction = new Reaction(reactionType, chat, agoraMember);
            reactionRepository.save(reaction);
        }

        return new SendReactionResponse(chatId,
                getReactionTypeIntegerEnumMap(chatId));
    }

    private void validIsNotWriter(final Long userId, final Long writerId) {
        if (userId.equals(writerId)) {
            throw new WriterReactionException();
        }
    }

    private boolean findReactionExists(final Long chatId, final ReactionType type, final Long agoraMemberId) {
        return reactionRepository.existsByChatIdAndAgoraMemberIdAndType(chatId, agoraMemberId, type);
    }

    private EnumMap<ReactionType, Long> getReactionTypeIntegerEnumMap(final Long chatId) {
        EnumMap<ReactionType, Long> counts = new EnumMap<>(ReactionType.class);
        for (ReactionType reactionType : ReactionType.values()) {
            counts.put(reactionType, 0L);
        }

        List<Object[]> results = reactionRepository.countReactionsByChatId(chatId);

        for (Object[] result : results) {
            ReactionType type = (ReactionType) result[0];
            Long count = (Long) result[1];
            counts.put(type, count);
        }

        return counts;
    }

    private Chat findChat(final Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundChatException(chatId));
    }
}
