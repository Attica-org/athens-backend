package com.attica.athens.domain.agora.api;

import com.attica.athens.domain.agora.application.AgoraService;
import com.attica.athens.domain.agora.dto.response.EndVoteAgoraResponse;
import com.attica.athens.domain.agora.dto.response.StartAgoraResponse;
import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import com.attica.athens.global.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/agoras")
public class AgoraAuthController {

    private final AgoraService agoraService;

    public AgoraAuthController(AgoraService agoraService) {
        this.agoraService = agoraService;
    }

    @PatchMapping("/{agora-id}/start")
    public ResponseEntity<ApiResponse<StartAgoraResponse>> startAgora(@PathVariable("agora-id") Long agoraId,
                                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        StartAgoraResponse response = agoraService.startAgora(agoraId, userDetails.getUserId());

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @PatchMapping("/{agora-id}/close")
    public ResponseEntity<ApiResponse<EndVoteAgoraResponse>> endVoteAgora(@PathVariable("agora-id") Long agoraId,
                                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        EndVoteAgoraResponse response = agoraService.endVoteAgora(agoraId, userDetails.getUserId());

        return ResponseEntity.ok(ApiUtil.success(response));
    }
}
