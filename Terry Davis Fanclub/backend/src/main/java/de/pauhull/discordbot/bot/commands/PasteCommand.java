package de.pauhull.discordbot.bot.commands;

import de.pauhull.discordbot.bot.DiscordBot;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

public class PasteCommand implements Command {

    @Override
    public void execute(MessageChannel channel, Member member, Message message, String label, String[] args) {

        DiscordBot bot = DiscordBot.getInstance();
        String prefix = bot.getConfig().getCommands().getPrefix();

        if (args.length < 1) {
            bot.sendMessageError(channel, "Verwendung: " + prefix + getLabel() + " <Quellcode>");
            return;
        }

        String content = String.join(" ", args);
        String url = bot.getHasteService().paste(content);
        bot.sendMessageSuccess(channel, String.format(bot.getConfig().getMessages().getSendLink(), url));
        bot.log("Created paste %s", url);
    }

    @Override
    public String getLabel() {
        return DiscordBot.getInstance().getConfig().getCommands().getPaste();
    }

    @Override
    public String getDesc() {
        return DiscordBot.getInstance().getConfig().getCommands().getPasteDesc();
    }
}
