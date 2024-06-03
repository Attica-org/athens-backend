package com.attica.athens.domain.agora.vote.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agora.vote.dao.AgoraVoteRepository;
import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;
import com.attica.athens.domain.agora.vote.dto.response.AgoraVoteResponse;
import com.attica.athens.domain.agora.vote.dto.response.AgoraVoteResultResponse;
import com.attica.athens.domain.agora.vote.exception.NotFoundUserException;
import com.attica.athens.domain.agoraUser.dao.AgoraUserRepository;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.exception.NotFoundAgoraUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgoraVoteService {

    private final AgoraVoteRepository agoraVoteRepository;
    private final AgoraRepository agoraRepository;
    private final AgoraUserRepository agoraUserRepository;

    @Transactional
    public AgoraVoteResponse vote(Long userId, AgoraVoteRequest agoraVoteRequest, Long agoraId) {

        agoraUserRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundUserException(userId));

        agoraVoteRepository.updateVoteType(userId, agoraVoteRequest, agoraId);

        AgoraUser agoraUser = agoraUserRepository.findByAgoraIdAndUserId(agoraId, userId)
                .orElseThrow(() -> new NotFoundAgoraUserException(agoraId, userId));

        return new AgoraVoteResponse(agoraUser.getId(), agoraUser.getVoteType());
    }

    @Transactional
    public AgoraVoteResultResponse voteResult(Long agoraId) {

        Agora agoraResult = findAgoraById(agoraId);

        Long prosVoteResult = agoraVoteRepository.getProsVoteResult(agoraId);
        Long consVoteResult = agoraVoteRepository.getConsVoteResult(agoraId);

        agoraVoteRepository.updateVoteResult(agoraResult, prosVoteResult, consVoteResult);

        return new AgoraVoteResultResponse(agoraId, prosVoteResult, consVoteResult);

    }

    private Agora findAgoraById(Long agoraId) {
        return agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundAgoraException(agoraId));
    }

}
