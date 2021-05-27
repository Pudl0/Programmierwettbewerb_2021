package de.pauhull.discordbot.bot.commands;

import de.pauhull.discordbot.bot.DiscordBot;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

public class TeamsCommand implements Command {

    @Override
    public void execute(MessageChannel channel, Member member, Message message, String label, String[] args) {

        DiscordBot bot = DiscordBot.getInstance();
        StringBuilder builder = new StringBuilder();

        if (bot.getTeamManager().getTeams().isEmpty()) {
            builder.append(bot.getConfig().getMessages().getNoTeams());
        } else {
            builder.append(bot.getConfig().getMessages().getAllTeams());
            bot.getTeamManager().getTeams()
                    .forEach(team -> builder.append("\n").append(team.getName()).append(" (")
                            .append(team.getOwnerName()).append("), ").append(team.getMembers().size())
                            .append(team.getMembers().size() == 1 ? " Mitglied" : " Mitglieder"));
        }

        bot.sendMessageNeutral(channel, builder.toString());
    }

    @Override
    public String getLabel() {
        return DiscordBot.getInstance().getConfig().getCommands().getTeams();
    }

    @Override
    public String getDesc() {
        return DiscordBot.getInstance().getConfig().getCommands().getTeamsDesc();
    }
}
