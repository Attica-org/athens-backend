package com.attica.athens.domain.sample;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/open")
@RestController
public class HealthCheckController {

    @GetMapping("/health-check")
    public ResponseEntity<Void> check() {
        return ResponseEntity.ok().build();
    }
}
