package com.attica.athens.domain.chat.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agoraUser.dao.AgoraUserRepository;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.domain.AgoraUserType;
import com.attica.athens.domain.chat.dao.ChatRepository;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.dto.Cursor;
import com.attica.athens.domain.chat.dto.response.GetChatParticipants;
import com.attica.athens.domain.chat.dto.response.GetChatResponse;
import com.attica.athens.domain.chat.dto.response.GetChatResponse.ChatData;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    private final AgoraUserRepository agoraUserRepository;
    private final ChatRepository chatRepository;

    public GetChatResponse getChatHistory(final Long agoraId, final Cursor cursor) {
        validateAgoraExists(agoraId);
        List<AgoraUser> agoraUsers = findAgoraUsers(agoraId);
        List<Chat> chats = findChats(cursor, agoraUsers);
        List<ChatData> chatData = createChatData(agoraUsers, chats);
        Cursor nextCursor = calculateNextCursor(cursor, chats);

        return new GetChatResponse(chatData, nextCursor);
    }

    private void validateAgoraExists(final Long agoraId) {
        agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundAgoraException(agoraId));
    }

    private List<AgoraUser> findAgoraUsers(final Long agoraId) {
        return agoraUserRepository.findByAgoraId(agoraId);
    }

    private List<Chat> findChats(final Cursor cursor, final List<AgoraUser> agoraUsers) {
        List<Long> agoraUserIds = extractAgoraUserIds(agoraUsers);
        Pageable pageable = createPageable(cursor);

        if (cursor.hasKey()) {
            return chatRepository.findByAgoraUserIdInAndIdLessThanOrderByIdDesc(agoraUserIds, cursor.key(),
                    pageable);
        }
        return chatRepository.findByAgoraUserIdInOrderByIdDesc(agoraUserIds, pageable);
    }

    private List<Long> extractAgoraUserIds(final List<AgoraUser> agoraUsers) {
        return agoraUsers.stream()
                .map(AgoraUser::getId)
                .toList();
    }

    private PageRequest createPageable(final Cursor cursor) {
        return PageRequest.of(0, cursor.getEffectiveSize());
    }

    private Cursor calculateNextCursor(final Cursor currentCursor, final List<Chat> chats) {
        if (chats.isEmpty() || chats.get(chats.size() - 1).getId().equals(1L)) {
            return currentCursor.next(Cursor.NONE_KEY);
        }
        return currentCursor.next(findMinChatId(chats));
    }

    private long findMinChatId(final List<Chat> chats) {
        return chats.stream()
                .mapToLong(Chat::getId)
                .min()
                .orElse(Cursor.NONE_KEY);
    }

    private List<ChatData> createChatData(final List<AgoraUser> agoraUsers, final List<Chat> chats) {
        Map<Long, AgoraUser> agoraUserMap = createAgoraUserMap(agoraUsers);
        return chats.stream()
                .map(chat -> new ChatData(chat, agoraUserMap.get(chat.getAgoraUser().getId())))
                .toList();
    }

    private Map<Long, AgoraUser> createAgoraUserMap(final List<AgoraUser> agoraUsers) {
        return agoraUsers.stream()
                .collect(Collectors.toMap(AgoraUser::getId, agoraUser -> agoraUser));
    }

    public GetChatParticipants getChatParticipants(final Long agoraId) {
        validateAgoraExists(agoraId);

        return new GetChatParticipants(findActiveParticipants(agoraId), agoraId);
    }

    private List<AgoraUser> findActiveParticipants(final Long agoraId) {
        return agoraUserRepository.findByAgoraIdAndTypeInAndSessionIdIsNotNull(agoraId,
                Arrays.asList(AgoraUserType.PROS, AgoraUserType.CONS));
    }
}
