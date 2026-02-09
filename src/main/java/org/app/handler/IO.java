package org.app.handler;

import java.io.IOException;
import java.io.RandomAccessFile;

public class IO {

    private RandomAccessFile file;

    // Constructor (equivalent to opening fstream)
    public IO(String filePath) {
        try {
            // "rw" = read + write, binary by default
            this.file = new RandomAccessFile(filePath, "rw");
        } catch (IOException e) {
            System.out.println("Unable to open file: " + filePath);
        }
    }

    // Equivalent to: return std::move(file_stream)
    public RandomAccessFile getFileStream() {
        RandomAccessFile temp = file;
        file = null; // simulate move semantics (ownership transfer)
        return temp;
    }

    // Destructor replacement (must be called explicitly)
    public void close() {
        if (file != null) {
            try {
                file.close();
            } catch (IOException ignored) {}
        }
    }
}
