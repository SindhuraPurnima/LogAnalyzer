package loganalyzer.parser;

import loganalyzer.model.RequestLogEntry;
import org.junit.Test;
import static org.junit.Assert.*;

public class RequestLogParserTest {
    private final RequestLogParser parser = new RequestLogParser();
    private final String validLog = "timestamp=2024-02-24T16:22:25Z request_method=POST request_url=\"/api/update\" response_status=202 response_time_ms=200 host=webserver1";
    private final String invalidLog = "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72";

    @Test
    public void testCanParse() {
        assertTrue("Should parse valid Request log", parser.canParse(validLog));
        assertFalse("Should not parse invalid Request log", parser.canParse(invalidLog));
    }

    @Test
    public void testParse() {
        RequestLogEntry entry = (RequestLogEntry) parser.parse(validLog);
        assertNotNull("Parsed entry should not be null", entry);
        assertEquals("Method should match", "POST", entry.getRequestMethod());
        assertEquals("URL should match", "/api/update", entry.getRequestUrl());
        assertEquals("Status should match", 202, entry.getResponseStatus());
        assertEquals("Response time should match", 200, entry.getResponseTimeMs());
        assertEquals("Host should match", "webserver1", entry.getHost());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidLog() {
        parser.parse(invalidLog);
    }
} 