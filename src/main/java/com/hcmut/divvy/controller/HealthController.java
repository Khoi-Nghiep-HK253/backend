package com.hcmut.divvy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@Tag(name = "Health Check", description = "Endpoints for server liveness and health monitoring")
@RestController
public class HealthController {

    @Operation(summary = "Check server health status")
    @GetMapping({"/health", "/api/health"})
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "divvy-backend",
                "timestamp", Instant.now().toString()
        ));
    }
}