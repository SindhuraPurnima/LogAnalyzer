package loganalyzer.model;

import java.time.LocalDateTime;

public abstract class LogEntry {
    private final LocalDateTime timestamp;
    private final String host;

    protected LogEntry(LocalDateTime timestamp, String host) {
        this.timestamp = timestamp;
        this.host = host;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getHost() {
        return host;
    }
}
