package com.attica.athens.domain.agora.vote.api;

import com.attica.athens.domain.agora.vote.application.AgoraVoteService;
import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;
import com.attica.athens.domain.agora.vote.dto.response.AgoraVoteResponse;
import com.attica.athens.domain.agoraUser.domain.AgoraUser;
import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import com.attica.athens.domain.user.domain.BaseUser;
import com.attica.athens.global.auth.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/agoras")
public class AgoraVoteController {

    private final AgoraVoteService agoraVoteService;
    public AgoraVoteController(AgoraVoteService agoraVoteService) {
        this.agoraVoteService = agoraVoteService;
    }

    @PatchMapping("vote")
    public ResponseEntity<ApiResponse<?>> vote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AgoraVoteRequest agoraVoteRequest
    ) {

        AgoraUser agoraUser = agoraVoteService.getAgoraUser(userDetails.getUserId());

        AgoraVoteResponse agoraVoteProsResponse = agoraVoteService.vote(agoraUser, agoraVoteRequest);

        return ResponseEntity.ok(ApiUtil.success(agoraVoteProsResponse));
    }

}
