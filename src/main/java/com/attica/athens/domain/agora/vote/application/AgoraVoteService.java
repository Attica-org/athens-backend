package com.attica.athens.domain.agora.vote.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agora.exception.InvalidAgoraStatusException;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agora.vote.dao.AgoraVoteRepository;
import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;
import com.attica.athens.domain.agora.vote.dto.response.AgoraVoteResponse;
import com.attica.athens.domain.agora.vote.dto.response.AgoraVoteResultResponse;
import com.attica.athens.domain.agora.vote.exception.AlreadyOpinionVotedException;
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

        checkAgoraUserVoted(agoraId, userId);

        Agora agora = findAgoraById(agoraId);

        checkAgoraStatus(agora);
        findAgoraUserByAgoraIdAndUserId(agoraId, userId);

        AgoraUser agoraUser = agoraVoteRepository.updateVoteType(userId, agoraVoteRequest.voteType(),
                agoraVoteRequest.isOpinionVoted(), agoraId);

        return new AgoraVoteResponse(agoraUser.getId(), agoraUser.getVoteType());
    }

    @Transactional
    public AgoraVoteResultResponse voteResult(Long agoraId) {

        findAgoraById(agoraId);

        Integer prosVoteResult = agoraVoteRepository.getProsVoteResult(agoraId);
        Integer consVoteResult = agoraVoteRepository.getConsVoteResult(agoraId);

        agoraVoteRepository.updateVoteResult(agoraId, prosVoteResult, consVoteResult);

        return new AgoraVoteResultResponse(agoraId, prosVoteResult, consVoteResult);

    }

    private void checkAgoraUserVoted(Long agoraId, Long userId) {
        AgoraUser agoraUser = findAgoraUserByAgoraIdAndUserId(agoraId, userId);

        if (agoraUser.isOpinionVoted()) {
            throw new AlreadyOpinionVotedException();
        }
    }

    private Agora findAgoraById(Long agoraId) {
        return agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundAgoraException(agoraId));
    }

    private AgoraUser findAgoraUserByAgoraIdAndUserId(Long agoraId, Long userId) {
        return agoraUserRepository.findByAgoraIdAndUserId(agoraId, userId)
                .orElseThrow(() -> new NotFoundAgoraUserException(agoraId, userId));
    }

    private void checkAgoraStatus(Agora agora) {
        if (agora.getStatus().equals("QUEUED") || agora.getStatus().equals("RUNNING")) {
            throw new InvalidAgoraStatusException(AgoraStatus.CLOSED);
        }
    }
}
