package de.pauhull.discordbot.util;

import java.util.Random;

public class RandomStringGenerator {

    private static final Random RANDOM = new Random();
    public static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generate(int length, String charset) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(charset.charAt(RANDOM.nextInt(charset.length())));
        }
        return builder.toString();
    }

}
