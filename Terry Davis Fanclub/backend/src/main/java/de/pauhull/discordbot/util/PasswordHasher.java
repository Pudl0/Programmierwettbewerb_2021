package de.pauhull.discordbot.util;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    public static String digest(String cleartext) {

        try {
            byte[] data = MessageDigest.getInstance("SHA-256").digest(cleartext.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(data).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
