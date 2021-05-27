package de.pauhull.discordbot.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.SuccessResponse;
import de.pauhull.discordbot.api.response.UnauthorizedResponse;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.bot.manager.CustomCommandManager.CustomCommand;
import de.pauhull.discordbot.webserver.session.Session;

public class CustomCommandDeleteHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        DiscordBot bot = DiscordBot.getInstance();

        if (!session.isAuthorized()) {
            return api.getGson().toJson(new UnauthorizedResponse());
        }

        CustomCommand command = bot.getCustomCommandManager().getCommand(path[2]);
        if (command == null) {
            return api.getGson().toJson(new SuccessResponse(false));
        }

        bot.getCustomCommandManager().getCustomCommands().remove(command);
        bot.getCustomCommandManager().save();
        return api.getGson().toJson(new SuccessResponse(true));
    }
}
