package com.attica.athens.domain.agora.api;

import com.attica.athens.domain.agora.application.AgoraService;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.request.AgoraRequest;
import com.attica.athens.domain.agora.dto.request.SearchKeywordRequest;
import com.attica.athens.domain.agora.dto.response.AgoraIdResponse;
import com.attica.athens.domain.agora.dto.response.AgoraSlice;
import com.attica.athens.domain.agora.dto.response.AgoraTitleResponse;
import com.attica.athens.domain.agora.dto.response.EndAgoraResponse;
import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
            @Valid AgoraRequest request
    ) {
        AgoraSlice<?> response = agoraService.findAgoraByCategory(request);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @GetMapping(params = {"status", "agora-name", "next"})
    public ResponseEntity<ApiResponse<?>> getAgoraByKeyword(
            @RequestParam("agora-name") String agoraName,
            @Valid SearchKeywordRequest request
    ) {
        AgoraSlice<?> response = agoraService.findAgoraByKeyword(agoraName, request);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @GetMapping("/{agoraId}/title")
    public ResponseEntity<ApiResponse<AgoraTitleResponse>> getAgoraTitle(
            @PathVariable("agoraId") Long agoraId
    ) {
        AgoraTitleResponse response = agoraService.getAgoraTitleAndImageUrl(agoraId);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @GetMapping("/ids")
    public ResponseEntity<ApiResponse<AgoraIdResponse>> getAgoraIdList() {

        AgoraIdResponse response = agoraService.getAgoraIdList();

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @PatchMapping("/{agoraId}/time-out")
    public ResponseEntity<ApiResponse<EndAgoraResponse>> timeOutAgora(
            @PathVariable("agoraId") Long agoraId
    ) {
        EndAgoraResponse response = agoraService.timeOutAgora(agoraId);

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SimpleAgoraResult>>> getTrendAgora() {
        List<SimpleAgoraResult> response = agoraService.findTrendAgora();

        return ResponseEntity.ok(ApiUtil.success(response));
    }
}
