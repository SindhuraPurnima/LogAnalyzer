package loganalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2 || !args[0].equals("--file")) {
            System.out.println("Usage: java -jar log-analyzer.jar --file <filename.txt>");
            System.exit(1);
        }

        String inputFile = args[1];
        LogManager logManager = LogManager.getInstance(); // Singleton pattern

        try {
            Files.lines(Paths.get(inputFile))
                 .forEach(logManager::processLogLine);
            
            // Generate output files
            logManager.generateOutputFiles();
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
    }
} 