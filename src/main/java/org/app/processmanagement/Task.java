package org.app.processmanagement;

import org.app.handler.IO;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Task {
    public enum Action {
        ENCRYPT,
        DECRYPT
    }

    private final String filePath;
    private RandomAccessFile file;
    private final Action action;

    // Constructor (equivalent to C++ move-based constructor)
    public Task(RandomAccessFile file, Action action, String filePath) {
        this.file = file;
        this.action = action;
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public RandomAccessFile getFile() {
        return file;
    }

    public Action getAction() {
        return action;
    }

    // Equivalent to C++ toString()
    @Override
    public String toString() {
        return filePath + "," + action.name();
    }

    // Equivalent to static Task::fromString
    public static Task fromString(String taskData) throws IOException {
        String[] parts = taskData.split(",", 2);

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid task data format");
        }

        String filePath = parts[0];
        Action action = Action.valueOf(parts[1]);

        IO io = new IO(filePath);
        RandomAccessFile file = io.getFileStream();

        if (file == null) {
            throw new RuntimeException("Failed to open file: " + filePath);
        }

        return new Task(file, action, filePath);
    }

    // Explicit cleanup (RAII replacement)
    public void close() {
        if (file != null) {
            try {
                file.close();
            } catch (IOException ignored) {}
            file = null;
        }
    }
}
