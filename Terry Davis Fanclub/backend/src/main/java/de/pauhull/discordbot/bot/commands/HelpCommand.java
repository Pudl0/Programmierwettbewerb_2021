package de.pauhull.discordbot.bot.commands;

import de.pauhull.discordbot.bot.DiscordBot;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

import java.util.function.Consumer;

public class HelpCommand implements Command {

    @Override
    public void execute(MessageChannel channel, Member member, Message message, String label, String[] args) {

        DiscordBot bot = DiscordBot.getInstance();
        String prefix = bot.getConfig().getCommands().getPrefix();

        StringBuilder builder = new StringBuilder(bot.getConfig().getMessages().getHelpMessage());
        Consumer<Command> append = command -> builder.append("\n").append(prefix).append(command.getLabel()).append(" - ").append(command.getDesc());
        bot.getCommandManager().getCommands().forEach(append);
        bot.getCustomCommandManager().getCustomCommands().forEach(append);

        bot.sendMessageNeutral(channel, builder.toString());
    }

    @Override
    public String getLabel() {
        return DiscordBot.getInstance().getConfig().getCommands().getHelp();
    }

    @Override
    public String getDesc() {
        return DiscordBot.getInstance().getConfig().getCommands().getHelpDesc();
    }
}
