package org.example;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String[] inputFiles = {
                "C:\\Users\\mrvea\\Desktop\\PyramidPrinter\\input3.txt",
                "C:\\Users\\mrvea\\Desktop\\PyramidPrinter\\input4.txt"
        };

        ExecutorService executorService = Executors.newFixedThreadPool(inputFiles.length);

        for (String inputFile : inputFiles) {
            executorService.submit(() -> {
                try {
                    processFile(inputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
    }

    private static void processFile(String inputFile) throws IOException {
        String outputFile = inputFile.replace(".txt", "_output.txt");  // Çıktı dosyasının yolu
        String logFile = inputFile.replace(".txt", "_log.txt");  // Log dosyasının yolu
        List<PrintTask> tasks = readTasks(inputFile);
        printTasks(tasks, outputFile, logFile);
    }

    private static List<PrintTask> readTasks(String filePath) throws FileNotFoundException {
        List<PrintTask> tasks = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("File not found: " + filePath);
            return tasks;
        }
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {  // İlk satırı oku ve atla.
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split("\\s+");
                int requestTime = Integer.parseInt(parts[0]);
                String patternType = parts[1];
                int outputSize = Integer.parseInt(parts[2]);
                tasks.add(new PrintTask(requestTime, patternType, outputSize));
            }
        }
        return tasks;
    }

    private static void printTasks(List<PrintTask> tasks, String outputFile, String logFile) throws IOException {
        FileWriter outputWriter = new FileWriter(outputFile);
        FileWriter logWriter = new FileWriter(logFile);
        String currentPattern = "";
        for (PrintTask task : tasks) {
            if (!task.patternType.equals(currentPattern) && !currentPattern.equals("")) {
                logWriter.write("Printer is free\n");
            }
            currentPattern = task.patternType;
            logWriter.write("Printing is started for " + task.patternType + "-" + task.outputSize + "\n");
            outputWriter.write(generatePattern(task.patternType, task.outputSize));
            logWriter.write("Printing is done for " + task.patternType + "-" + task.outputSize + "\n");
        }
        outputWriter.close();
        logWriter.close();
    }

    private static String generatePattern(String patternType, int size) {
        StringBuilder builder = new StringBuilder();
        if (patternType.equals("Star")) {
            for (int i = 1; i <= size; i++) {
                builder.append(" ".repeat(size - i));
                builder.append("* ".repeat(i).trim());
                builder.append("\n");
            }
        } else if (patternType.equals("Alphabet")) {
            for (int i = 1; i <= size; i++) {
                builder.append(" ".repeat(size - i));
                for (int j = 0; j < i; j++) {
                    builder.append((char) ('A' + j) + " ");
                }
                for (int j = i - 2; j >= 0; j--) {
                    builder.append((char) ('A' + j) + " ");
                }
                builder.append("\n");
            }
        }
        return builder.toString();
    }
}

class PrintTask {
    int requestTime;
    String patternType;
    int outputSize;

    public PrintTask(int requestTime, String patternType, int outputSize) {
        this.requestTime = requestTime;
        this.patternType = patternType;
        this.outputSize = outputSize;
    }
}
