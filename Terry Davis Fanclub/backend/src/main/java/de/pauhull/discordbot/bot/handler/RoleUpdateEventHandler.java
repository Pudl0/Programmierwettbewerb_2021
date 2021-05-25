package de.pauhull.discordbot.bot.handler;

import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.bot.manager.TeamManager;
import discord4j.core.event.domain.role.RoleUpdateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;

import java.io.IOException;
import java.util.function.Consumer;

public class RoleUpdateEventHandler implements Consumer<RoleUpdateEvent> {

    @Override
    public void accept(RoleUpdateEvent event) {

        DiscordBot bot = DiscordBot.getInstance();
        Role old = event.getOld().orElse(null);

        if(old == null) {
            return;
        }

        String newName = event.getCurrent().getName();
        Guild currentGuild = event.getCurrent().getGuild().block();
        TeamManager.Team oldTeam = bot.getTeamManager().getTeam(old.getName());
        if(oldTeam != null) {
            bot.getClient().getGuilds()
                    .filter(guild -> guild != currentGuild)
                    .flatMap(Guild::getRoles)
                    .filter(role -> role.getName().equalsIgnoreCase(oldTeam.getName()))
                    .toIterable().forEach(role -> {
                        role.edit(spec -> {
                            spec.setName(newName);
                        }).subscribe();
            });

            try {
                oldTeam.setName(event.getCurrent().getName());
                bot.getTeamManager().save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
