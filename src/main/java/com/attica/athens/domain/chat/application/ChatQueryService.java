package com.attica.athens.domain.chat.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agora.exception.NotParticipateException;
import com.attica.athens.domain.agoraMember.dao.AgoraMemberRepository;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.agoraMember.domain.AgoraMemberType;
import com.attica.athens.domain.chat.dao.ChatRepository;
import com.attica.athens.domain.chat.dao.ReactionRepository;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.domain.Chats;
import com.attica.athens.domain.chat.domain.ReactionType;
import com.attica.athens.domain.chat.dto.Cursor;
import com.attica.athens.domain.chat.dto.projection.ReactionCountById;
import com.attica.athens.domain.chat.dto.response.GetChatParticipants;
import com.attica.athens.domain.chat.dto.response.GetChatResponse;
import com.attica.athens.domain.chat.dto.response.GetChatResponse.ChatData;
import com.attica.athens.global.auth.domain.CustomUserDetails;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatQueryService {

    private final AgoraRepository agoraRepository;
    private final AgoraMemberRepository agoraMemberRepository;
    private final ChatRepository chatRepository;
    private final ReactionRepository reactionRepository;

    public GetChatResponse getClosedChatHistory(final Long agoraId, final Cursor cursor) {
        validateAgoraExists(agoraId);
        List<AgoraMember> agoraMembers = findAgoraMembers(agoraId);
        return getChatHistory(agoraMembers, cursor, this::findChats);
    }

    public GetChatResponse getActiveChatHistory(final CustomUserDetails userDetails, final Long agoraId, final Cursor cursor) {
        validateAgoraExists(agoraId);
        AgoraMember agoraMember = findValidAgoraMember(agoraId, userDetails.getUserId());
        List<AgoraMember> agoraMembers = findAgoraMembers(agoraId);
        return getChatHistory(agoraMembers, cursor, (c, members) -> findChatsFromJoinTime(c, members, agoraMember));
    }

    private GetChatResponse getChatHistory(List<AgoraMember> agoraMembers, Cursor cursor, ChatFinder chatFinder) {
        Chats chats = new Chats(chatFinder.findChats(cursor, agoraMembers));
        Map<Long, Map<ReactionType, Long>> reactionCounts = getReactionCounts(chats.getChats());
        List<ChatData> chatData = chats.createChatDataWithUsers(agoraMembers, reactionCounts);
        Cursor nextCursor = cursor.calculateNext(chats);
        return new GetChatResponse(chatData, nextCursor);
    }

    private AgoraMember findValidAgoraMember(final Long agoraId, final Long memberId) {
        return agoraMemberRepository.findByAgoraIdAndMemberIdAndSessionIdIsNotNull(agoraId, memberId)
                .orElseThrow(NotParticipateException::new)
                .validateSendMessage();
    }

    private Map<Long, Map<ReactionType, Long>> getReactionCounts(List<Chat> chats) {
        List<Long> chatIds = chats.stream()
                .map(Chat::getId)
                .toList();
        List<ReactionCountById> reactionCounts = reactionRepository.countReactionsByChatIds(chatIds);

        Map<Long, Map<ReactionType, Long>> result = new HashMap<>();
        for (Long chatId : chatIds) {
            Map<ReactionType, Long> reactionMap = new EnumMap<>(ReactionType.class);
            for (ReactionType reactionType : ReactionType.values()) {
                reactionMap.put(reactionType, 0L);
            }
            result.put(chatId, reactionMap);
        }
        for (ReactionCountById reactionCount : reactionCounts) {
            result.get(reactionCount.getChatId())
                    .put(reactionCount.getType(), reactionCount.getCount());
        }
        return result;
    }

    public GetChatParticipants getChatParticipants(final Long agoraId) {
        validateAgoraExists(agoraId);

        return new GetChatParticipants(findActiveParticipants(agoraId), agoraId);
    }

    private Agora validateAgoraExists(final Long agoraId) {
        return agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundAgoraException(agoraId));
    }

    private List<AgoraMember> findAgoraMembers(final Long agoraId) {
        return agoraMemberRepository.findByAgoraId(agoraId);
    }

    private List<Chat> findChats(final Cursor cursor, final List<AgoraMember> agoraMembers) {
        List<Long> agoraMemberIds = agoraMembers.stream()
                .map(AgoraMember::getId)
                .toList();
        Pageable pageable = PageRequest.of(0, cursor.getEffectiveSize());

        return chatRepository.findChatsForAgoraMembers(agoraMemberIds,
                cursor.hasKey() ? cursor.key() : null,
                pageable);
    }

    private List<Chat> findChatsFromJoinTime(final Cursor cursor, final List<AgoraMember> agoraMembers,
                                             final AgoraMember agoraMember) {
        List<Long> agoraMemberIds = agoraMembers.stream()
                .map(AgoraMember::getId)
                .toList();
        Pageable pageable = PageRequest.of(0, cursor.getEffectiveSize());

        return chatRepository.findChatsFromJoinTime(agoraMemberIds,
                agoraMember.getCreatedAt(),
                cursor.hasKey() ? cursor.key() : null,
                pageable);
    }

    private List<AgoraMember> findActiveParticipants(final Long agoraId) {
        return agoraMemberRepository.findByAgoraIdAndTypeInAndSessionIdIsNotNull(agoraId,
                List.of(AgoraMemberType.PROS, AgoraMemberType.CONS));
    }

    @FunctionalInterface
    private interface ChatFinder {
        List<Chat> findChats(Cursor cursor, List<AgoraMember> agoraMembers);
    }
}
