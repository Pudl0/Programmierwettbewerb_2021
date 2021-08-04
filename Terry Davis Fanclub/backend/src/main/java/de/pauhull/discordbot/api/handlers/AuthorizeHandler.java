package de.pauhull.discordbot.api.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.SuccessResponse;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.webserver.session.Session;

public class AuthorizeHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        DiscordBot bot = DiscordBot.getInstance();

        if(session.isAuthorized()) {
            return api.getGson().toJson(new SuccessResponse(false));
        }

        if (httpExchange.getRequestMethod().equalsIgnoreCase("POST")) {
            JsonObject request = api.readRequestBody(httpExchange);
            String passwordHash = request.get("password").getAsString();
            if (passwordHash.equals(api.getWebServer().getPasswordHash())) {
                session.setAuthorized(true);
                bot.log("%s authorized successfully", httpExchange.getRemoteAddress().getAddress().getHostAddress());
            }
        }

        return api.getGson().toJson(new SuccessResponse(session.isAuthorized()));
    }
}
