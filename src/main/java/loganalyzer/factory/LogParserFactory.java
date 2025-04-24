package loganalyzer.factory;

import loganalyzer.parser.*;
import java.util.ArrayList;
import java.util.List;

public class LogParserFactory {
    private static volatile LogParserFactory instance;
    private final List<LogParser> parsers;

    private LogParserFactory() {
        this.parsers = new ArrayList<>();
        initializeParsers();
    }

    public static LogParserFactory getInstance() {
        if (instance == null) {
            synchronized (LogParserFactory.class) {
                if (instance == null) {
                    instance = new LogParserFactory();
                }
            }
        }
        return instance;
    }

    private void initializeParsers() {
        // Add parsers in order of most specific to most general
        parsers.add(new APMLogParser());
        parsers.add(new RequestLogParser());
        parsers.add(new ApplicationLogParser());
    }

    /**
     * Gets the appropriate parser for the given log line
     * @param logLine the log line to parse
     * @return the appropriate LogParser
     * @throws IllegalArgumentException if no parser can handle the log line
     */
    public LogParser getParser(String logLine) {
        return parsers.stream()
                .filter(parser -> parser.canParse(logLine))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No parser found for log line: " + logLine));
    }
}
