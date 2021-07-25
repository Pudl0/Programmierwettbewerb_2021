package de.pauhull.discordbot.api.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.SuccessResponse;
import de.pauhull.discordbot.api.response.UnauthorizedResponse;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.bot.manager.TeamManager;
import de.pauhull.discordbot.webserver.session.Session;

public class TeamsDeleteHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        if (!session.isAuthorized()) {
            return api.getGson().toJson(new UnauthorizedResponse());
        }

        if (httpExchange.getRequestMethod().equals("POST")) {
            DiscordBot bot = DiscordBot.getInstance();
            JsonObject request = api.readRequestBody(httpExchange);
            String name = request.get("name").getAsString();
            TeamManager.Team team = bot.getTeamManager().getTeam(name);
            if (team == null) {
                return api.getGson().toJson(new SuccessResponse(false));
            }
            boolean success = bot.getTeamManager().removeTeam(team);
            if(success) {
                bot.log("Team %s deleted", team.getName());
            }
            return api.getGson().toJson(new SuccessResponse(success));
        }

        return api.getGson().toJson(new SuccessResponse(false));
    }
}
