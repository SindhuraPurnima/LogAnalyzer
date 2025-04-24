package loganalyzer.aggregator;

import com.fasterxml.jackson.databind.JsonNode;
import loganalyzer.model.LogEntry;

public interface LogAggregator<T extends LogEntry> {
    /**
     * Aggregate a log entry
     * @param logEntry the log entry to aggregate
     */
    void aggregate(T logEntry);

    /**
     * Get the aggregation results as JSON
     * @return JsonNode containing the aggregation results
     */
    JsonNode getAggregationResult();

    /**
     * Get the output file name for this aggregator
     * @return the name of the output JSON file
     */
    String getOutputFileName();
} 