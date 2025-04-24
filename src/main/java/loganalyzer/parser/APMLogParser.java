package loganalyzer.parser;

import loganalyzer.model.APMLogEntry;
import loganalyzer.model.LogEntry;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APMLogParser implements LogParser {
    private static final Pattern LOG_PATTERN = Pattern.compile(
        "timestamp=(.*?)Z metric=(.*?) host=(.*?) value=([0-9.]+)"
    );

    @Override
    public boolean canParse(String logLine) {
        return logLine.contains("metric=") && logLine.contains("value=");
    }

    @Override
    public LogEntry parse(String logLine) {
        Matcher matcher = LOG_PATTERN.matcher(logLine);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid APM log format: " + logLine);
        }

        LocalDateTime timestamp = LocalDateTime.parse(matcher.group(1));
        String metric = matcher.group(2);
        String host = matcher.group(3);
        double value = Double.parseDouble(matcher.group(4));

        return new APMLogEntry(timestamp, host, metric, value);
    }
}
