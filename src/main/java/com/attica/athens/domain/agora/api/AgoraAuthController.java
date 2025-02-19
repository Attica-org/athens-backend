package com.attica.athens.domain.agora.api;

import com.attica.athens.domain.agora.application.AgoraService;
import com.attica.athens.domain.agora.dto.request.AgoraCreateRequest;
import com.attica.athens.domain.agora.dto.request.AgoraParticipateRequest;
import com.attica.athens.domain.agora.dto.response.AgoraExitResponse;
import com.attica.athens.domain.agora.dto.response.AgoraParticipateResponse;
import com.attica.athens.domain.agora.dto.response.ClosedAgoraParticipateResponse;
import com.attica.athens.domain.agora.dto.response.CreateAgoraResponse;
import com.attica.athens.domain.agora.dto.response.EndVoteAgoraResponse;
import com.attica.athens.domain.agora.dto.response.StartAgoraResponse;
import com.attica.athens.domain.agora.dto.response.UpdateThumbnailResponse;
import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import com.attica.athens.global.auth.domain.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/auth/agoras")
@RequiredArgsConstructor
public class AgoraAuthController {

    private final AgoraService agoraService;

    @PatchMapping("/{agoraId}/start")
    public ResponseEntity<ApiResponse<StartAgoraResponse>> startAgora(@PathVariable("agoraId") Long agoraId,
                                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        StartAgoraResponse response = agoraService.startAgora(agoraId, userDetails.getUserId());

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @PatchMapping("/{agoraId}/close")
    public ResponseEntity<ApiResponse<EndVoteAgoraResponse>> endVoteAgora(@PathVariable("agoraId") Long agoraId,
                                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        EndVoteAgoraResponse response = agoraService.endVoteAgora(agoraId, userDetails.getUserId());

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @PostMapping("/{agoraId}/participants")
    public ResponseEntity<ApiResponse<AgoraParticipateResponse>> participateAgora(
            @PathVariable("agoraId") Long agoraId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid AgoraParticipateRequest request
    ) {
        Long memberId = Long.parseLong(user.getUsername());
        AgoraParticipateResponse response = agoraService.participate(memberId, agoraId, request);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @GetMapping("/{agoraId}/participants")
    public ResponseEntity<ApiResponse<ClosedAgoraParticipateResponse>> participateAgora(
            @PathVariable("agoraId") Long agoraId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long memberId = Long.parseLong(user.getUsername());
        ClosedAgoraParticipateResponse response = agoraService.closedAgoraParticipate(agoraId, memberId);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @PatchMapping("/{agoraId}/exit")
    public ResponseEntity<ApiResponse<AgoraExitResponse>> exitAgora(
            @PathVariable("agoraId") Long agoraId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long memberId = Long.parseLong(user.getUsername());
        AgoraExitResponse response = agoraService.exit(memberId, agoraId);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateAgoraResponse>> createAgora(
            @RequestPart @Valid AgoraCreateRequest request,
            @RequestPart(required = false) MultipartFile file
    ) {
        CreateAgoraResponse response = agoraService.create(request, file);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @PatchMapping("/{agoraId}")
    public ResponseEntity<ApiResponse<UpdateThumbnailResponse>> updateAgoraThumbnail(
            @PathVariable("agoraId") Long agoraId,
            @AuthenticationPrincipal CustomUserDetails user, MultipartFile file
    ) {
        Long memberId = Long.parseLong(user.getUsername());
        UpdateThumbnailResponse response = agoraService.updateAgoraImage(agoraId, memberId, file);

        return ResponseEntity.ok(ApiUtil.success(response));
    }
}
