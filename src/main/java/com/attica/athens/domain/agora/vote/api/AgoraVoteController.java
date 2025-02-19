package com.attica.athens.domain.agora.vote.api;

import com.attica.athens.domain.agora.vote.application.AgoraVoteService;
import com.attica.athens.domain.agora.vote.dto.request.AgoraVoteRequest;
import com.attica.athens.domain.agora.vote.dto.request.KickVoteRequest;
import com.attica.athens.domain.agora.vote.dto.response.AgoraVoteResponse;
import com.attica.athens.domain.agora.vote.dto.response.AgoraVoteResultResponse;
import com.attica.athens.domain.agora.vote.dto.response.KickVoteResult;
import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import com.attica.athens.global.auth.domain.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/agoras")
public class AgoraVoteController {

    private final AgoraVoteService agoraVoteService;

    @PatchMapping("/{agoraId}/vote")
    public ResponseEntity<ApiResponse<?>> vote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("agoraId") Long agoraId,
            @RequestBody AgoraVoteRequest agoraVoteRequest
    ) {

        AgoraVoteResponse response = agoraVoteService.vote(userDetails.getUserId(), agoraVoteRequest,
                agoraId);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @GetMapping("/{agoraId}/results")
    public ResponseEntity<ApiResponse<AgoraVoteResultResponse>> voteResult(
            @PathVariable("agoraId") Long agoraId
    ) {
        AgoraVoteResultResponse agoraVoteResultResponse = agoraVoteService.voteResult(agoraId);

        return ResponseEntity.ok(ApiUtil.success(agoraVoteResultResponse));
    }

    @PostMapping("/{agoraId}/kick-vote")
    public ResponseEntity<ApiResponse<?>> kickVote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("agoraId") Long agoraId,
            @RequestBody KickVoteRequest request
    ) {
        Long memberId = userDetails.getUserId();
        KickVoteResult response = agoraVoteService.kickVote(agoraId, memberId, request);

        return ResponseEntity.ok(ApiUtil.success(response.getMessage()));
    }
}
