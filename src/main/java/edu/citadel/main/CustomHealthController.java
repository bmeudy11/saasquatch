package edu.citadel.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

//reconfigured the "health" endpoint with metrics
@RestController
public class CustomHealthController {

    private final HealthEndpoint healthEndpoint;
    private final MetricsEndpoint metricsEndpoint;

    @Autowired
    public CustomHealthController(HealthEndpoint healthEndpoint, MetricsEndpoint metricsEndpoint) {
        this.healthEndpoint = healthEndpoint;
        this.metricsEndpoint = metricsEndpoint;
    }

    @GetMapping(value = "status/health", produces = "application/json")
    public Map<String, Object> getCustomHealth() {
        Map<String, Object> combined = new HashMap<>();

        // Add health data
        combined.put("health", healthEndpoint.health());

        // Add selected metrics
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("system.cpu.usage", metricsEndpoint.metric("system.cpu.usage", null));
        metrics.put("process.cpu.usage", metricsEndpoint.metric("process.cpu.usage", null));
        metrics.put("jvm.memory.used", metricsEndpoint.metric("jvm.memory.used", null));
        metrics.put("http.server.requests", metricsEndpoint.metric("http.server.requests", null));

        combined.put("metrics", metrics);
        return combined;
    }
}
