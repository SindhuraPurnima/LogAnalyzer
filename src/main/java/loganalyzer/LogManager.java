package loganalyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import loganalyzer.aggregator.*;
import loganalyzer.factory.LogParserFactory;
import loganalyzer.model.*;
import loganalyzer.parser.LogParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogManager {
    private static volatile LogManager instance;
    private final LogParserFactory parserFactory;
    private final APMAggregator apmAggregator;
    private final ApplicationLogAggregator applicationLogAggregator;
    private final RequestLogAggregator requestLogAggregator;
    private final List<LogEntry> logs;
    private final ObjectMapper objectMapper;

    private LogManager() {
        this.parserFactory = LogParserFactory.getInstance();
        this.apmAggregator = new APMAggregator();
        this.applicationLogAggregator = new ApplicationLogAggregator();
        this.requestLogAggregator = new RequestLogAggregator();
        this.logs = new ArrayList<>();
        this.objectMapper = new ObjectMapper();
    }

    public static LogManager getInstance() {
        if (instance == null) {
            synchronized (LogManager.class) {
                if (instance == null) {
                    instance = new LogManager();
                }
            }
        }
        return instance;
    }

    public void processLogLine(String logLine) {
        try {
            LogParser parser = parserFactory.getParser(logLine);
            LogEntry entry = parser.parse(logLine);
            logs.add(entry);

            // Route to appropriate aggregator based on log type
            if (entry instanceof APMLogEntry) {
                apmAggregator.aggregate((APMLogEntry) entry);
            } else if (entry instanceof ApplicationLogEntry) {
                applicationLogAggregator.aggregate((ApplicationLogEntry) entry);
            } else if (entry instanceof RequestLogEntry) {
                requestLogAggregator.aggregate((RequestLogEntry) entry);
            }
        } catch (IllegalArgumentException e) {
            // Log line couldn't be parsed, skip it as per requirements
            System.err.println("Skipping invalid log line: " + logLine);
        }
    }

    public void generateOutputFiles() {
        try {
            // Generate APM logs output
            writeJsonToFile(apmAggregator.getAggregationResult(), apmAggregator.getOutputFileName());

            // Generate Application logs output
            writeJsonToFile(applicationLogAggregator.getAggregationResult(), applicationLogAggregator.getOutputFileName());

            // Generate Request logs output
            writeJsonToFile(requestLogAggregator.getAggregationResult(), requestLogAggregator.getOutputFileName());

        } catch (IOException e) {
            System.err.println("Error generating output files: " + e.getMessage());
            throw new RuntimeException("Failed to generate output files", e);
        }
    }

    private void writeJsonToFile(Object content, String fileName) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter()
                   .writeValue(new File(fileName), content);
    }

    // For testing purposes
    public List<LogEntry> getLogs() {
        return new ArrayList<>(logs);
    }
} 