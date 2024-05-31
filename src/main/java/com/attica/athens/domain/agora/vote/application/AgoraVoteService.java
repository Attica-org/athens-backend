package com.attica.athens.domain.agora.vote.application;

import com.attica.athens.domain.agora.vote.dao.AgoraVoteRepository;
import com.attica.athens.domain.agora.vote.dao.AgoraVoteUserRepository;
import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;
import com.attica.athens.domain.agora.vote.dto.response.AgoraVoteResponse;
import com.attica.athens.domain.agora.vote.exception.NotFoundUserException;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgoraVoteService {

    private final AgoraVoteUserRepository agoraVoteUserRepository;
    private final AgoraVoteRepository agoraVoteRepository;

    public AgoraUser getAgoraUser(Long id) {

        AgoraUser agoraUser = agoraVoteUserRepository.findByUserId(id)
                .orElseThrow(() -> new NotFoundUserException(id));

        return agoraUser;
    }

    @Transactional
    public AgoraVoteResponse vote(AgoraUser agoraUser, AgoraVoteRequest agoraVoteRequest) {

        AgoraUser updatedAgoraUser = agoraVoteRepository.updateVoteType(agoraUser, agoraVoteRequest);

        return new AgoraVoteResponse(updatedAgoraUser.getId(), updatedAgoraUser.getVoteType());
    }

}
