package com.attica.athens.domain.agora.api;

import com.attica.athens.domain.agora.application.AgoraService;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.request.SearchCategoryRequest;
import com.attica.athens.domain.agora.dto.request.SearchKeywordRequest;
import com.attica.athens.domain.agora.dto.response.AgoraIdResponse;
import com.attica.athens.domain.agora.dto.response.AgoraSlice;
import com.attica.athens.domain.agora.dto.response.AgoraTitleResponse;
import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/open/agoras")
@RequiredArgsConstructor
public class AgoraOpenController {

    private final AgoraService agoraService;

    @GetMapping(params = {"status", "category", "next"})
    public ResponseEntity<ApiResponse<?>> getAgoraByCategory(
            @Valid SearchCategoryRequest request
    ) {
        AgoraSlice<SimpleAgoraResult> response = agoraService.findAgoraByCategory(request);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @GetMapping(params = {"agora-name", "status", "next"})
    public ResponseEntity<ApiResponse<?>> getAgoraByKeyword(
            @RequestParam("agora-name") String agoraName,
            @Valid SearchKeywordRequest request
    ) {
        AgoraSlice<SimpleAgoraResult> response =
                agoraService.findAgoraByKeyword(agoraName, new SearchKeywordRequest(request.status(), request.next()));

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @GetMapping("/{agora-id}/title")
    public ResponseEntity<ApiResponse<?>> getAgoraTitle(
            @PathVariable("agora-id") Long agoraId
    ) {
        AgoraTitleResponse agoraTitle = agoraService.getAgoraTitle(agoraId);

        return ResponseEntity.ok(ApiUtil.success(agoraTitle));
    }

    @GetMapping("/ids")
    public ResponseEntity<ApiResponse<?>> getAgoraIdList() {

        AgoraIdResponse agoraIdList = agoraService.getAgoraIdList();

        return ResponseEntity.ok(ApiUtil.success(agoraIdList));
    }
}