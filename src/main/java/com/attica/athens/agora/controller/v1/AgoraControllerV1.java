package com.attica.athens.agora.controller.v1;

import com.attica.athens.agora.domain.Agora;
import com.attica.athens.agora.dto.AgoraResponse;
import com.attica.athens.agora.dto.CreateAgoraRequestDto;
import com.attica.athens.agora.dto.CreateAgoraResponseDto;
import com.attica.athens.agora.service.v1.AgoraServiceV1;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/agoras")
public class AgoraControllerV1 {

    private final AgoraServiceV1 agoraServiceV1;

    public AgoraControllerV1(AgoraServiceV1 agoraServiceV1) {
        this.agoraServiceV1 = agoraServiceV1;
    }

    @PostMapping
    public ResponseEntity<AgoraResponse<CreateAgoraResponseDto>> createAgora(
        @RequestBody CreateAgoraRequestDto requestDto
    ) {
        Agora agora = agoraServiceV1.create(requestDto);
        CreateAgoraResponseDto response = new CreateAgoraResponseDto(agora.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(AgoraResponse.<CreateAgoraResponseDto>builder()
                .success(true)
                .response(response)
                .build());
    }
}
