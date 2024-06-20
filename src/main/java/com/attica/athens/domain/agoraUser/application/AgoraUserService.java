package com.attica.athens.domain.agoraUser.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agoraUser.dao.AgoraUserRepository;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.dto.response.SendMetaResponse;
import com.attica.athens.domain.agoraUser.dto.response.SendMetaResponse.MetaData;
import com.attica.athens.domain.agoraUser.dto.response.SendMetaResponse.ParticipantsInfo;
import com.attica.athens.domain.agoraUser.exception.NotFoundSessionException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgoraUserService {

    private final AgoraUserRepository agoraUserRepository;
    private final AgoraRepository agoraRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public void updateSessionId(Long agoraId, Long userId, String sessionId) {
        agoraUserRepository.findByAgoraIdAndUserId(agoraId, userId)
                .ifPresent(agoraUser -> agoraUser.updateSessionId(sessionId));
    }

    @Transactional
    public void removeSessionId(String sessionId) {
        AgoraUser agoraUser = agoraUserRepository.findBySessionId(sessionId)
                .orElseThrow(NotFoundSessionException::new);
        agoraUser.updateSessionId(null);
    }

    public void sendMetaToActiveUsers(Long agoraId) {
        simpMessagingTemplate.convertAndSend("/topic/agoras/" + agoraId + "/meta",
                new SendMetaResponse(
                        new MetaData(
                                findAgoraUserByType(agoraId),
                                findAgoraById(agoraId)
                        )
                ));
    }

    private List<ParticipantsInfo> findAgoraUserByType(Long agoraId) {
        return agoraUserRepository.countActiveAgoraUsersByType(agoraId);
    }

    private Agora findAgoraById(Long agoraId) {
        return agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundAgoraException(agoraId));
    }

    public Long findAgoraIdBySessionId(String sessionId) {
        return agoraUserRepository.findBySessionId(sessionId)
                .map(agoraUser -> agoraUser.getAgora().getId())
                .orElseThrow(NotFoundSessionException::new);
    }
}
