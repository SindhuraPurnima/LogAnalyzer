package loganalyzer.aggregator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import loganalyzer.model.ApplicationLogEntry;

import java.util.HashMap;
import java.util.Map;

public class ApplicationLogAggregator implements LogAggregator<ApplicationLogEntry> {
    private final Map<String, Integer> levelCounts;
    private final ObjectMapper objectMapper;

    public ApplicationLogAggregator() {
        this.levelCounts = new HashMap<>();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void aggregate(ApplicationLogEntry logEntry) {
        String level = logEntry.getLevel().toUpperCase();
        levelCounts.merge(level, 1, Integer::sum);
    }

    @Override
    public JsonNode getAggregationResult() {
        ObjectNode rootNode = objectMapper.createObjectNode();
        
        // Ensure all standard log levels are present even if count is 0
        String[] standardLevels = {"ERROR", "WARNING", "INFO", "DEBUG"};
        for (String level : standardLevels) {
            rootNode.put(level, levelCounts.getOrDefault(level, 0));
        }
        
        // Add any additional non-standard levels that might exist
        levelCounts.forEach((level, count) -> {
            if (!rootNode.has(level)) {
                rootNode.put(level, count);
            }
        });
        
        return rootNode;
    }

    @Override
    public String getOutputFileName() {
        return "application.json";
    }
} 