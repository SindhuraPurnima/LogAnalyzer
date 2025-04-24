package loganalyzer.aggregator;

import com.fasterxml.jackson.databind.JsonNode;
import loganalyzer.model.RequestLogEntry;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
import static org.junit.Assert.*;

public class RequestLogAggregatorTest {
    private RequestLogAggregator aggregator;
    private final LocalDateTime now = LocalDateTime.now();

    @Before
    public void setUp() {
        aggregator = new RequestLogAggregator();
    }

    @Test
    public void testSingleRouteAggregation() {
        // Add test data for a single route
        aggregator.aggregate(new RequestLogEntry(now, "server1", "GET", "/api/status", 200, 100));
        aggregator.aggregate(new RequestLogEntry(now, "server1", "GET", "/api/status", 200, 150));
        aggregator.aggregate(new RequestLogEntry(now, "server1", "GET", "/api/status", 404, 120));
        aggregator.aggregate(new RequestLogEntry(now, "server1", "GET", "/api/status", 500, 300));

        JsonNode result = aggregator.getAggregationResult();
        JsonNode routeStats = result.get("/api/status");
        
        assertNotNull("Route statistics should exist", routeStats);
        
        // Test response times
        JsonNode responseTimes = routeStats.get("response_times");
        assertEquals("Minimum response time should match", 100, responseTimes.get("min").asLong());
        assertEquals("Maximum response time should match", 300, responseTimes.get("max").asLong());
        assertEquals("50th percentile should match", 120, responseTimes.get("50_percentile").asLong());
        assertEquals("90th percentile should match", 300, responseTimes.get("90_percentile").asLong());
        assertEquals("95th percentile should match", 300, responseTimes.get("95_percentile").asLong());
        assertEquals("99th percentile should match", 300, responseTimes.get("99_percentile").asLong());

        // Test status codes
        JsonNode statusCodes = routeStats.get("status_codes");
        assertEquals("2XX count should match", 2, statusCodes.get("2XX").asInt());
        assertEquals("4XX count should match", 1, statusCodes.get("4XX").asInt());
        assertEquals("5XX count should match", 1, statusCodes.get("5XX").asInt());
    }

    @Test
    public void testMultipleRoutesAggregation() {
        // Add test data for multiple routes
        aggregator.aggregate(new RequestLogEntry(now, "server1", "GET", "/api/users", 200, 50));
        aggregator.aggregate(new RequestLogEntry(now, "server1", "POST", "/api/users", 201, 150));
        aggregator.aggregate(new RequestLogEntry(now, "server1", "GET", "/api/products", 200, 75));
        aggregator.aggregate(new RequestLogEntry(now, "server1", "GET", "/api/products", 500, 250));

        JsonNode result = aggregator.getAggregationResult();
        
        // Test /api/users route
        JsonNode usersStats = result.get("/api/users");
        assertNotNull("Users route statistics should exist", usersStats);
        assertEquals("Users route should have 2 2XX responses", 
                    2, usersStats.get("status_codes").get("2XX").asInt());

        // Test /api/products route
        JsonNode productsStats = result.get("/api/products");
        assertNotNull("Products route statistics should exist", productsStats);
        assertEquals("Products route should have 1 2XX response", 
                    1, productsStats.get("status_codes").get("2XX").asInt());
        assertEquals("Products route should have 1 5XX response", 
                    1, productsStats.get("status_codes").get("5XX").asInt());
    }

    @Test
    public void testEmptyAggregation() {
        JsonNode result = aggregator.getAggregationResult();
        assertTrue("Result should be an empty object", result.isEmpty());
    }

    @Test
    public void testOutputFileName() {
        assertEquals("Output file name should be request.json", 
                    "request.json", aggregator.getOutputFileName());
    }
} 