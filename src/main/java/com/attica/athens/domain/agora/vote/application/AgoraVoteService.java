package com.attica.athens.domain.agora.vote.application;

import com.attica.athens.domain.agora.dao.AgoraRepository;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.exception.NotFoundAgoraException;
import com.attica.athens.domain.agora.vote.dao.AgoraQueryVoteRepository;
import com.attica.athens.domain.agora.vote.domain.KickVote;
import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;
import com.attica.athens.domain.agora.vote.dto.request.KickVoteRequest;
import com.attica.athens.domain.agora.vote.dto.response.AgoraVoteResponse;
import com.attica.athens.domain.agora.vote.dto.response.AgoraVoteResultResponse;
import com.attica.athens.domain.agora.vote.dto.response.KickVoteResult;
import com.attica.athens.domain.agora.vote.dto.response.SendKickResponse;
import com.attica.athens.domain.agora.vote.dto.response.SendKickResponse.KickVoteInfo;
import com.attica.athens.domain.agora.vote.dto.response.VoteResultResponse;
import com.attica.athens.domain.agora.vote.exception.AlreadyKickVotedException;
import com.attica.athens.domain.agora.vote.exception.AlreadyOpinionVotedException;
import com.attica.athens.domain.agoraMember.dao.AgoraMemberRepository;
import com.attica.athens.domain.agoraMember.domain.AgoraMember;
import com.attica.athens.domain.agoraMember.exception.NotFoundAgoraMemberException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgoraVoteService {

    private Map<Long, Map<Long, KickVote>> activeVotes = new ConcurrentHashMap<>();

    private final AgoraQueryVoteRepository agoraVoteRepository;
    private final AgoraRepository agoraRepository;
    private final AgoraMemberRepository agoraMemberRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public AgoraVoteResponse vote(Long userId, AgoraVoteRequest agoraVoteRequest, Long agoraId) {

        Agora agora = findAgoraById(agoraId);

        AgoraMember agoraMember = checkAgoraMemberVoteProcess(agoraVoteRequest, agora, userId);

        agoraMember.updateIsOpinionVotedAndVoteType(agoraVoteRequest.voteType(), agoraVoteRequest.isOpinionVoted());

        return new AgoraVoteResponse(agoraMember);
    }

    @Transactional
    public AgoraVoteResultResponse voteResult(Long agoraId) {

        Agora agora = findAgoraById(agoraId);

        VoteResultResponse voteResults = agoraVoteRepository.getVoteResults(agoraId);

        agora.updateProsCountAndConsCount(voteResults.pros(), voteResults.cons());

        return new AgoraVoteResultResponse(agoraId, voteResults.pros(), voteResults.cons());
    }

    @Transactional
    public KickVoteResult kickVote(Long agoraId, Long memberId, KickVoteRequest request) {
        AgoraMember member = agoraMemberRepository.findById(request.targetMemberId())
                .orElseThrow(() -> new NotFoundAgoraMemberException(agoraId, request.targetMemberId()));

        Long targetMemberId = member.getMember().getId();
        Map<Long, KickVote> kickVoteMap = activeVotes.computeIfAbsent(agoraId, k -> new ConcurrentHashMap<>());
        KickVote kickVote = kickVoteMap.computeIfAbsent(targetMemberId, k -> new KickVote());

        validateAndAddVote(kickVote, memberId, targetMemberId);

        if (kickVote.kickPossible(request.currentMemberCount())) {
            handleKickVoteSuccess(kickVoteMap, targetMemberId);
            sendKickVoteInfo(agoraId, member.getNickname(), targetMemberId);
            return KickVoteResult.KICK_REQUIRED;
        }

        return KickVoteResult.VOTE_SUCCESS;
    }

    public void removeVotes(Long agoraId) {
        activeVotes.remove(agoraId);
    }

    private void validateAndAddVote(KickVote kickVote, Long memberId, Long targetMemberId) {
        if (!kickVote.addVoteMember(memberId)) {
            throw new AlreadyKickVotedException(targetMemberId);
        }
    }

    private void handleKickVoteSuccess(Map<Long, KickVote> kickVoteMap, Long targetMemberId) {
        kickVoteMap.remove(targetMemberId);
        kickVoteMap.values().forEach(vote -> vote.removeVoteMember(targetMemberId));
    }

    private void sendKickVoteInfo(Long agoraId, String nickname, Long targetMemberId) {
        String destination = "/topic/agoras/" + agoraId + "/kick";
        KickVoteInfo kickVoteInfo = new KickVoteInfo(targetMemberId, nickname);
        SendKickResponse response = new SendKickResponse(kickVoteInfo);
        simpMessagingTemplate.convertAndSend(destination, response);
    }

    private AgoraMember checkAgoraMemberVoteProcess(AgoraVoteRequest agoraVoteRequest, Agora agora, Long userId) {
        agora.checkAgoraVoteRequest(agoraVoteRequest);
        agora.checkAgoraStatus();
        agora.checkVoteTime();

        AgoraMember agoraMember = findAgoraMemberByAgoraIdAndUserId(agora.getId(), userId);

        if (agoraMember.getIsOpinionVoted()) {
            throw new AlreadyOpinionVotedException();
        }
        return agoraMember;
    }

    private Agora findAgoraById(Long agoraId) {
        return agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundAgoraException(agoraId));
    }

    private AgoraMember findAgoraMemberByAgoraIdAndUserId(Long agoraId, Long userId) {
        return agoraMemberRepository.findLatestByAgoraIdAndMemberId(agoraId, userId)
                .orElseThrow(() -> new NotFoundAgoraMemberException(agoraId, userId));
    }
}
