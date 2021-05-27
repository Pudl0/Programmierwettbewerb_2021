package de.pauhull.discordbot.api.handlers;

import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.Config;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.SuccessResponse;
import de.pauhull.discordbot.api.response.UnauthorizedResponse;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.util.InstanceUpdater;
import de.pauhull.discordbot.webserver.session.Session;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ConfigUpdateHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        if (!session.isAuthorized()) {
            return api.getGson().toJson(new UnauthorizedResponse());
        }

        if (!httpExchange.getRequestMethod().equals("POST")) {
            return api.getGson().toJson(new SuccessResponse(false));
        }

        DiscordBot bot = DiscordBot.getInstance();
        String request = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
        InstanceCreator<Config> creator = new InstanceUpdater<>(bot.getConfig());
        new GsonBuilder().registerTypeAdapter(Config.class, creator).create().fromJson(request, Config.class);
        bot.getConfig().save();

        return api.getGson().toJson(new SuccessResponse(true));
    }
}
