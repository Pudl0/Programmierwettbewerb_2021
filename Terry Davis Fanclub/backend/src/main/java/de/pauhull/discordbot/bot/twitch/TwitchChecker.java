package de.pauhull.discordbot.bot.twitch;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.pauhull.discordbot.bot.DiscordBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TwitchChecker implements Runnable {

    private ScheduledExecutorService scheduler;

    public TwitchChecker() {

        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduler.scheduleAtFixedRate(this, 0, 12, TimeUnit.HOURS);
    }

    @Override
    public void run() {
        try {
            this.subscribeToWebhook();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void subscribeToWebhook() throws IOException {

        DiscordBot bot = DiscordBot.getInstance();

        String accessToken = getAccessToken(bot.getConfig().getTwitch().getClientId(), bot.getConfig().getTwitch().getClientSecret());
        if (accessToken == null) {
            bot.log("Invalid twitch access token");
            return;
        }

        String channelId = getUserId(bot.getConfig().getTwitch().getChannel(), accessToken);
        if (channelId == null) {
            bot.log("Could not find channel " + bot.getConfig().getTwitch().getChannel());
            return;
        }

        String topic = String.format("https://api.twitch.tv/helix/streams?user_id=%s", channelId);
        String callback = String.format("http://%s:%d/api/twitch", bot.getRemoteAddress(), bot.getConfig().getWebConsole().getPort());

        JsonObject object = new JsonObject();
        object.addProperty("hub.mode", "subscribe");
        object.addProperty("hub.topic", topic);
        object.addProperty("hub.callback", callback);
        object.addProperty("hub.lease_seconds", TimeUnit.HOURS.toSeconds(12));
        String requestBody = DiscordBot.getInstance().getGson().toJson(object);

        URL url = new URL("https://api.twitch.tv/helix/webhooks/hub");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Client-Id", bot.getConfig().getTwitch().getClientId());
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setDoOutput(true);
        connection.setFixedLengthStreamingMode(requestBody.length());
        connection.getOutputStream().write(requestBody.getBytes(StandardCharsets.UTF_8));
        connection.getOutputStream().flush();

        if (connection.getResponseCode() != 200) {
            bot.log("Successfully (re-)subscribed to twitch webhook");
        }
    }

    private String getUserId(String channelName, String accessToken) throws IOException {

        DiscordBot bot = DiscordBot.getInstance();
        URL url = new URL(String.format("https://api.twitch.tv/helix/users?login=%s", channelName));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Client-Id", bot.getConfig().getTwitch().getClientId());
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);

        if (connection.getResponseCode() != 200) {
            return null;
        }

        String response = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();

        if (!json.has("data")) {
            return null;
        }

        JsonArray dataArray = json.getAsJsonArray("data");
        for (int i = 0; i < dataArray.size(); i++) {
            JsonObject data = dataArray.get(i).getAsJsonObject();
            if (data.get("login").getAsString().equalsIgnoreCase(channelName)) {
                return data.get("id").getAsString();
            }
        }

        return null;
    }

    private String getAccessToken(String clientId, String clientSecret) throws IOException {

        URL url = new URL(String.format(
                "https://id.twitch.tv/oauth2/token?client_id=%s&client_secret=%s&grant_type=%s",
                clientId, clientSecret, "client_credentials"));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        if (connection.getResponseCode() != 200) {
            return null;
        }

        String response = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();

        if (json.has("access_token")) {
            return json.get("access_token").getAsString();
        }

        return null;
    }
}
