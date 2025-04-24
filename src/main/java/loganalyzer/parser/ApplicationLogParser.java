package loganalyzer.parser;

import loganalyzer.model.ApplicationLogEntry;
import loganalyzer.model.LogEntry;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationLogParser implements LogParser {
    private static final Pattern LOG_PATTERN = Pattern.compile(
        "timestamp=(.*?)Z level=(.*?) message=\"(.*?)\" host=(.*?)(\\s|$)"
    );

    @Override
    public boolean canParse(String logLine) {
        return logLine.contains("level=") && logLine.contains("message=");
    }

    @Override
    public LogEntry parse(String logLine) {
        Matcher matcher = LOG_PATTERN.matcher(logLine);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid Application log format: " + logLine);
        }

        LocalDateTime timestamp = LocalDateTime.parse(matcher.group(1));
        String level = matcher.group(2);
        String message = matcher.group(3);
        String host = matcher.group(4);

        return new ApplicationLogEntry(timestamp, host, level, message);
    }
}
