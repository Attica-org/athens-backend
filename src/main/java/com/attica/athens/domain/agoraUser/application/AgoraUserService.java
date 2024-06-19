package com.attica.athens.domain.agoraUser.application;

import com.attica.athens.domain.agoraUser.dao.AgoraUserRepository;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.chat.application.ChatQueryService;
import com.attica.athens.domain.chat.dto.response.SendMetaResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgoraUserService {

    private final AgoraUserRepository agoraUserRepository;
    private final ChatQueryService chatQueryService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public void updateSessionName(Long agoraId, Long userId, String sessionName) {
        agoraUserRepository.findByAgoraIdAndUserId(agoraId, userId)
                .ifPresent(agoraUser -> agoraUser.updateSessionName(sessionName));
    }

    @Transactional
    public void removeSessionName(String sessionName) {
        AgoraUser agoraUser = agoraUserRepository.findBySessionName(sessionName)
                .orElseThrow(() -> new IllegalArgumentException("Session name is not exist."));
        agoraUser.updateSessionName(null);
    }
}
