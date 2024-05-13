package com.attica.athens.domain.agora.api;

import com.attica.athens.domain.agora.application.AgoraService;
import com.attica.athens.domain.agora.domain.Agora;
import com.attica.athens.domain.agora.dto.request.AgoraCreateRequest;
import com.attica.athens.domain.agora.dto.response.AgoraCreateResponse;
import com.attica.athens.domain.agora.dto.response.AgoraResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/agoras")
public class AgoraController {

    private final AgoraService agoraService;

    public AgoraController(AgoraService agoraService) {
        this.agoraService = agoraService;
    }

    @PostMapping
    public ResponseEntity<AgoraResponse<AgoraCreateResponse>> createAgora(
        @RequestBody AgoraCreateRequest requestDto
    ) {
        Agora agora = agoraService.create(requestDto);
        AgoraCreateResponse response = new AgoraCreateResponse(agora.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(AgoraResponse.<AgoraCreateResponse>builder()
                .success(true)
                .response(response)
                .build());
    }
}