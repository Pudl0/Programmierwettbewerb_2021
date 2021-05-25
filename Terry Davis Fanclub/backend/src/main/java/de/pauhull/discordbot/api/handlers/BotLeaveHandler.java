package de.pauhull.discordbot.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.SuccessResponse;
import de.pauhull.discordbot.api.response.UnauthorizedResponse;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.webserver.session.Session;

public class BotLeaveHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        if (!session.isAuthorized()) {
            return api.getGson().toJson(new UnauthorizedResponse());
        }

        DiscordBot bot = DiscordBot.getInstance();
        bot.getClient().getGuilds()
                .filter(guild -> guild.getId().asString().equalsIgnoreCase(path[2]))
                .toIterable().forEach(guild -> guild.leave().block());

        return api.getGson().toJson(new SuccessResponse(true));
    }
}
