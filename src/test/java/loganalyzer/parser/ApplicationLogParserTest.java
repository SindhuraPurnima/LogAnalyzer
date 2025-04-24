package loganalyzer.parser;

import loganalyzer.model.ApplicationLogEntry;
import org.junit.Test;
import static org.junit.Assert.*;

public class ApplicationLogParserTest {
    private final ApplicationLogParser parser = new ApplicationLogParser();
    private final String validLog = "timestamp=2024-02-24T16:22:20Z level=INFO message=\"Scheduled maintenance starting\" host=webserver1";
    private final String invalidLog = "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72";

    @Test
    public void testCanParse() {
        assertTrue("Should parse valid Application log", parser.canParse(validLog));
        assertFalse("Should not parse invalid Application log", parser.canParse(invalidLog));
    }

    @Test
    public void testParse() {
        ApplicationLogEntry entry = (ApplicationLogEntry) parser.parse(validLog);
        assertNotNull("Parsed entry should not be null", entry);
        assertEquals("Level should match", "INFO", entry.getLevel());
        assertEquals("Message should match", "Scheduled maintenance starting", entry.getMessage());
        assertEquals("Host should match", "webserver1", entry.getHost());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidLog() {
        parser.parse(invalidLog);
    }
} 