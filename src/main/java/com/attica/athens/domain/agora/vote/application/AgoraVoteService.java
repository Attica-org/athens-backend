package com.attica.athens.domain.agora.vote.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agora.exception.InvalidAgoraStatusException;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agora.vote.dao.AgoraQueryVoteRepository;
import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;
import com.attica.athens.domain.agora.vote.dto.response.AgoraVoteResponse;
import com.attica.athens.domain.agora.vote.dto.response.AgoraVoteResultResponse;
import com.attica.athens.domain.agora.vote.exception.AlreadyOpinionVotedException;
import com.attica.athens.domain.agora.vote.exception.InvalidAgoraVoteTypeException;
import com.attica.athens.domain.agora.vote.exception.VoteTimeOutException;
import com.attica.athens.domain.agoraUser.dao.AgoraUserRepository;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.agoraUser.exception.NotFoundAgoraUserException;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgoraVoteService {

    private final AgoraQueryVoteRepository agoraVoteRepository;
    private final AgoraRepository agoraRepository;
    private final AgoraUserRepository agoraUserRepository;

    @Transactional
    public AgoraVoteResponse vote(Long userId, AgoraVoteRequest agoraVoteRequest, Long agoraId) {

        checkAgoraUserVoted(agoraId, userId);
        checkAgoraVoteRequest(agoraVoteRequest);

        Agora agora = findAgoraById(agoraId);

        checkAgoraStatus(agora);

        AgoraUser agoraUser = findAgoraUserByAgoraIdAndUserId(agoraId, userId);

        agoraUser.updateIsOpinionVotedAndVoteType(agoraVoteRequest.voteType(), agoraVoteRequest.isOpinionVoted());
        // 이후에 update 되더라도 rollback되니 db에는 반영 x
        checkVoteTime(agora);

        return new AgoraVoteResponse(agoraUser);

    }

    private void checkVoteTime(Agora agora) {
        LocalDateTime now = LocalDateTime.now();

        //LocalDateTime endTime = now.plusSeconds(6);
        LocalDateTime endTime = agora.getEndTime();

        if (Duration.between(now, endTime).getSeconds() > 5) {
            throw new VoteTimeOutException();
        }
    }

    @Transactional
    public AgoraVoteResultResponse voteResult(Long agoraId) {

        Agora agora = findAgoraById(agoraId);

        Integer prosVoteResult = agoraVoteRepository.getProsVoteResult(agoraId);
        Integer consVoteResult = agoraVoteRepository.getConsVoteResult(agoraId);

        agora.updateProsCountAndConsCount(prosVoteResult, consVoteResult);

        return new AgoraVoteResultResponse(agoraId, prosVoteResult, consVoteResult);

    }

    private void checkAgoraVoteRequest(AgoraVoteRequest agoraVoteRequest) {
        if (agoraVoteRequest.voteType() == null) {
            throw new InvalidAgoraVoteTypeException();
        }
    }

    private void checkAgoraUserVoted(Long agoraId, Long userId) {
        AgoraUser agoraUser = findAgoraUserByAgoraIdAndUserId(agoraId, userId);

        if (agoraUser.getIsOpinionVoted()) {
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
