package de.pauhull.discordbot.api.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.SuccessResponse;
import de.pauhull.discordbot.api.response.UnauthorizedResponse;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.webserver.session.Session;

public class TeamsAddHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        if (!session.isAuthorized()) {
            return api.getGson().toJson(new UnauthorizedResponse());
        }

        if (httpExchange.getRequestMethod().equals("POST")) {

            DiscordBot bot = DiscordBot.getInstance();
            JsonObject json = api.readRequestBody(httpExchange);
            if (!json.has("name") || !json.has("color") || json.get("name").getAsString().isEmpty()) {
                return api.getGson().toJson(new SuccessResponse(false));
            }

            String name = json.get("name").getAsString();
            String color = json.get("color").getAsString();

            boolean success = bot.getTeamManager().addTeam(name, color, null) != null;
            if(success) {
                bot.log("Public team %s created", name);
            }
            return api.getGson().toJson(new SuccessResponse(success));
        }

        return api.getGson().toJson(new SuccessResponse(false));
    }
}
