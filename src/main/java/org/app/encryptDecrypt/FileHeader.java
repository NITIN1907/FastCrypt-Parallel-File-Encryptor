package org.app.encryptDecrypt;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileHeader {

    public static final int MAGIC = 0x43525950; // "CRYP"
    public static final int HEADER_SIZE = 8;

    public static boolean hasHeader(RandomAccessFile file) throws IOException {
        if (file.length() < HEADER_SIZE) return false;
        file.seek(0);
        return file.readInt() == MAGIC;
    }

    public static int getCount(RandomAccessFile file) throws IOException {
        if (!hasHeader(file)) return 0;
        file.seek(4);
        return file.readInt();
    }

    private static void writeHeader(RandomAccessFile file, int count) throws IOException {
        file.seek(0);
        file.writeInt(MAGIC);
        file.writeInt(count);
    }

    /**
     * Inserts header by shifting file data forward (SAFE)
     */
    public static void insertHeader(RandomAccessFile file) throws IOException {
        long originalSize = file.length();
        file.setLength(originalSize + HEADER_SIZE);

        // Shift data forward
        for (long i = originalSize - 1; i >= 0; i--) {
            file.seek(i);
            byte b = file.readByte();
            file.seek(i + HEADER_SIZE);
            file.writeByte(b);
        }

        writeHeader(file, 1);
    }

    /**
     * Removes header by shifting data back (SAFE)
     */
    public static void removeHeader(RandomAccessFile file) throws IOException {
        long size = file.length();

        // Shift data backward
        for (long i = HEADER_SIZE; i < size; i++) {
            file.seek(i);
            byte b = file.readByte();
            file.seek(i - HEADER_SIZE);
            file.writeByte(b);
        }

        file.setLength(size - HEADER_SIZE);
    }
}
