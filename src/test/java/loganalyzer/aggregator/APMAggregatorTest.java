package loganalyzer.aggregator;

import com.fasterxml.jackson.databind.JsonNode;
import loganalyzer.model.APMLogEntry;
import org.junit.Test;
import java.time.LocalDateTime;
import static org.junit.Assert.*;

public class APMAggregatorTest {
    private final APMAggregator aggregator = new APMAggregator();
    private final LocalDateTime now = LocalDateTime.now();

    @Test
    public void testAggregation() {
        // Add some test data
        aggregator.aggregate(new APMLogEntry(now, "host1", "cpu_usage_percent", 60.0));
        aggregator.aggregate(new APMLogEntry(now, "host1", "cpu_usage_percent", 90.0));
        aggregator.aggregate(new APMLogEntry(now, "host1", "cpu_usage_percent", 75.0));

        JsonNode result = aggregator.getAggregationResult();
        JsonNode cpuMetrics = result.get("cpu_usage_percent");

        assertNotNull("CPU metrics should exist", cpuMetrics);
        assertEquals("Minimum should match", 60.0, cpuMetrics.get("minimum").asDouble(), 0.001);
        assertEquals("Maximum should match", 90.0, cpuMetrics.get("maximum").asDouble(), 0.001);
        assertEquals("Average should match", 75.0, cpuMetrics.get("average").asDouble(), 0.001);
        assertEquals("Median should match", 75.0, cpuMetrics.get("median").asDouble(), 0.001);
    }

    @Test
    public void testMultipleMetrics() {
        aggregator.aggregate(new APMLogEntry(now, "host1", "cpu_usage_percent", 60.0));
        aggregator.aggregate(new APMLogEntry(now, "host1", "memory_usage_percent", 80.0));

        JsonNode result = aggregator.getAggregationResult();
        assertTrue("Should contain CPU metrics", result.has("cpu_usage_percent"));
        assertTrue("Should contain memory metrics", result.has("memory_usage_percent"));
    }

    @Test
    public void testEmptyAggregation() {
        JsonNode result = aggregator.getAggregationResult();
        assertTrue("Result should be an object", result.isObject());
        assertEquals("Result should be empty", 0, result.size());
    }
} 