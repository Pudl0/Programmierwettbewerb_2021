package de.pauhull.discordbot.bot.handler;

import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.bot.manager.TeamManager;
import de.pauhull.discordbot.util.ColorUtil;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class GuildCreateEventHandler implements Consumer<GuildCreateEvent> {

    @Override
    public void accept(GuildCreateEvent event) {

        DiscordBot bot = DiscordBot.getInstance();
        Guild guild = event.getGuild();

        createChannelIfNotExists(guild, bot.getConfig().getChannels().getWelcome());
        createChannelIfNotExists(guild, bot.getConfig().getChannels().getAnnouncements());

        createOrEditTeams(guild, bot.getTeamManager().getTeams());
    }

    private void createOrEditTeams(Guild guild, List<TeamManager.Team> teams) {

        for (TeamManager.Team team : teams) {
            List<Role> roles = guild.getRoles().filter(role -> role.getName().equalsIgnoreCase(team.getName())).collectList().block();
            if (roles == null || roles.isEmpty()) {
                guild.createRole(spec -> {
                    spec.setName(team.getName());
                    spec.setColor(ColorUtil.getDiscordColor(team.getColor()));
                    spec.setHoist(true);
                    spec.setMentionable(false);
                    spec.setPermissions(PermissionSet.none());
                }).block();
            } else {
                roles.forEach(role -> role.edit(spec -> {
                    spec.setName(team.getName())
                            .setColor(ColorUtil.getDiscordColor(team.getColor()))
                            .setHoist(true)
                            .setMentionable(false)
                            .setPermissions(PermissionSet.none());
                }));
            }
        }
    }

    private void createChannelIfNotExists(Guild guild, String channelName) {

        TextChannel textChannel = (TextChannel) guild.getChannels()
                .filter(channel -> channel instanceof TextChannel && channel.getName().equalsIgnoreCase(channelName))
                .blockFirst();

        if (textChannel == null) {
            textChannel = guild.createTextChannel(spec -> spec.setName(channelName)).block();
        }

        Objects.requireNonNull(textChannel).edit(spec -> {
            spec.setPermissionOverwrites(Collections.singleton(
                    PermissionOverwrite.forRole(guild.getEveryoneRole().block().getId(),
                            PermissionSet.none(),
                            PermissionSet.of(Permission.SEND_MESSAGES)
                    )));
        }).block();
    }
}
