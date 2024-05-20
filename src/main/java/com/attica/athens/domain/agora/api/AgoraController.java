package com.attica.athens.domain.agora.api;

import com.attica.athens.domain.agora.application.AgoraService;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.request.AgoraCreateRequest;
import com.attica.athens.domain.agora.dto.request.SearchCategoryRequest;
import com.attica.athens.domain.agora.dto.request.SearchKeywordRequest;
import com.attica.athens.domain.agora.dto.response.AgoraResponse;
import com.attica.athens.domain.agora.dto.response.AgoraSlice;
import com.attica.athens.domain.agora.dto.response.CreateAgoraResponse;
import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agoras")
public class AgoraController {

    private final AgoraService agoraService;

    public AgoraController(AgoraService agoraService) {
        this.agoraService = agoraService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createAgora(
        @RequestBody @Valid AgoraCreateRequest request
    ) {
        CreateAgoraResponse result = agoraService.create(request);

        AgoraResponse<CreateAgoraResponse> response = new AgoraResponse<>(result);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @GetMapping(params = {"status", "category", "next"})
    public ResponseEntity<ApiResponse<?>> getAgoraByCategory(
        @Valid SearchCategoryRequest request
    ) {
        AgoraSlice<SimpleAgoraResult> result = agoraService.findAgoraByCategory(request);

        AgoraResponse<AgoraSlice<SimpleAgoraResult>> response = new AgoraResponse<>(result);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @GetMapping(params = {"agora_name", "status", "next"})
    public ResponseEntity<ApiResponse<?>> getAgoraByKeyword(
        @RequestParam("agora_name") String agoraName,
        @Valid SearchKeywordRequest request
    ) {
        AgoraSlice<SimpleAgoraResult> result =
            agoraService.findAgoraByKeyword(agoraName, new SearchKeywordRequest(request.status(), request.next()));

        AgoraResponse<AgoraSlice<SimpleAgoraResult>> response = new AgoraResponse<>(result);

        return ResponseEntity.ok(ApiUtil.success(response));
    }
}