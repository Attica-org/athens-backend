package com.attica.athens.domain.agora.vote.api;

import com.attica.athens.domain.agora.vote.application.AgoraVoteService;
import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;
import com.attica.athens.domain.agora.vote.dto.response.AgoraVoteResponse;
import com.attica.athens.domain.agora.vote.dto.response.AgoraVoteResultResponse;
import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import com.attica.athens.global.auth.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PatchMapping("/{agora-id}/vote")
    public ResponseEntity<ApiResponse<?>> vote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("agora-id") Long agoraId,
            @RequestBody AgoraVoteRequest agoraVoteRequest
    ) {

        AgoraVoteResponse agoraVoteProsResponse = agoraVoteService.vote(userDetails.getUserId(), agoraVoteRequest,
                agoraId);

        return ResponseEntity.ok(ApiUtil.success(agoraVoteProsResponse));
    }

    @GetMapping("/{agora-id}/voteResult")
    public ResponseEntity<ApiResponse<?>> voteResult(
            @PathVariable("agora-id") Long agoraId
    ) {
        AgoraVoteResultResponse agoraVoteResultResponse = agoraVoteService.voteResult(agoraId);

        return ResponseEntity.ok(ApiUtil.success(agoraVoteResultResponse));
    }

}
