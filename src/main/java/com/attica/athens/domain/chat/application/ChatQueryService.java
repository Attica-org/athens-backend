package com.attica.athens.domain.chat.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agoraUser.dao.AgoraUserRepository;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.domain.AgoraUserType;
import com.attica.athens.domain.chat.dao.ChatRepository;
import com.attica.athens.domain.chat.domain.Chat;
import com.attica.athens.domain.chat.domain.Chats;
import com.attica.athens.domain.chat.dto.Cursor;
import com.attica.athens.domain.chat.dto.response.GetChatParticipants;
import com.attica.athens.domain.chat.dto.response.GetChatResponse;
import com.attica.athens.domain.chat.dto.response.GetChatResponse.ChatData;
import java.util.List;
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
        Chats chats = new Chats(findChats(cursor, agoraUsers));
        List<ChatData> chatData = chats.createChatDataWithUsers(agoraUsers);
        Cursor nextCursor = cursor.calculateNext(chats);

        return new GetChatResponse(chatData, nextCursor);
    }

    public GetChatParticipants getChatParticipants(final Long agoraId) {
        validateAgoraExists(agoraId);

        return new GetChatParticipants(findActiveParticipants(agoraId), agoraId);
    }

    private void validateAgoraExists(final Long agoraId) {
        agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundAgoraException(agoraId));
    }

    private List<AgoraUser> findAgoraUsers(final Long agoraId) {
        return agoraUserRepository.findByAgoraId(agoraId);
    }

    private List<Chat> findChats(final Cursor cursor, final List<AgoraUser> agoraUsers) {
        List<Long> agoraUserIds = agoraUsers.stream()
                .map(AgoraUser::getId)
                .toList();
        Pageable pageable = PageRequest.of(0, cursor.getEffectiveSize());

        return chatRepository.findChatsForAgoraUsers(agoraUserIds,
                cursor.hasKey() ? cursor.key() : null,
                pageable);
    }

    private List<AgoraUser> findActiveParticipants(final Long agoraId) {
        return agoraUserRepository.findByAgoraIdAndTypeInAndSessionIdIsNotNull(agoraId,
                List.of(AgoraUserType.PROS, AgoraUserType.CONS));
    }
}
