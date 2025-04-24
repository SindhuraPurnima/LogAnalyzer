package loganalyzer.model;

import java.time.LocalDateTime;

public class APMLogEntry extends LogEntry {
    private final String metric;
    private final double value;

    public APMLogEntry(LocalDateTime timestamp, String host, String metric, double value) {
        super(timestamp, host);
        this.metric = metric;
        this.value = value;
    }

    public String getMetric() {
        return metric;
    }

    public double getValue() {
        return value;
    }
}

//Defines common attributes that ALL log entries must have
//timestamp, host, metric, value
