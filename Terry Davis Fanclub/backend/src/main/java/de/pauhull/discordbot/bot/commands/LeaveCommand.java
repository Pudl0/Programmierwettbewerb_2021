package de.pauhull.discordbot.bot.commands;

import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.bot.manager.TeamManager;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.MessageChannel;

public class LeaveCommand implements Command {

    @Override
    public void execute(MessageChannel channel, Member member, Message message, String label, String[] args) {

        DiscordBot bot = DiscordBot.getInstance();

        if (member == null) {
            bot.sendMessageError(channel, bot.getConfig().getMessages().getNoPrivateMessage());
            return;
        }

        String removedTeam = null;
        for (Role role : member.getRoles().toIterable()) {
            if (bot.getTeamManager().getTeam(role.getName()) != null) {
                member.removeRole(role.getId()).block();
                removedTeam = role.getName();
            }
        }

        TeamManager.Team team = bot.getTeamManager().getTeam(removedTeam);
        if (team != null) {
            if (member.getId().asLong() == team.getOwner()) {
                bot.getTeamManager().removeTeam(team);
                bot.sendMessageSuccess(channel, bot.getConfig().getMessages().getTeamDeleted());
            } else {
                bot.sendMessageSuccess(channel, String.format(bot.getConfig().getMessages().getTeamRemoved(), removedTeam));
            }
        } else {
            bot.sendMessageError(channel, bot.getConfig().getMessages().getNotInTeam());
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
