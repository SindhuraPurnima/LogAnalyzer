package loganalyzer.parser;

import loganalyzer.model.LogEntry;
import loganalyzer.model.RequestLogEntry;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestLogParser implements LogParser {
    private static final Pattern LOG_PATTERN = Pattern.compile(
        "timestamp=(.*?)Z request_method=(.*?) request_url=\"(.*?)\" response_status=(\\d+) response_time_ms=(\\d+) host=(.*?)(\\s|$)"
    );

    @Override
    public boolean canParse(String logLine) {
        return logLine.contains("request_method=") && logLine.contains("response_status=");
    }

    @Override
    public LogEntry parse(String logLine) {
        Matcher matcher = LOG_PATTERN.matcher(logLine);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid Request log format: " + logLine);
        }

        LocalDateTime timestamp = LocalDateTime.parse(matcher.group(1));
        String requestMethod = matcher.group(2);
        String requestUrl = matcher.group(3);
        int responseStatus = Integer.parseInt(matcher.group(4));
        long responseTimeMs = Long.parseLong(matcher.group(5));
        String host = matcher.group(6);

        return new RequestLogEntry(timestamp, host, requestMethod, requestUrl, responseStatus, responseTimeMs);
    }
}
