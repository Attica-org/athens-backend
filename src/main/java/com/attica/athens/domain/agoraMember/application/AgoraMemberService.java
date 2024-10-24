package com.attica.athens.domain.agoraMember.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agoraMember.dao.AgoraMemberRepository;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.agoraMember.dto.response.SendMetaResponse;
import com.attica.athens.domain.agoraMember.dto.response.SendMetaResponse.MetaData;
import com.attica.athens.domain.agoraMember.dto.response.SendMetaResponse.ParticipantsInfo;
import com.attica.athens.domain.agoraMember.exception.NotFoundAgoraMemberException;
import com.attica.athens.domain.agoraMember.exception.NotFoundSessionException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgoraMemberService {

    private final AgoraMemberRepository agoraMemberRepository;
    private final AgoraRepository agoraRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public void updateSessionId(Long agoraId, Long memberId, String sessionId) {
        agoraMemberRepository.findByAgoraIdAndMemberId(agoraId, memberId)
                .ifPresent(agoraMember -> agoraMember.updateSessionId(sessionId));
    }

    @Transactional
    public void removeSessionId(String sessionId) {
        AgoraMember agoraMember = agoraMemberRepository.findBySessionId(sessionId)
                .orElseThrow(NotFoundSessionException::new);
        agoraMember.updateSessionId(null);
        checkAgoraStatus(agoraMember.getAgora());
    }

    public void checkAgoraStatus(Agora agora) {
        if (agora.getStatus() == AgoraStatus.CLOSED) {
            return;
        }
        boolean isAnyAgoraMembersActive = agoraMemberRepository.existsByAgoraIdAndSessionIdIsNotNull(agora.getId());
        if (!isAnyAgoraMembersActive) {
            agora.endAgora();
        }
    }

    public void sendMetaToActiveMembers(Long agoraId, Long userId) {
        simpMessagingTemplate.convertAndSend("/topic/agoras/" + agoraId,
                new SendMetaResponse(
                        new MetaData(
                                findAgoraMemberByType(agoraId),
                                findAgoraById(agoraId),
                                findAgoraMemberByAgoraIdAndMemberId(agoraId, userId)
                        )
                ));
    }

    private List<ParticipantsInfo> findAgoraMemberByType(Long agoraId) {
        return agoraMemberRepository.countActiveAgoraMembersByType(agoraId);
    }

    private Agora findAgoraById(Long agoraId) {
        return agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundAgoraException(agoraId));
    }

    public Long findAgoraIdBySessionId(String sessionId) {
        return agoraMemberRepository.findBySessionId(sessionId)
                .map(agoraMember -> agoraMember.getAgora().getId())
                .orElseThrow(NotFoundSessionException::new);
    }

    public AgoraMember findAgoraMemberByAgoraIdAndMemberId(Long agoraId, Long memberId) {
        return agoraMemberRepository.findByAgoraIdAndMemberId(agoraId, memberId)
                .orElseThrow(() -> new NotFoundAgoraMemberException(agoraId, memberId));
    }

    public AgoraMember findAgoraMember(String sessionId) {
        return agoraMemberRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new NotFoundAgoraMemberException(sessionId));
    }
}
