package org.app.encryptDecrypt;

import org.app.handler.ReadEnv;
import org.app.processmanagement.Task;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Cryption {

    private static volatile Integer cachedKey = null;

    /**
     * Load encryption key once (thread-safe)
     */
    private static int getKey() throws IOException {
        if (cachedKey == null) {
            synchronized (Cryption.class) {
                if (cachedKey == null) {
                    String envValue = new ReadEnv().getenv().trim();
                    cachedKey = Integer.parseInt(envValue);
                }
            }
        }
        return cachedKey;
    }

    public static int executeCryption(String taskData) {

        try {
            Task task = Task.fromString(taskData);
            int key = getKey();

            byte[] buffer = new byte[8192];
            int bytesRead;

            try (RandomAccessFile file = task.getFile()) {

                int count = FileHeader.getCount(file);

                //  STRICT MODE RULES
                if (task.getAction() == Task.Action.ENCRYPT && count == 1) {
                    throw new IllegalStateException(
                            "File is already encrypted. Double encryption not allowed."
                    );
                }

                if (task.getAction() == Task.Action.DECRYPT && count == 0) {
                    throw new IllegalStateException(
                            "File is not encrypted. Cannot decrypt."
                    );
                }

                //  ENCRYPT: insert header first
                if (task.getAction() == Task.Action.ENCRYPT) {
                    FileHeader.insertHeader(file);
                }

                // Move to actual data
                file.seek(FileHeader.HEADER_SIZE);

                //  Encrypt / Decrypt data
                while ((bytesRead = file.read(buffer)) != -1) {

                    for (int i = 0; i < bytesRead; i++) {
                        int b = buffer[i] & 0xFF;

                        if (task.getAction() == Task.Action.ENCRYPT) {
                            buffer[i] = (byte) ((b + key) % 256);
                        } else {
                            buffer[i] = (byte) ((b - key + 256) % 256);
                        }
                    }

                    file.seek(file.getFilePointer() - bytesRead);
                    file.write(buffer, 0, bytesRead);
                }

                //  DECRYPT: remove header after processing
                if (task.getAction() == Task.Action.DECRYPT) {
                    FileHeader.removeHeader(file);
                }

                System.out.println(
                        task.getAction() + " successful for " + task.getFilePath()
                );
                return 0;
            }

        } catch (Exception e) {
            System.err.println("Cryption blocked: " + e.getMessage());
            return -1;
        }
    }
}
