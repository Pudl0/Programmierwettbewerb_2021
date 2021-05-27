package de.pauhull.discordbot.bot.paste;

import com.google.gson.JsonParser;
import de.pauhull.discordbot.bot.DiscordBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HasteService {

    private String url;
    private ScheduledExecutorService executorService;

    public HasteService() {

        this.url = DiscordBot.getInstance().getConfig().getPaste().getUrl();
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.executorService.scheduleAtFixedRate(this::keepAlive, 0, 1, TimeUnit.MINUTES);
    }

    public String paste(String content) {

        try {
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

            HttpURLConnection connection = (HttpURLConnection) new URL(url + "documents").openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.addRequestProperty("User-Agent", "placeholder");
            connection.addRequestProperty("Content-Type", "text/plain; charset=UTF-8");

            OutputStream output = connection.getOutputStream();
            output.write(bytes);
            output.flush();

            if (connection.getResponseCode() == 200) {
                String response = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
                String key = JsonParser.parseString(response).getAsJsonObject().get("key").getAsString();
                return url + key;
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    // glitch.com free hosted apps need to be kept alive or they will go to sleep
    private void keepAlive() {

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.getResponseCode();
        } catch (IOException ignored) {
            // service down :(
        }
    }
}
