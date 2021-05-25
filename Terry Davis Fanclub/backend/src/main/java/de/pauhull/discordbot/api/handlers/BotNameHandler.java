package de.pauhull.discordbot.api.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.SuccessResponse;
import de.pauhull.discordbot.api.response.UnauthorizedResponse;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.webserver.session.Session;
import discord4j.rest.http.client.ClientException;

public class BotNameHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        if (!session.isAuthorized()) {
            return api.getGson().toJson(new UnauthorizedResponse());
        }

        if (httpExchange.getRequestMethod().equals("POST")) {
            JsonObject object = api.readRequestBody(httpExchange);
            String name = object.get("name").getAsString();

            try {
                DiscordBot.getInstance().getClient().edit(spec -> {
                    spec.setUsername(name);
                }).block();
                return api.getGson().toJson(new SuccessResponse(true));

            } catch (ClientException e) {
                return api.getGson().toJson(new SuccessResponse(false));
            }
        }

        return api.getGson().toJson(new SuccessResponse(false));
    }
}
