package de.pauhull.discordbot.api.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.SuccessResponse;
import de.pauhull.discordbot.api.response.UnauthorizedResponse;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.webserver.session.Session;

public class AnnounceHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        if (!session.isAuthorized()) {
            return api.getGson().toJson(new UnauthorizedResponse());
        }

        DiscordBot bot = DiscordBot.getInstance();

        if (httpExchange.getRequestMethod().equalsIgnoreCase("POST")) {
            JsonObject request = api.readRequestBody(httpExchange);
            String title = request.get("title").getAsString();
            String description = request.get("description").getAsString();

            if (title.isEmpty() && description.isEmpty()) {
                return api.getGson().toJson(new SuccessResponse(false));
            }

            bot.inTextChannels(bot.getConfig().getChannels().getAnnouncements(), channel -> {
                channel.createEmbed(spec -> {
                    spec.setTitle(title)
                            .setDescription(description);
                }).block();
            });

            bot.log("Sent announcement");

            return api.getGson().toJson(new SuccessResponse(true));
        }

        return api.getGson().toJson(new SuccessResponse(false));
    }
}
