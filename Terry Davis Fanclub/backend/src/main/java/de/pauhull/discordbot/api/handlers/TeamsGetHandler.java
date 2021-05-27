package de.pauhull.discordbot.api.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.UnauthorizedResponse;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.bot.manager.TeamManager;
import de.pauhull.discordbot.webserver.session.Session;

public class TeamsGetHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        if (!session.isAuthorized()) {
            return api.getGson().toJson(new UnauthorizedResponse());
        }

        JsonArray array = new JsonArray();
        for (TeamManager.Team team : DiscordBot.getInstance().getTeamManager().getTeams()) {
            JsonObject json = api.getGson().toJsonTree(team).getAsJsonObject();
            json.addProperty("ownerName", team.getOwnerName());
            json.add("memberNames", api.getGson().toJsonTree(team.getMemberNames()));
            array.add(json);
        }

        return api.getGson().toJson(array);
    }
}
