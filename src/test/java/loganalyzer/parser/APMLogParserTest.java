package loganalyzer.parser;

import loganalyzer.model.APMLogEntry;
import org.junit.Test;
import static org.junit.Assert.*;

public class APMLogParserTest {
    private final APMLogParser parser = new APMLogParser();
    private final String validLog = "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72";
    private final String invalidLog = "timestamp=2024-02-24T16:22:15Z level=INFO message=\"test\" host=webserver1";

    @Test
    public void testCanParse() {
        assertTrue("Should parse valid APM log", parser.canParse(validLog));
        assertFalse("Should not parse invalid APM log", parser.canParse(invalidLog));
    }

    @Test
    public void testParse() {
        APMLogEntry entry = (APMLogEntry) parser.parse(validLog);
        assertNotNull("Parsed entry should not be null", entry);
        assertEquals("Metric should match", "cpu_usage_percent", entry.getMetric());
        assertEquals("Value should match", 72.0, entry.getValue(), 0.001);
        assertEquals("Host should match", "webserver1", entry.getHost());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidLog() {
        parser.parse(invalidLog);
    }
} 