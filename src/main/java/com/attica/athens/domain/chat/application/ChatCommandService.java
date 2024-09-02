package com.attica.athens.domain.chat.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agora.exception.NotParticipateException;
import com.attica.athens.domain.agoraMember.dao.AgoraMemberRepository;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.chat.component.BadWordFilter;
import com.attica.athens.domain.chat.component.FilterResult;
import com.attica.athens.domain.chat.dao.ChatRepository;
import com.attica.athens.domain.chat.dao.ReactionRepository;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.domain.ChatContent;
import com.attica.athens.domain.chat.domain.Reaction;
import com.attica.athens.domain.chat.domain.ReactionType;
import com.attica.athens.domain.chat.dto.projection.ReactionCount;
import com.attica.athens.domain.chat.dto.request.SendChatRequest;
import com.attica.athens.domain.chat.dto.request.SendReactionRequest;
import com.attica.athens.domain.chat.dto.response.BadWordResponse;
import com.attica.athens.domain.chat.dto.response.SendChatResponse;
import com.attica.athens.domain.chat.dto.response.SendReactionResponse;
import com.attica.athens.domain.chat.exception.NotFoundChatException;
import com.attica.athens.domain.chat.exception.WriterReactionException;
import com.attica.athens.global.auth.CustomUserDetails;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatCommandService {

    private final AgoraRepository agoraRepository;
    private final AgoraMemberRepository agoraMemberRepository;
    private final ChatRepository chatRepository;
    private final BadWordFilter badWordFilter;
    private final ReactionRepository reactionRepository;

    private static final EnumMap<ReactionType, Long> EMPTY_ENUM_MAP = new EnumMap<>(ReactionType.class);

    static {
        for (ReactionType reactionType : ReactionType.values()) {
            EMPTY_ENUM_MAP.put(reactionType, 0L);
        }
    }

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

        if (hasReaction(chat.getId(), reactionType, agoraMember.getId())) {
            removeReaction(chatId, agoraMember, reactionType);
        } else {
            addReaction(agoraMember, chat, reactionType);
        }

        return new SendReactionResponse(chatId,
                getReactionTypeEnumMap(chatId));
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

    private Map<ReactionType, Long> getReactionTypeEnumMap(final Long chatId) {
        Map<ReactionType, Long> counts = new EnumMap<>(EMPTY_ENUM_MAP);
        List<ReactionCount> results = reactionRepository.countReactionsByChatId(chatId);
        for (ReactionCount result : results) {
            counts.put(result.getType(), result.getCount());
        }
        return counts;
    }

    public ResponseEntity<?> checkBadWord(final CustomUserDetails userDetails, final Long agoraId,
                                          final SendChatRequest sendChatRequest) {
        validateAgoraExists(agoraId);
        findValidAgoraMember(agoraId, userDetails.getUserId());

        FilterResult filter = badWordFilter.filter(sendChatRequest.message());

        if (!filter.getBadword().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new BadWordResponse(badWordFilter.filter(sendChatRequest.message())));
        }
        return ResponseEntity.ok("비속어가 포함되어있지 않습니다.");
    }
}
