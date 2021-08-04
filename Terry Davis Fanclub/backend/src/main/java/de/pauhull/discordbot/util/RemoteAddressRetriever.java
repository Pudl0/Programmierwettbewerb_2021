package de.pauhull.discordbot.util;

import de.pauhull.discordbot.bot.DiscordBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class RemoteAddressRetriever {

    public static String getRemoteAddress() {

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://checkip.amazonaws.com/").openConnection();
            return new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().collect(Collectors.joining());
        } catch (IOException e) {
            DiscordBot.getInstance().log("Couldn't retrieve remote IP address: %s", e);
            return "127.0.0.1";
        }
    }
}
