package de.pauhull.discordbot.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.handlers.*;
import de.pauhull.discordbot.api.response.ErrorResponse;
import de.pauhull.discordbot.webserver.WebServer;
import de.pauhull.discordbot.webserver.session.Session;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class Api {

    private Gson gson;
    private WebServer webServer;
    private Map<String, RequestHandler> handlers;

    public Api(WebServer webServer) {

        this.gson = webServer.getGson();
        this.webServer = webServer;
        this.handlers = new HashMap<>();

        handlers.put("session", new SessionHandler());
        handlers.put("authorize", new AuthorizeHandler());
        handlers.put("password", new PasswordHandler());
        handlers.put("logout", new LogoutHandler());
        handlers.put("announce", new AnnounceHandler());
        handlers.put("bot/info", new BotInfoHandler());
        handlers.put("bot/leave/{}", new BotLeaveHandler());
        handlers.put("bot/image", new BotImageHandler());
        handlers.put("bot/name", new BotNameHandler());
        handlers.put("twitch", new TwitchHandler());
        handlers.put("config/get", new ConfigGetHandler());
        handlers.put("config/update", new ConfigUpdateHandler());
        handlers.put("teams/get", new TeamsGetHandler());
        handlers.put("teams/add", new TeamsAddHandler());
        handlers.put("teams/delete", new TeamsDeleteHandler());
        handlers.put("applications/get", new ApplicationsGetHandler());
        handlers.put("applications/get/csv/{}", new ApplicationsGetCsvHandler());
        handlers.put("applications/delete", new ApplicationsDeleteHandler());
        handlers.put("customcommand/add", new CustomCommandAddHandler());
        handlers.put("customcommand/get", new CustomCommandGetHandler());
        handlers.put("customcommand/delete/{}", new CustomCommandDeleteHandler());
    }

    public String handleRequest(String[] pathArr, Session session, HttpExchange httpExchange) {

        findHandler:
        for (String path : handlers.keySet()) {

            String[] parts = path.split("/");
            if (parts.length == pathArr.length) {
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equals("{}")) {
                        continue;
                    }
                    if (!parts[i].equalsIgnoreCase(pathArr[i])) {
                        continue findHandler;
                    }
                }

                return handlers.get(path).handleRequest(pathArr, session, httpExchange, this);
            }
        }

        return gson.toJson(new ErrorResponse("Api call not found"));
    }

    public JsonObject readRequestBody(HttpExchange httpExchange) {

        String request = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
        return JsonParser.parseString(request).getAsJsonObject();
    }
}
