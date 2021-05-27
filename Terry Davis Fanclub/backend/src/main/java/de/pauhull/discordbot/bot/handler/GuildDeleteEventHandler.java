package de.pauhull.discordbot.bot.handler;

import de.pauhull.discordbot.bot.DiscordBot;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;

import java.util.function.Consumer;

public class GuildDeleteEventHandler implements Consumer<GuildDeleteEvent> {

    @Override
    public void accept(GuildDeleteEvent event) {

        DiscordBot bot = DiscordBot.getInstance();

        Guild guild = event.getGuild().orElse(null);

        if (guild == null) {
            return;
        }

        guild.getRoles()
                .filter(role -> bot.getTeamManager().getTeam(role.getName()) != null)
                .toIterable().forEach(role -> role.delete().subscribe());

        guild.getChannels()
                .filter(channel -> channel instanceof TextChannel)
                .filter(channel -> channel.getName().equalsIgnoreCase(bot.getConfig().getChannels().getAnnouncements())
                        || channel.getName().equalsIgnoreCase(bot.getConfig().getChannels().getWelcome()))
                .toIterable().forEach(channel -> channel.delete().subscribe());
    }
}
