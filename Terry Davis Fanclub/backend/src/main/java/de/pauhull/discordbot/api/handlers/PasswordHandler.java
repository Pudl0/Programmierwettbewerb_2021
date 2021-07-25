package de.pauhull.discordbot.api.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.SuccessResponse;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.webserver.session.Session;

import java.io.IOException;

public class PasswordHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        if (httpExchange.getRequestMethod().equalsIgnoreCase("POST")) {
            JsonObject request = api.readRequestBody(httpExchange);
            String newPasswordHash = request.get("newPassword").getAsString();
            String passwordHash = request.get("password").getAsString();

            if (passwordHash.equals(api.getWebServer().getPasswordHash())) {
                try {
                    api.getWebServer().setPasswordHash(newPasswordHash);
                    api.getWebServer().savePassword();
                    DiscordBot.getInstance().log("Password changed");
                    return api.getGson().toJson(new SuccessResponse(true));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return api.getGson().toJson(new SuccessResponse(false));
    }
}
