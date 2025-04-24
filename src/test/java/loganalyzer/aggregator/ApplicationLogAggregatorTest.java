package loganalyzer.aggregator;

import com.fasterxml.jackson.databind.JsonNode;
import loganalyzer.model.ApplicationLogEntry;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
import static org.junit.Assert.*;

public class ApplicationLogAggregatorTest {
    private ApplicationLogAggregator aggregator;
    private final LocalDateTime now = LocalDateTime.now();

    @Before
    public void setUp() {
        aggregator = new ApplicationLogAggregator();
    }

    @Test
    public void testAggregation() {
        // Add test data
        aggregator.aggregate(new ApplicationLogEntry(now, "server1", "ERROR", "Failed"));
        aggregator.aggregate(new ApplicationLogEntry(now, "server1", "INFO", "Success"));
        aggregator.aggregate(new ApplicationLogEntry(now, "server1", "ERROR", "Error"));

        JsonNode result = aggregator.getAggregationResult();
        
        // Test log level counts
        assertEquals("Should have 2 ERROR logs", 2, result.get("ERROR").asInt());
        assertEquals("Should have 1 INFO log", 1, result.get("INFO").asInt());
        assertEquals("Should have 0 WARNING logs", 0, result.get("WARNING").asInt());
        assertEquals("Should have 0 DEBUG logs", 0, result.get("DEBUG").asInt());
    }

    @Test
    public void testEmptyAggregation() {
        JsonNode result = aggregator.getAggregationResult();
        
        // Test that all standard log levels are present with zero counts
        assertEquals("Should have 0 ERROR logs", 0, result.get("ERROR").asInt());
        assertEquals("Should have 0 WARNING logs", 0, result.get("WARNING").asInt());
        assertEquals("Should have 0 INFO logs", 0, result.get("INFO").asInt());
        assertEquals("Should have 0 DEBUG logs", 0, result.get("DEBUG").asInt());
    }

    @Test
    public void testOutputFileName() {
        assertEquals("Output file name should be application.json", 
                    "application.json", aggregator.getOutputFileName());
    }
} 