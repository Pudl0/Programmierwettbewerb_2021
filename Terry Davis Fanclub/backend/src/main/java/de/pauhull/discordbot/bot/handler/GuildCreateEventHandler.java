package de.pauhull.discordbot.bot.handler;

import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.bot.manager.TeamManager;
import de.pauhull.discordbot.util.ColorUtil;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class GuildCreateEventHandler implements Consumer<GuildCreateEvent> {

    @Override
    public void accept(GuildCreateEvent event) {

        Guild guild = event.getGuild();
        createChannelIfNotExists(guild, DiscordBot.getInstance().getConfig().getChannels().getWelcome());
        createChannelIfNotExists(guild, DiscordBot.getInstance().getConfig().getChannels().getAnnouncements());

        DiscordBot.getInstance().getTeamManager().getTeams().forEach(team -> createOrEditTeam(guild, team));
    }

    private void createOrEditTeam(Guild guild, TeamManager.Team team) {

        AtomicBoolean exists = new AtomicBoolean(false);
        guild.getRoles()
                .filter(role -> role.getName().equalsIgnoreCase(team.getName()))
                .toIterable().forEach(role -> role.edit(spec -> {
            exists.set(true);
            spec.setName(team.getName())
                    .setColor(ColorUtil.getDiscordColor(team.getColor()))
                    .setHoist(true)
                    .setMentionable(false)
                    .setPermissions(PermissionSet.none());
        }).block());

        if (exists.get()) {
            guild.createRole(spec -> {
                spec.setName(team.getName());
                spec.setColor(ColorUtil.getDiscordColor(team.getColor()));
                spec.setHoist(true);
                spec.setMentionable(false);
                spec.setPermissions(PermissionSet.none());
            }).block();
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
