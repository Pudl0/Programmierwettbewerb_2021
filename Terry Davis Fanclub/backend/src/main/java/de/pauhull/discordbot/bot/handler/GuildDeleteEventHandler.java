package de.pauhull.discordbot.bot.handler;

import de.pauhull.discordbot.bot.DiscordBot;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import discord4j.core.object.entity.channel.TextChannel;

import java.util.function.Consumer;

public class GuildDeleteEventHandler implements Consumer<GuildDeleteEvent> {

    @Override
    public void accept(GuildDeleteEvent event) {

        DiscordBot bot = DiscordBot.getInstance();

        bot.getTeamManager().getTeams().stream()
                .map(team -> event.getGuild().get().getRoles()
                        .filter(role -> role.getName().equalsIgnoreCase(team.getName())).toIterable())
                .forEach(roles -> roles.forEach(role -> role.delete().block()));

        event.getGuild().get().getChannels()
                .filter(channel -> channel instanceof TextChannel)
                .filter(channel -> channel.getName().equalsIgnoreCase(bot.getConfig().getChannels().getAnnouncements())
                        || channel.getName().equalsIgnoreCase(bot.getConfig().getChannels().getWelcome()))
                .toIterable().forEach(channel -> channel.delete().block());
    }
}
