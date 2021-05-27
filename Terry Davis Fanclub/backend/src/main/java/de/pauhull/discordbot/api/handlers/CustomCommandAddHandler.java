package de.pauhull.discordbot.api.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.SuccessResponse;
import de.pauhull.discordbot.api.response.UnauthorizedResponse;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.bot.manager.CustomCommandManager.CustomCommand;
import de.pauhull.discordbot.webserver.session.Session;

public class CustomCommandAddHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        DiscordBot bot = DiscordBot.getInstance();

        if (!session.isAuthorized()) {
            return api.getGson().toJson(new UnauthorizedResponse());
        }

        if (httpExchange.getRequestMethod().equals("POST")) {

            JsonObject request = api.readRequestBody(httpExchange);
            CustomCommand customCommand = api.getGson().fromJson(request, CustomCommand.class);

            if (customCommand != null
                    && customCommand.getLabel() != null
                    && customCommand.getResponse() != null
                    && customCommand.getDesc() != null) {

                if (bot.getCustomCommandManager().getCommand(customCommand.getLabel()) != null) {
                    return api.getGson().toJson(new SuccessResponse(false));
                }

                bot.getCustomCommandManager().getCustomCommands().add(customCommand);
                bot.getCustomCommandManager().save();
                return api.getGson().toJson(new SuccessResponse(true));
            }
        }

        return api.getGson().toJson(new SuccessResponse(false));
    }
}
