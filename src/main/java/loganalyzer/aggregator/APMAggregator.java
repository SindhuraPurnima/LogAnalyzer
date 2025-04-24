package loganalyzer.aggregator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import loganalyzer.model.APMLogEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APMAggregator implements LogAggregator<APMLogEntry> {
    private final Map<String, List<Double>> metricValues;
    private final ObjectMapper objectMapper;

    public APMAggregator() {
        this.metricValues = new HashMap<>();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void aggregate(APMLogEntry logEntry) {
        String metric = logEntry.getMetric();
        double value = logEntry.getValue();
        
        metricValues.computeIfAbsent(metric, k -> new ArrayList<>()).add(value);
    }

    @Override
    public JsonNode getAggregationResult() {
        ObjectNode rootNode = objectMapper.createObjectNode();
        
        for (Map.Entry<String, List<Double>> entry : metricValues.entrySet()) {
            String metric = entry.getKey();
            List<Double> values = entry.getValue();
            
            // Sort values for calculating median
            Collections.sort(values);
            
            ObjectNode metricNode = objectMapper.createObjectNode();
            metricNode.put("minimum", values.isEmpty() ? 0 : values.get(0));
            metricNode.put("maximum", values.isEmpty() ? 0 : values.get(values.size() - 1));
            metricNode.put("average", calculateAverage(values));
            metricNode.put("median", calculateMedian(values));
            
            rootNode.set(metric, metricNode);
        }
        
        return rootNode;
    }

    @Override
    public String getOutputFileName() {
        return "apm.json";
    }

    private double calculateAverage(List<Double> values) {
        if (values.isEmpty()) return 0;
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private double calculateMedian(List<Double> values) {
        if (values.isEmpty()) return 0;
        int size = values.size();
        if (size % 2 == 0) {
            return (values.get(size/2 - 1) + values.get(size/2)) / 2.0;
        } else {
            return values.get(size/2);
        }
    }
} 