package de.pauhull.discordbot.api.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.SuccessResponse;
import de.pauhull.discordbot.api.response.UnauthorizedResponse;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.webserver.session.Session;

public class ApplicationsDeleteHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        if (!session.isAuthorized()) {
            return api.getGson().toJson(new UnauthorizedResponse());
        }

        if (httpExchange.getRequestMethod().equals("POST")) {
            JsonObject request = api.readRequestBody(httpExchange);
            String email = request.get("email").getAsString();
            boolean success = DiscordBot.getInstance().getApplicationManager().removeApplication(email);
            return api.getGson().toJson(new SuccessResponse(success));
        }

        return api.getGson().toJson(new SuccessResponse(false));
    }
}
