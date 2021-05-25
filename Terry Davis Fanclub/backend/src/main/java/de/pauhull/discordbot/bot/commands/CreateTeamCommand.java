package de.pauhull.discordbot.bot.commands;

import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.bot.manager.TeamManager;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

import java.awt.*;
import java.util.Random;

public class CreateTeamCommand implements Command {

    private String[] colors;
    private Random random;

    public CreateTeamCommand() {

        this.colors = new String[]{"#f44336", "#e91e63", "#9c27b0", "#673ab7", "#3f51b5", "#2196f3", "#03a9f4", "#00bcd4",
                "#009688", "#4caf50", "#8bc34a", "#cddc39", "#ffeb3b", "#ffc107", "#ff9800", "#ff5722", "#795548", "#607d8b"};
        this.random = new Random();
    }

    @Override
    public void execute(MessageChannel channel, Member member, Message message, String label, String[] args) {

        DiscordBot bot = DiscordBot.getInstance();
        String prefix = bot.getConfig().getCommands().getPrefix();

        if (member == null) {
            bot.sendMessageError(channel, bot.getConfig().getMessages().getNoPrivateMessage());
            return;
        }

        if (args.length < 1) {
            bot.sendMessageError(channel, "Verwendung: " + prefix + getLabel() + " <Team-Name> <Optional: Farbe in Hex (Beispiel: #ff4242)>");
            return;
        }

        String color = colors[random.nextInt(colors.length)];
        int teamNameArgsLength = args.length;
        if (args.length > 1) {
            try {
                if (!color.startsWith("#") || color.length() != 7) throw new NumberFormatException();
                Color.decode(args[args.length - 1]);
                color = args[args.length - 1];
                teamNameArgsLength--;
            } catch (NumberFormatException ignored) {
            }
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < teamNameArgsLength; i++) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(args[i]);
        }
        String teamName = builder.toString();

        if (bot.getTeamManager().getTeam(teamName) != null) {
            bot.sendMessageError(channel, bot.getConfig().getMessages().getTeamAlreadyExists());
            return;
        }

        TeamManager.Team team = bot.getTeamManager().addTeam(teamName, color, member.getId().asLong());
        if (team != null) {
            bot.getTeamManager().assignTeam(member, team);
            bot.sendMessageSuccess(channel, bot.getConfig().getMessages().getTeamCreated());
        } else {
            bot.sendMessageError(channel, bot.getConfig().getMessages().getTeamAlreadyExists());
        }
    }

    @Override
    public String getLabel() {
        return DiscordBot.getInstance().getConfig().getCommands().getCreateTeam();
    }

    @Override
    public String getDesc() {
        return DiscordBot.getInstance().getConfig().getCommands().getCreateTeamDesc();
    }
}
