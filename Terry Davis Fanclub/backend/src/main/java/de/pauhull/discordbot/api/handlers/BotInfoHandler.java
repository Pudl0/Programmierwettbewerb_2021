package de.pauhull.discordbot.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.BotInfoResponse;
import de.pauhull.discordbot.api.response.UnauthorizedResponse;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.webserver.session.Session;
import discord4j.core.object.entity.User;
import discord4j.rest.util.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BotInfoHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        if (!session.isAuthorized()) {
            return api.getGson().toJson(new UnauthorizedResponse());
        }

        DiscordBot bot = DiscordBot.getInstance();
        User user = Objects.requireNonNull(bot.getClient().getSelf().block());
        String username = user.getUsername();
        String discriminator = user.getDiscriminator();
        String profileImg = user.getAvatarUrl();
        String clientId = user.getClient().getSelfId().asString();
        List<BotInfoResponse.Guild> guilds = new ArrayList<>();

        bot.getClient().getGuilds().toIterable().forEach(guild -> {
            guilds.add(new BotInfoResponse.Guild(
                    guild.getId().asString(),
                    guild.getName(),
                    guild.getIconUrl(Image.Format.JPEG)
                            .orElse("https://discord.com/assets/145dc557845548a36a82337912ca3ac5.svg")));
        });

        return api.getGson().toJson(new BotInfoResponse(username, discriminator, profileImg, clientId, guilds));
    }
}
