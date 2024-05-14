package com.attica.athens.domain.agora.api;

import com.attica.athens.domain.agora.application.AgoraService;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.dto.AgoraSlice;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.request.AgoraCreateRequest;
import com.attica.athens.domain.agora.dto.request.SearchCategoryRequest;
import com.attica.athens.domain.agora.dto.request.SearchKeywordRequest;
import com.attica.athens.domain.agora.dto.response.AgoraCreateResponse;
import com.attica.athens.domain.agora.dto.response.AgoraResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agoras")
public class AgoraController {

    private final AgoraService agoraService;

    public AgoraController(AgoraService agoraService) {
        this.agoraService = agoraService;
    }

    @PostMapping
    public ResponseEntity<AgoraResponse<AgoraCreateResponse>> createAgora(
        @RequestBody AgoraCreateRequest request
    ) {
        Agora agora = agoraService.create(request);
        AgoraCreateResponse response = new AgoraCreateResponse(agora.getId());

        return ResponseEntity.ok(
                AgoraResponse.<AgoraCreateResponse>builder()
                    .success(true)
                    .response(response)
                    .build()
            );
    }

    @GetMapping(params = {"status", "category", "next"})
    public ResponseEntity<AgoraResponse<AgoraSlice<SimpleAgoraResult>>> getAgoraByCategory(
        SearchCategoryRequest request
    ) {
        AgoraSlice<SimpleAgoraResult> response = agoraService.findAgoraByCategory(request);

        return ResponseEntity.ok(
            AgoraResponse.<AgoraSlice<SimpleAgoraResult>>builder()
                .success(true)
                .response(response)
                .build()
        );
    }

    @GetMapping(params = {"agora_name", "status", "next"})
    public ResponseEntity<AgoraResponse<AgoraSlice<SimpleAgoraResult>>> getAgoraByKeyword(
        SearchKeywordRequest request
    ) {
        AgoraSlice<SimpleAgoraResult> response = agoraService.findAgoraByKeyword(request);

        return ResponseEntity.ok(
            AgoraResponse.<AgoraSlice<SimpleAgoraResult>>builder()
                .success(true)
                .response(response)
                .build()
        );
    }
}