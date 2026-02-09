package org.app.encryptDecrypt;

import java.io.IOException;

public class CryptionMain {
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java CryptionMain <task_data>");
            System.exit(1);
        }

        Cryption.executeCryption(args[0]);
    }
}
