package de.pauhull.discordbot.bot.commands;

import de.pauhull.discordbot.bot.DiscordBot;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import org.apache.commons.validator.routines.EmailValidator;

public class ApplicationCommand implements Command {

    @Override
    public void execute(MessageChannel channel, Member member, Message message, String label, String[] args) {

        DiscordBot bot = DiscordBot.getInstance();
        String prefix = bot.getConfig().getCommands().getPrefix();

        if (member != null) {
            message.delete().block();
            bot.sendMessageError(channel, bot.getConfig().getMessages().getOnlyPrivateChat());
            return;
        }

        if (args.length < 3) {
            bot.sendMessageError(channel, "Verwendung: " + prefix + getLabel() + " <Email> <Vorname Nachname>");
            return;
        }

        String email = args[0];
        if (!EmailValidator.getInstance().isValid(email)) {
            bot.sendMessageError(channel, bot.getConfig().getMessages().getInvalidEmail());
            return;
        }

        if (bot.getApplicationManager().getApplication(email) != null) {
            bot.sendMessageError(channel, bot.getConfig().getMessages().getAlreadyRegistered());
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(args[i]);
        }
        String name = builder.toString();

        bot.getApplicationManager().addApplication(email, name);
        bot.sendMessageSuccess(channel, bot.getConfig().getMessages().getApplicationSuccessful());
    }

    @Override
    public String getLabel() {
        return DiscordBot.getInstance().getConfig().getCommands().getApplication();
    }

    @Override
    public String getDesc() {
        return DiscordBot.getInstance().getConfig().getCommands().getApplicationDesc();
    }
}
