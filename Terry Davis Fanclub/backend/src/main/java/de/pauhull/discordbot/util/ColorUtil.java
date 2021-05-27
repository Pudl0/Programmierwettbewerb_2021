package de.pauhull.discordbot.util;

import discord4j.rest.util.Color;

public class ColorUtil {

    public static Color getDiscordColor(String hex) {
        java.awt.Color javaColor = java.awt.Color.decode(hex);
        return Color.of(javaColor.getRGB());
    }
}
