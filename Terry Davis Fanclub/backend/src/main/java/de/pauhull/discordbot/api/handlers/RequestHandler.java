package de.pauhull.discordbot.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.webserver.session.Session;

public interface RequestHandler {

    String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api);
}
