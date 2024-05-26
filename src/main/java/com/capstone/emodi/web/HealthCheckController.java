package com.capstone.emodi.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {

    private final DataSourceHealthIndicator dataSourceHealthIndicator;

    @Autowired
    public HealthCheckController(DataSource dataSource) {
        this.dataSourceHealthIndicator = new DataSourceHealthIndicator(dataSource);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> healthStatus = new HashMap<>();

        // 데이터베이스 연결 확인
        Health databaseHealth = dataSourceHealthIndicator.health();
        healthStatus.put("database", databaseHealth.getStatus().getCode());

        // 전체 상태 평가
        boolean isHealthy = databaseHealth.getStatus().getCode().equals("UP");
        // 추가 상태 확인 로직에 따라 isHealthy 변수 값을 업데이트할 수 있습니다.

        if (isHealthy) {
            healthStatus.put("status", "UP");
            return ResponseEntity.ok(healthStatus);
        } else {
            healthStatus.put("status", "DOWN");
            return ResponseEntity.status(503).body(healthStatus);
        }
    }
}
