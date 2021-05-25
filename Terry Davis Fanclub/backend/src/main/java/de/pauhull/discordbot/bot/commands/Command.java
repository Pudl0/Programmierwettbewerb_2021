package de.pauhull.discordbot.bot.commands;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

public interface Command {

    void execute(MessageChannel channel, Member member, Message message, String label, String[] args);

    String getLabel();

    String getDesc();
}
