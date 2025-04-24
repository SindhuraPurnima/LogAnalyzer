package loganalyzer.aggregator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import loganalyzer.model.RequestLogEntry;

import java.util.*;

public class RequestLogAggregator implements LogAggregator<RequestLogEntry> {
    private final Map<String, List<Long>> responseTimesByRoute;
    private final Map<String, Map<String, Integer>> statusCodesByRoute;
    private final ObjectMapper objectMapper;

    public RequestLogAggregator() {
        this.responseTimesByRoute = new HashMap<>();
        this.statusCodesByRoute = new HashMap<>();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void aggregate(RequestLogEntry logEntry) {
        String route = logEntry.getRequestUrl();
        long responseTime = logEntry.getResponseTimeMs();
        int statusCode = logEntry.getResponseStatus();

        // Aggregate response times
        responseTimesByRoute.computeIfAbsent(route, k -> new ArrayList<>())
                          .add(responseTime);

        // Aggregate status codes
        String statusCategory = getStatusCodeCategory(statusCode);
        statusCodesByRoute.computeIfAbsent(route, k -> new HashMap<>())
                         .merge(statusCategory, 1, Integer::sum);
    }

    @Override
    public JsonNode getAggregationResult() {
        ObjectNode rootNode = objectMapper.createObjectNode();

        for (String route : responseTimesByRoute.keySet()) {
            ObjectNode routeNode = objectMapper.createObjectNode();
            
            // Response times statistics
            List<Long> responseTimes = responseTimesByRoute.get(route);
            Collections.sort(responseTimes);
            
            ObjectNode responseTimesNode = objectMapper.createObjectNode();
            responseTimesNode.put("min", calculateMin(responseTimes));
            responseTimesNode.put("max", calculateMax(responseTimes));
            responseTimesNode.put("50_percentile", calculatePercentile(responseTimes, 50));
            responseTimesNode.put("90_percentile", calculatePercentile(responseTimes, 90));
            responseTimesNode.put("95_percentile", calculatePercentile(responseTimes, 95));
            responseTimesNode.put("99_percentile", calculatePercentile(responseTimes, 99));
            
            // Status code counts
            ObjectNode statusCodesNode = objectMapper.createObjectNode();
            Map<String, Integer> statusCodes = statusCodesByRoute.getOrDefault(route, new HashMap<>());
            statusCodesNode.put("2XX", statusCodes.getOrDefault("2XX", 0));
            statusCodesNode.put("3XX", statusCodes.getOrDefault("3XX", 0));
            statusCodesNode.put("4XX", statusCodes.getOrDefault("4XX", 0));
            statusCodesNode.put("5XX", statusCodes.getOrDefault("5XX", 0));

            routeNode.set("response_times", responseTimesNode);
            routeNode.set("status_codes", statusCodesNode);
            
            rootNode.set(route, routeNode);
        }

        return rootNode;
    }

    @Override
    public String getOutputFileName() {
        return "request.json";
    }

    private String getStatusCodeCategory(int statusCode) {
        return (statusCode / 100) + "XX";
    }

    private long calculateMin(List<Long> values) {
        return values.isEmpty() ? 0 : values.get(0);
    }

    private long calculateMax(List<Long> values) {
        return values.isEmpty() ? 0 : values.get(values.size() - 1);
    }

    private long calculatePercentile(List<Long> values, int percentile) {
        if (values.isEmpty()) return 0;
        int index = (int) Math.ceil(percentile / 100.0 * values.size()) - 1;
        return values.get(Math.max(0, Math.min(index, values.size() - 1)));
    }
} 