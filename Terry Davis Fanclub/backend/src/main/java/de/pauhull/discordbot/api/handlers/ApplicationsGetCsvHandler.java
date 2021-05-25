package de.pauhull.discordbot.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.UnauthorizedResponse;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.bot.manager.ApplicationManager;
import de.pauhull.discordbot.webserver.session.Session;

public class ApplicationsGetCsvHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        if (!session.isAuthorized()) {
            return api.getGson().toJson(new UnauthorizedResponse());
        }

        httpExchange.getResponseHeaders().add("Content-Type", "text/csv; charset=UTF-8");
        StringBuilder builder = new StringBuilder("sep=,\n");
        for (ApplicationManager.Application application : DiscordBot.getInstance().getApplicationManager().getApplications()) {
            builder.append("\"").append(application.getEmail()).append("\"")
                    .append(",")
                    .append("\"").append(application.getName()).append("\"\n");
        }

        return builder.toString();
    }
}
