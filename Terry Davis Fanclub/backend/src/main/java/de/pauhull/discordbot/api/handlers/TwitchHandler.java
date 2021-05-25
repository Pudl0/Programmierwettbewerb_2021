package de.pauhull.discordbot.api.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.ErrorResponse;
import de.pauhull.discordbot.api.response.SuccessResponse;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.webserver.session.Session;
import discord4j.rest.util.Color;

import java.util.HashSet;
import java.util.Set;

public class TwitchHandler implements RequestHandler {

    private Set<String> notificationIds;

    public TwitchHandler() {

        this.notificationIds = new HashSet<>();
    }

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        if (!httpExchange.getRequestMethod().equals("POST")) {

            if (httpExchange.getRequestURI().getQuery() != null) {
                String[] queries = httpExchange.getRequestURI().getQuery().split("&");
                for (String query : queries) {
                    String[] parts = query.split("=");
                    if (parts[0].equals("hub.challenge")) {
                        return parts[1];
                    }
                }
            }

            return api.getGson().toJson(new SuccessResponse(false));
        }

        String notificationId = httpExchange.getRequestHeaders().getFirst("Twitch-Notfication-Id");
        if (notificationIds.contains(notificationId)) {
            return api.getGson().toJson(new ErrorResponse("Duplicate request"));
        }
        notificationIds.add(notificationId);

        JsonArray data = api.readRequestBody(httpExchange).getAsJsonArray("data");

        if (data.size() == 0) {
            // stream offline
            return api.getGson().toJson(new SuccessResponse(true));
        }

        DiscordBot bot = DiscordBot.getInstance();
        JsonObject stream = data.get(0).getAsJsonObject();
        String author = stream.get("user_name").getAsString();
        String title = String.format(bot.getConfig().getMessages().getStreamStarted(), author, stream.get("title").getAsString());
        String image = stream.get("thumbnail_url").getAsString()
                .replace("{width}", "1280").replace("{height}", "720");
        String streamUrl = String.format("https://www.twitch.tv/%s", stream.get("user_login").getAsString());

        bot.inTextChannels(bot.getConfig().getChannels().getAnnouncements(), channel -> {
            channel.createEmbed(spec -> {
                spec.setColor(Color.VIVID_VIOLET)
                        .setAuthor(author, null, null)
                        .setTitle(title)
                        .setUrl(streamUrl)
                        .setImage(image);
            }).block();
        });

        bot.log("Announced live stream");
        return api.getGson().toJson(new SuccessResponse(true));
    }
}
