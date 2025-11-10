package edu.citadel.main;

import edu.citadel.api.CustomHealthController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomHealthControllerTest {

    private HealthEndpoint healthEndpoint;
    private MetricsEndpoint metricsEndpoint;
    private CustomHealthController customHealthController;

    @BeforeEach
    void setUp() {
        healthEndpoint = mock(HealthEndpoint.class);
        metricsEndpoint = mock(MetricsEndpoint.class);
        customHealthController = new CustomHealthController(healthEndpoint, metricsEndpoint);
    }

    @Test
    void testGetCustomHealth_success() {
        // Arrange
        Health health = Health.up().build();
        when(healthEndpoint.health()).thenReturn(health);

        // Mock metric responses - using MetricDescriptor which is the actual return type
        MetricsEndpoint.MetricDescriptor cpuUsageResponse = createMetricDescriptor("system.cpu.usage", 0.45);
        MetricsEndpoint.MetricDescriptor processCpuResponse = createMetricDescriptor("process.cpu.usage", 0.32);
        MetricsEndpoint.MetricDescriptor memoryResponse = createMetricDescriptor("jvm.memory.used", 524288000.0);
        MetricsEndpoint.MetricDescriptor requestsResponse = createMetricDescriptor("http.server.requests", 150.0);

        when(metricsEndpoint.metric(eq("system.cpu.usage"), eq(null))).thenReturn(cpuUsageResponse);
        when(metricsEndpoint.metric(eq("process.cpu.usage"), eq(null))).thenReturn(processCpuResponse);
        when(metricsEndpoint.metric(eq("jvm.memory.used"), eq(null))).thenReturn(memoryResponse);
        when(metricsEndpoint.metric(eq("http.server.requests"), eq(null))).thenReturn(requestsResponse);

        // Act
        Map<String, Object> result = customHealthController.getCustomHealth();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("health"));
        assertTrue(result.containsKey("metrics"));

        // Verify health data
        assertEquals(health, result.get("health"));

        // Verify metrics data
        @SuppressWarnings("unchecked")
        Map<String, Object> metrics = (Map<String, Object>) result.get("metrics");
        assertNotNull(metrics);
        assertEquals(4, metrics.size());
        assertEquals(cpuUsageResponse, metrics.get("system.cpu.usage"));
        assertEquals(processCpuResponse, metrics.get("process.cpu.usage"));
        assertEquals(memoryResponse, metrics.get("jvm.memory.used"));
        assertEquals(requestsResponse, metrics.get("http.server.requests"));
    }

    @Test
    void testGetCustomHealth_withDownStatus() {
        // Arrange
        Health health = Health.down().withDetail("error", "Database connection failed").build();
        when(healthEndpoint.health()).thenReturn(health);

        // Mock metric responses
        MetricsEndpoint.MetricDescriptor cpuUsageResponse = createMetricDescriptor("system.cpu.usage", 0.75);
        MetricsEndpoint.MetricDescriptor processCpuResponse = createMetricDescriptor("process.cpu.usage", 0.60);
        MetricsEndpoint.MetricDescriptor memoryResponse = createMetricDescriptor("jvm.memory.used", 1048576000.0);
        MetricsEndpoint.MetricDescriptor requestsResponse = createMetricDescriptor("http.server.requests", 500.0);

        when(metricsEndpoint.metric(eq("system.cpu.usage"), eq(null))).thenReturn(cpuUsageResponse);
        when(metricsEndpoint.metric(eq("process.cpu.usage"), eq(null))).thenReturn(processCpuResponse);
        when(metricsEndpoint.metric(eq("jvm.memory.used"), eq(null))).thenReturn(memoryResponse);
        when(metricsEndpoint.metric(eq("http.server.requests"), eq(null))).thenReturn(requestsResponse);

        // Act
        Map<String, Object> result = customHealthController.getCustomHealth();

        // Assert
        assertNotNull(result);
        Health resultHealth = (Health) result.get("health");
        assertEquals(Status.DOWN, resultHealth.getStatus());
        assertTrue(resultHealth.getDetails().containsKey("error"));
    }

    @Test
    void testGetCustomHealth_withNullMetrics() {
        // Arrange
        Health health = Health.up().build();
        when(healthEndpoint.health()).thenReturn(health);

        // Mock some metrics as null (not available)
        when(metricsEndpoint.metric(eq("system.cpu.usage"), eq(null))).thenReturn(null);
        when(metricsEndpoint.metric(eq("process.cpu.usage"), eq(null))).thenReturn(null);
        when(metricsEndpoint.metric(eq("jvm.memory.used"), eq(null))).thenReturn(null);
        when(metricsEndpoint.metric(eq("http.server.requests"), eq(null))).thenReturn(null);

        // Act
        Map<String, Object> result = customHealthController.getCustomHealth();

        // Assert
        assertNotNull(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> metrics = (Map<String, Object>) result.get("metrics");
        assertNotNull(metrics);
        assertEquals(4, metrics.size());
        assertNull(metrics.get("system.cpu.usage"));
        assertNull(metrics.get("process.cpu.usage"));
        assertNull(metrics.get("jvm.memory.used"));
        assertNull(metrics.get("http.server.requests"));
    }

    @Test
    void testGetCustomHealth_returnType() {
        // Arrange
        Health health = Health.up().build();
        when(healthEndpoint.health()).thenReturn(health);

        MetricsEndpoint.MetricDescriptor cpuUsageResponse = createMetricDescriptor("system.cpu.usage", 0.25);
        MetricsEndpoint.MetricDescriptor processCpuResponse = createMetricDescriptor("process.cpu.usage", 0.15);
        MetricsEndpoint.MetricDescriptor memoryResponse = createMetricDescriptor("jvm.memory.used", 262144000.0);
        MetricsEndpoint.MetricDescriptor requestsResponse = createMetricDescriptor("http.server.requests", 100.0);

        when(metricsEndpoint.metric(eq("system.cpu.usage"), eq(null))).thenReturn(cpuUsageResponse);
        when(metricsEndpoint.metric(eq("process.cpu.usage"), eq(null))).thenReturn(processCpuResponse);
        when(metricsEndpoint.metric(eq("jvm.memory.used"), eq(null))).thenReturn(memoryResponse);
        when(metricsEndpoint.metric(eq("http.server.requests"), eq(null))).thenReturn(requestsResponse);

        // Act
        Map<String, Object> result = customHealthController.getCustomHealth();

        // Assert - verify the structure matches expected JSON format
        assertNotNull(result);
        assertTrue(result.get("health") instanceof Health);
        assertTrue(result.get("metrics") instanceof Map);
    }

    @Test
    void testConstructor() {
        // Test that constructor properly initializes dependencies
        CustomHealthController controller = new CustomHealthController(healthEndpoint, metricsEndpoint);
        assertNotNull(controller);
    }

    /**
     * Helper method to create a MetricDescriptor mock
     */
    private MetricsEndpoint.MetricDescriptor createMetricDescriptor(String name, Double value) {
        MetricsEndpoint.MetricDescriptor descriptor = mock(MetricsEndpoint.MetricDescriptor.class);
        when(descriptor.getName()).thenReturn(name);

        MetricsEndpoint.Sample sample = mock(MetricsEndpoint.Sample.class);
        when(sample.getValue()).thenReturn(value);

        when(descriptor.getMeasurements()).thenReturn(Collections.singletonList(sample));

        return descriptor;
    }
}
