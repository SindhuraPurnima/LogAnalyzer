package loganalyzer.parser;

import loganalyzer.model.LogEntry;

public interface LogParser {
    /**
     * Checks if this parser can handle the given log line
     * @param logLine the log line to check
     * @return true if this parser can handle the log line
     */
    boolean canParse(String logLine);

    /**
     * Parses the log line into a LogEntry object
     * @param logLine the log line to parse
     * @return the parsed LogEntry
     * @throws IllegalArgumentException if the log line cannot be parsed
     */
    LogEntry parse(String logLine);
}
