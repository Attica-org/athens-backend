package com.attica.athens.domain.chat.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.exception.ClosedAgoraException;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agora.exception.NotParticipateException;
import com.attica.athens.domain.agoraMember.dao.AgoraMemberRepository;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.chat.component.BadWordFilter;
import com.attica.athens.domain.chat.dao.ChatRepository;
import com.attica.athens.domain.chat.dao.ReactionRepository;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.domain.ChatContent;
import com.attica.athens.domain.chat.domain.FilterResult;
import com.attica.athens.domain.chat.domain.Reaction;
import com.attica.athens.domain.chat.domain.ReactionType;
import com.attica.athens.domain.chat.dto.request.SendChatRequest;
import com.attica.athens.domain.chat.dto.request.SendReactionRequest;
import com.attica.athens.domain.chat.dto.response.BadWordResponse;
import com.attica.athens.domain.chat.dto.response.SendChatResponse;
import com.attica.athens.domain.chat.dto.response.SendReactionResponse;
import com.attica.athens.domain.chat.exception.NotFoundChatException;
import com.attica.athens.domain.chat.exception.WriterReactionException;
import com.attica.athens.global.auth.domain.CustomUserDetails;
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
    private final BadWordFilter badWordFilter;

    public SendChatResponse sendChat(final CustomUserDetails userDetails, final Long agoraId,
                                     final SendChatRequest sendChatRequest) {
        validateAgora(agoraId);

        AgoraMember agoraMember = findValidAgoraMemberOfProsAndCons(agoraId, userDetails.getUserId());
        Chat chat = createAndSaveChat(sendChatRequest, agoraMember);

        return new SendChatResponse(agoraMember, chat);
    }

    private void validateAgora(final Long agoraId) {
        Agora agora = agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundAgoraException(agoraId));

        if (agora.isClosed()) {
            throw new ClosedAgoraException();
        }

        if (!agoraRepository.existsById(agoraId)) {
            throw new NotFoundAgoraException(agoraId);
        }
    }

    private AgoraMember findValidAgoraMemberOfProsAndCons(final Long agoraId, final Long memberId) {
        return findValidAgoraMember(agoraId, memberId)
                .validateSendMessage();
    }

    private AgoraMember findValidAgoraMember(final Long agoraId, final Long memberId) {
        return agoraMemberRepository.findByAgoraIdAndMemberIdAndSessionIdIsNotNull(agoraId, memberId)
                .orElseThrow(NotParticipateException::new);
    }

    private Chat createAndSaveChat(final SendChatRequest sendChatRequest, final AgoraMember agoraMember) {
        Chat chat = new Chat(sendChatRequest.type(), new ChatContent(sendChatRequest.message()), agoraMember);

        return chatRepository.save(chat);
    }

    @Transactional
    public SendReactionResponse sendReaction(final CustomUserDetails userDetails, final Long agoraId, Long chatId,
                                             final SendReactionRequest sendReactionRequest) {
        validateAgora(agoraId);
        AgoraMember agoraMember = findValidAgoraMember(agoraId, userDetails.getUserId());

        Chat chat = findChat(chatId);
        validIsNotWriter(userDetails.getUserId(), chat.getAgoraMember().getId());

        ReactionType reactionType = sendReactionRequest.reactionType();

        if (hasReaction(chat.getId(), reactionType, agoraMember.getId())) {
            removeReaction(chatId, agoraMember, reactionType);
        } else {
            addReaction(agoraMember, chat, reactionType);
        }

        return new SendReactionResponse(chatId,
                ReactionType.getReactionTypeEnumMap(reactionRepository.countReactionsByChatId(chatId)));
    }

    private void addReaction(final AgoraMember agoraMember, final Chat chat, final ReactionType reactionType) {
        Reaction reaction = new Reaction(reactionType, chat, agoraMember);
        reactionRepository.save(reaction);
    }

    private void removeReaction(final Long chatId, final AgoraMember agoraMember, final ReactionType reactionType) {
        reactionRepository.deleteByChatIdAndAgoraMemberIdAndType(chatId, agoraMember.getId(), reactionType);
    }

    private Chat findChat(final Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundChatException(chatId));
    }

    private void validIsNotWriter(final Long userId, final Long writerId) {
        if (userId.equals(writerId)) {
            throw new WriterReactionException();
        }
    }

    private boolean hasReaction(final Long chatId, final ReactionType type, final Long agoraMemberId) {
        return reactionRepository.existsByChatIdAndAgoraMemberIdAndType(chatId, agoraMemberId, type);
    }

    public BadWordResponse checkBadWord(final CustomUserDetails userDetails, final Long agoraId,
                                        final SendChatRequest sendChatRequest) {
        findValidAgoraMemberOfProsAndCons(agoraId, userDetails.getUserId());

        FilterResult filter = badWordFilter.filter(sendChatRequest.message());

        return new BadWordResponse(filter);
    }
}
