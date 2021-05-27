package de.pauhull.discordbot.bot.commands;

import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.bot.manager.TeamManager;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

import java.util.List;

public class LeaveCommand implements Command {

    @Override
    public void execute(MessageChannel channel, Member member, Message message, String label, String[] args) {

        DiscordBot bot = DiscordBot.getInstance();
        String prefix = bot.getConfig().getCommands().getPrefix();

        if (member == null) {
            bot.sendMessageError(channel, bot.getConfig().getMessages().getNoPrivateMessage());
            return;
        }

        List<TeamManager.Team> teams = bot.getTeamManager().getTeams(member);
        TeamManager.Team teamToRemove;

        if (args.length > 0 || teams.size() > 1) {
            if (args.length < 1) {
                bot.sendMessageError(channel, "Verwendung: " + prefix + getLabel() + " <Team-Name>");
                return;
            }

            String teamName = String.join(" ", args);
            teamToRemove = bot.getTeamManager().getTeam(teamName);
            if (teamToRemove == null) {
                bot.sendMessageError(channel, bot.getConfig().getMessages().getTeamDoesntExist());
                return;
            }
        } else {
            if (teams.size() == 0) {
                bot.sendMessageError(channel, bot.getConfig().getMessages().getNotInTeam());
                return;

            } else {
                teamToRemove = teams.get(0);
            }
        }

        if (teamToRemove.getOwner() != null && member.getId().asString().equals(teamToRemove.getOwner())) {
            bot.getTeamManager().removeTeam(teamToRemove);
            bot.sendMessageSuccess(channel, bot.getConfig().getMessages().getTeamDeleted());
        } else {
            bot.getTeamManager().unassignTeam(member, teamToRemove);
            bot.sendMessageSuccess(channel, String.format(bot.getConfig().getMessages().getTeamRemoved(), teamToRemove.getName()));
        }
    }

    @Override
    public String getLabel() {
        return DiscordBot.getInstance().getConfig().getCommands().getLeave();
    }

    @Override
    public String getDesc() {
        return DiscordBot.getInstance().getConfig().getCommands().getLeaveDesc();
    }
}
