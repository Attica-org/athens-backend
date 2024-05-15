package com.attica.athens.domain.agora.api;

import com.attica.athens.domain.agora.application.AgoraService;
import com.attica.athens.domain.agora.domain.AgoraStatus;
import com.attica.athens.domain.agora.dto.SimpleAgoraResult;
import com.attica.athens.domain.agora.dto.request.AgoraCreateRequest;
import com.attica.athens.domain.agora.dto.request.SearchCategoryRequest;
import com.attica.athens.domain.agora.dto.request.SearchKeywordRequest;
import com.attica.athens.domain.agora.dto.response.AgoraResponse;
import com.attica.athens.domain.agora.dto.response.AgoraSlice;
import com.attica.athens.domain.agora.dto.response.CreateAgoraResponse;
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
    public ResponseEntity<AgoraResponse<CreateAgoraResponse>> createAgora(
        @RequestBody AgoraCreateRequest request
    ) {
        CreateAgoraResponse response = agoraService.create(request);

        return ResponseEntity.ok(
                AgoraResponse.<CreateAgoraResponse>builder()
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

    @GetMapping
    public ResponseEntity<AgoraResponse<AgoraSlice<SimpleAgoraResult>>> getAgoraByKeyword(
        @RequestParam(name = "agora_name") String agoraName,
        @RequestParam AgoraStatus status,
        @RequestParam(required = false) Long next
    ) {
        AgoraSlice<SimpleAgoraResult> response =
            agoraService.findAgoraByKeyword(new SearchKeywordRequest(agoraName, status, next));

        return ResponseEntity.ok(
            AgoraResponse.<AgoraSlice<SimpleAgoraResult>>builder()
                .success(true)
                .response(response)
                .build()
        );
    }
}