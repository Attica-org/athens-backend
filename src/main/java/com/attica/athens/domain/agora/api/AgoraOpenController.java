package com.attica.athens.domain.agora.api;

import com.attica.athens.domain.agora.application.AgoraService;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.request.AgoraCreateRequest;
import com.attica.athens.domain.agora.dto.request.AgoraParticipateRequest;
import com.attica.athens.domain.agora.dto.request.SearchCategoryRequest;
import com.attica.athens.domain.agora.dto.request.SearchKeywordRequest;
import com.attica.athens.domain.agora.dto.response.AgoraParticipateResponse;
import com.attica.athens.domain.agora.dto.response.AgoraSlice;
import com.attica.athens.domain.agora.dto.response.CreateAgoraResponse;
import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/open/agoras")
public class AgoraOpenController {

    private final AgoraService agoraService;

    public AgoraOpenController(AgoraService agoraService) {
        this.agoraService = agoraService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createAgora(
            @RequestBody @Valid AgoraCreateRequest request
    ) {
        CreateAgoraResponse response = agoraService.create(request);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @PostMapping("/{agora-id}/participants")
    public ResponseEntity<ApiResponse<?>> participateAgora(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable("agora-id") Long agoraId,
            @RequestBody @Valid AgoraParticipateRequest request
    ) {
        Long userId = Long.parseLong(user.getUsername());
        AgoraParticipateResponse response = agoraService.participate(userId, agoraId, request);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @GetMapping(params = {"status", "category", "next"})
    public ResponseEntity<ApiResponse<?>> getAgoraByCategory(
            @Valid SearchCategoryRequest request
    ) {
        AgoraSlice<SimpleAgoraResult> response = agoraService.findAgoraByCategory(request);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @GetMapping(params = {"agora_name", "status", "next"})
    public ResponseEntity<ApiResponse<?>> getAgoraByKeyword(
            @RequestParam("agora_name") String agoraName,
            @Valid SearchKeywordRequest request
    ) {
        AgoraSlice<SimpleAgoraResult> response =
                agoraService.findAgoraByKeyword(agoraName, new SearchKeywordRequest(request.status(), request.next()));

        return ResponseEntity.ok(ApiUtil.success(response));
    }
}