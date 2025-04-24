# Log Analyzer Application

A robust command-line application that processes and analyzes different types of log entries, generating meaningful aggregated metrics in JSON format.

## Table of Contents
1. [Overview](#overview)
2. [Features](#features)
3. [Project Structure](#project-structure)
4. [Design Patterns](#design-patterns)
5. [Log Types and Formats](#log-types-and-formats)
6. [Installation](#installation)
7. [Usage](#usage)
8. [Output Format](#output-format)
9. [Development Guide](#development-guide)

## Overview

The Log Analyzer is designed to process log files containing different types of log entries, classify them, and generate aggregated metrics. It supports three types of logs:
- APM (Application Performance Metrics) Logs
- Application Logs
- Request Logs

## Features

- **Flexible Log Parsing**: Automatically detects and parses different log formats
- **Robust Error Handling**: Gracefully handles corrupted or incompatible log entries
- **Extensible Design**: Easy to add support for new log types
- **Statistical Analysis**: Provides various metrics and aggregations
- **JSON Output**: Generates structured, easy-to-process output files

## Design Patterns

1. **Strategy Pattern**
   - Used in parser implementations
   - Allows different parsing strategies for each log type
   - Easy to add new parsing strategies

2. **Factory Pattern**
   - Implemented in LogParserFactory
   - Creates appropriate parser instances
   - Centralizes parser creation logic

3. **Singleton Pattern**
   - Used in LogManager and LogParserFactory
   - Ensures single point of control
   - Manages application state

## Log Types and Formats

### 1. APM Logs
Format:
```
timestamp=<ISO8601> metric=<name> host=<hostname> value=<number>
```
Example:
```
timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72
```

### 2. Application Logs
Format:
```
timestamp=<ISO8601> level=<LEVEL> message="<message>" host=<hostname>
```
Example:
```
timestamp=2024-02-24T16:22:20Z level=INFO message="Scheduled maintenance starting" host=webserver1
```

### 3. Request Logs
Format:
```
timestamp=<ISO8601> request_method=<METHOD> request_url="<URL>" response_status=<CODE> response_time_ms=<TIME> host=<hostname>
```
Example:
```
timestamp=2024-02-24T16:22:25Z request_method=POST request_url="/api/update" response_status=202 response_time_ms=200 host=webserver1
```

## Installation

1. Prerequisites:
   - Java 17 or higher
   - Maven 3.6 or higher

2. Build the project:
```bash
mvn clean install
```

## Usage

1. Run the application:
```bash
java -jar log-analyzer.jar --file <input-file.txt>
```

2. Output files generated:
   - `apm.json`: Performance metrics statistics
   - `application.json`: Log level distribution
   - `request.json`: API response time metrics

## Output Format

### APM Metrics (apm.json)
```json
{
    "cpu_usage_percent": {
        "minimum": 60,
        "median": 78,
        "average": 77,
        "max": 90
    }
}
```

### Application Logs (application.json)
```json
{
    "ERROR": 2,
    "INFO": 3,
    "DEBUG": 1,
    "WARNING": 1
}
```

### Request Logs (request.json)
```json
{
    "/api/update": {
        "response_times": {
            "min": 200,
            "50_percentile": 200,
            "90_percentile": 200,
            "95_percentile": 200,
            "99_percentile": 200,
            "max": 200
        },
        "status_codes": {
            "2XX": 1,
            "4XX": 0,
            "5XX": 0
        }
    }
}
```

## Development Guide

### Adding a New Log Type

1. Create a new model class extending `LogEntry`
2. Implement a new parser implementing `LogParser`
3. Create an aggregator implementing `LogAggregator`
4. Register the parser in `LogParserFactory`

### Running Tests

```bash
mvn test
```

### Code Style

- Follow Java naming conventions
- Add JavaDoc comments for public methods
- Include unit tests for new functionality 

sequenceDiagram
    Client->>LogManager: processLogLine("timestamp=2024... metric=cpu...")
    LogManager->>LogParserFactory: getParser(logLine)
    Note over LogParserFactory: Tries each parser in sequence
    LogParserFactory->>APMLogParser: canParse(logLine)?
    APMLogParser-->>LogParserFactory: true (found "metric=")
    LogParserFactory-->>LogManager: Returns APMLogParser
    LogManager->>APMLogParser: parse(logLine)
    APMLogParser-->>LogManager: Returns APMLogEntry
    LogManager->>APMAggregator: aggregate(apmEntry)

Key Points about Singleton in the Sequence:
Early Initialization:
LogManager singleton is created at application start
LogParserFactory singleton is created when LogManager is initialized