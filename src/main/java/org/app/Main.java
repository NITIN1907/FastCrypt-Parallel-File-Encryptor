package org.app;

import org.app.handler.IO;
import org.app.processmanagement.ProcessManager;
import org.app.processmanagement.Task;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print("Enter the directory path: ");
            String directory = scanner.nextLine();

            System.out.print("Enter the action (encrypt/decrypt): ");
            String actionInput = scanner.nextLine().toLowerCase();

            Task.Action action;
            if ("encrypt".equals(actionInput)) {
                action = Task.Action.ENCRYPT;
            } else if ("decrypt".equals(actionInput)) {
                action = Task.Action.DECRYPT;
            } else {
                System.out.println("Invalid action!");
                return;
            }

            Path dirPath = Paths.get(directory);

            if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
                System.out.println("Invalid directory path!");
                return;
            }

            logStartTime();
            long start = System.nanoTime();
            int workerCount = Runtime.getRuntime().availableProcessors();
            ProcessManager processManager = new ProcessManager(workerCount);

            try (Stream<Path> paths = Files.walk(dirPath)) {

                paths.filter(Files::isRegularFile)
                        .forEach(path -> submitTask(path, action, processManager));

            } catch (IOException e) {
                System.out.println("Filesystem error: " + e.getMessage());
            }

            processManager.shutdownAndAwait();
            long end = System.nanoTime();
            double timeMs = (end - start) / 1_000_000.0;
            System.out.println("Total time taken: " + timeMs + " ms");

        }
    }

    private static void submitTask(
            Path path,
            Task.Action action,
            ProcessManager processManager
    ) {
        String filePath = path.toString();

        try {
            IO io = new IO(filePath);
            var file = io.getFileStream();

            if (file != null) {
                Task task = new Task(file, action, filePath);
                processManager.submitToQueue(task);
            } else {
                System.out.println("Unable to open file: " + filePath);
            }

        } catch (Exception e) {
            System.out.println("Error processing file: " + filePath);
        }
    }

    private static void logStartTime() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println(
                "Starting the encryption/decryption at: " +
                        LocalDateTime.now().format(formatter)
        );
    }
}
