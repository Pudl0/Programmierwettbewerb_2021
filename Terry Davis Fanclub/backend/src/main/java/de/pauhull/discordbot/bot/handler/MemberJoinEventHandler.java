package de.pauhull.discordbot.bot.handler;

import de.pauhull.discordbot.bot.DiscordBot;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.object.entity.channel.TextChannel;

import java.util.function.Consumer;

public class MemberJoinEventHandler implements Consumer<MemberJoinEvent> {

    @Override
    public void accept(MemberJoinEvent event) {

        DiscordBot bot = DiscordBot.getInstance();
        String welcomeChannel = bot.getConfig().getChannels().getWelcome();
        String welcomeMessage = String.format(bot.getConfig().getMessages().getWelcome(), event.getMember().getDisplayName());

        event.getGuild().block().getChannels()
                .filter(channel -> (channel instanceof TextChannel && channel.getName().equalsIgnoreCase(welcomeChannel)))
                .map(channel -> (TextChannel) channel)
                .toIterable()
                .forEach(textChannel -> bot.sendMessageSuccess(textChannel, welcomeMessage));
    }
}
