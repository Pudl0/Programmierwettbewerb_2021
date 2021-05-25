package de.pauhull.discordbot.api.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.SuccessResponse;
import de.pauhull.discordbot.webserver.session.Session;

public class AuthorizeHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        boolean authorized = false;

        if (httpExchange.getRequestMethod().equalsIgnoreCase("POST")) {
            JsonObject request = api.readRequestBody(httpExchange);
            String passwordHash = request.get("password").getAsString();
            if (passwordHash.equals(api.getWebServer().getPasswordHash())) {
                authorized = true;
                session.setAuthorized(true);
            }
        }

        return api.getGson().toJson(new SuccessResponse(authorized));
    }
}
