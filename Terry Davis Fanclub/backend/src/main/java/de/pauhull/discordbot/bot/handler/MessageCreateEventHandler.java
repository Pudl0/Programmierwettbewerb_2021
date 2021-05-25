package de.pauhull.discordbot.bot.handler;

import de.pauhull.discordbot.bot.DiscordBot;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

import java.util.function.Consumer;

public class MessageCreateEventHandler implements Consumer<MessageCreateEvent> {

    @Override
    public void accept(MessageCreateEvent event) {

        DiscordBot bot = DiscordBot.getInstance();
        Message messageObject = event.getMessage();
        String message = messageObject.getContent();
        Member member = event.getMember().orElse(null);
        MessageChannel channel = event.getMessage().getChannel().block();

        if (message.startsWith(bot.getConfig().getCommands().getPrefix())) {
            String command = message.substring(1);
            String[] parts = command.split(" ");
            String label = parts[0];
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, args.length);
            bot.getCommandManager().runCommand(channel, member, messageObject, label, args);
        }
    }
}
