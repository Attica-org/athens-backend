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
import com.attica.athens.domain.agoraMember.dao.AgoraMemberRepository;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.agoraMember.exception.NotFoundAgoraMemberException;
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
    private final AgoraMemberRepository agoraMemberRepository;

    @Transactional
    public AgoraVoteResponse vote(Long userId, AgoraVoteRequest agoraVoteRequest, Long agoraId) {

        checkAgoraMemberVoted(agoraId, userId);
        checkAgoraVoteRequest(agoraVoteRequest);

        Agora agora = findAgoraById(agoraId);

        checkAgoraStatus(agora);

        AgoraMember agoraMember = findAgoraMemberByAgoraIdAndUserId(agoraId, userId);

        agoraMember.updateIsOpinionVotedAndVoteType(agoraVoteRequest.voteType(), agoraVoteRequest.isOpinionVoted());

        checkVoteTime(agora);

        return new AgoraVoteResponse(agoraMember);

    }

    private void checkVoteTime(Agora agora) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime endTime = agora.getEndTime();

        if (Duration.between(now, endTime).getSeconds() > 20) {
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

    private void checkAgoraMemberVoted(Long agoraId, Long userId) {
        AgoraMember agoraMember = findAgoraMemberByAgoraIdAndUserId(agoraId, userId);

        if (agoraMember.getIsOpinionVoted()) {
            throw new AlreadyOpinionVotedException();
        }
    }

    private Agora findAgoraById(Long agoraId) {
        return agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundAgoraException(agoraId));
    }

    private AgoraMember findAgoraMemberByAgoraIdAndUserId(Long agoraId, Long userId) {
        return agoraMemberRepository.findByAgoraIdAndMemberId(agoraId, userId)
                .orElseThrow(() -> new NotFoundAgoraMemberException(agoraId, userId));
    }

    private void checkAgoraStatus(Agora agora) {
        if (agora.getStatus().equals("QUEUED") || agora.getStatus().equals("RUNNING")) {
            throw new InvalidAgoraStatusException(AgoraStatus.CLOSED);
        }
    }
}
