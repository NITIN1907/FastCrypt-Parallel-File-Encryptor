package org.app.handler;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ReadEnv {
    public String getenv() throws IOException {
        String envPath = ".env";
        IO io = new IO(envPath);
        RandomAccessFile file = io.getFileStream();

        StringBuilder buffer = new StringBuilder();

        try {
            String line;
            file.seek(0); // ensure reading from start

            while ((line = file.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            file.close(); // ownership is here, so close here

        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }
}
