package loganalyzer.model;

import java.time.LocalDateTime;

public class RequestLogEntry extends LogEntry {
    private final String requestMethod;
    private final String requestUrl;
    private final int responseStatus;
    private final long responseTimeMs;

    public RequestLogEntry(LocalDateTime timestamp, String host, 
                         String requestMethod, String requestUrl, 
                         int responseStatus, long responseTimeMs) {
        super(timestamp, host);
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.responseStatus = responseStatus;
        this.responseTimeMs = responseTimeMs;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public long getResponseTimeMs() {
        return responseTimeMs;
    }
}
