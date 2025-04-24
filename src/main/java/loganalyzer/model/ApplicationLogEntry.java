package loganalyzer.model;

import java.time.LocalDateTime;

public class ApplicationLogEntry extends LogEntry {
    private final String level;
    private final String message;

    public ApplicationLogEntry(LocalDateTime timestamp, String host, String level, String message) {
        super(timestamp, host);
        this.level = level;
        this.message = message;
    }

    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }
}
