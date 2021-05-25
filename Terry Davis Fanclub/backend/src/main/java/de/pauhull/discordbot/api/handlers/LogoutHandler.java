package de.pauhull.discordbot.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.SuccessResponse;
import de.pauhull.discordbot.webserver.session.Session;

public class LogoutHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        if (session.isAuthorized()) {
            session.setAuthorized(false);
            return api.getGson().toJson(new SuccessResponse(true));
        }

        return api.getGson().toJson(new SuccessResponse(false));
    }
}
