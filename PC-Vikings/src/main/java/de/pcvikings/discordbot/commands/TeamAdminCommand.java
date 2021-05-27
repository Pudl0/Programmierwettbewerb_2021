package de.pcvikings.discordbot.commands;

import de.pcvikings.discordbot.DiscordBot;
import de.pcvikings.discordbot.configs.ChannelConfig;
import de.pcvikings.discordbot.teams.Team;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class TeamAdminCommand {

    public TeamAdminCommand(Message message, User user) {
        String[] commandArray = message.getContentRaw().split(" ");
        Member member = DiscordBot.getInstance().getCurrentGuild().retrieveMember(user).complete();

        if(commandArray.length == 2) {
            if(commandArray[1].equalsIgnoreCase("list")) {
                String teamList = "";

                for(Team team : DiscordBot.getInstance().getTeamManager().getTeams().values()) {
                    Member teamLeader = DiscordBot.getInstance().getCurrentGuild().retrieveMemberById(team.getTeamLeader()).complete();
                    teamList += "**" + team.getTeamName() + "** \n Teamleiter: " + teamLeader.getAsMention() + "\n" +
                            "Status: " + team.getStatus() + "\n";
                }

                DiscordBot.getInstance().sendEmbedMessage("Auflistung aller Teams", teamList, new Color(0,111,255), ChannelConfig.TEAMREGISTRATIONCHANNEL);
            }
        } else if (commandArray.length == 3) {
            if (commandArray[1].equalsIgnoreCase("info")) {
                if (!DiscordBot.getInstance().getTeamManager().isTeamExisting(commandArray[2].toLowerCase())) {
                    DiscordBot.getInstance().getCurrentGuild().getTextChannelById(ChannelConfig.TEAMREGISTRATIONCHANNEL).sendMessage("Das Team ``" + commandArray[2] + "`` existiert nicht!").queue();
                    return;
                }

                Team team = DiscordBot.getInstance().getTeamManager().getTeam(commandArray[2]);
                Member teamLeader = DiscordBot.getInstance().getCurrentGuild().retrieveMemberById(team.getTeamLeader()).complete();
                String infoMessage = "";
                infoMessage += "Teamleiter: " + teamLeader.getAsMention() + "\nTeammitglieder:";
                for (long teamMember : team.getTeamMember()) {
                    Member retrievedTeamMember = DiscordBot.getInstance().getCurrentGuild().retrieveMemberById(teamMember).complete();
                    infoMessage += "\n" + retrievedTeamMember.getAsMention();
                }
                infoMessage += "\nStatus des Teams: " + team.getStatus();

                DiscordBot.getInstance().sendEmbedMessage("**Informationen zum Team " + team.getTeamName() + "**", infoMessage, new Color(0,111,255), ChannelConfig.TEAMREGISTRATIONCHANNEL);
            } else if (commandArray[1].equalsIgnoreCase("delete")) {
                if (!DiscordBot.getInstance().getTeamManager().isTeamExisting(commandArray[2].toLowerCase())) {
                    DiscordBot.getInstance().getCurrentGuild().getTextChannelById(ChannelConfig.TEAMREGISTRATIONCHANNEL).sendMessage("Das Team ``" + commandArray[2] + "`` existiert nicht!").queue();
                    return;
                }

                Team team = DiscordBot.getInstance().getTeamManager().getTeam(commandArray[2]);

                DiscordBot.getInstance().sendEmbedMessage("Team entfernt", member.getAsMention() + " hat das Team " + team.getTeamName() + " administrativ aufgelöst!", new Color(235, 36, 26), ChannelConfig.TEAMREGISTRATIONCHANNEL);
                DiscordBot.getInstance().getTeamManager().removeTeam(team.getTeamName());
            } else if (commandArray[1].equalsIgnoreCase("data")) {
                if (!DiscordBot.getInstance().getTeamManager().isTeamExisting(commandArray[2].toLowerCase())) {
                    DiscordBot.getInstance().getCurrentGuild().getTextChannelById(ChannelConfig.TEAMREGISTRATIONCHANNEL).sendMessage("Das Team ``" + commandArray[2] + "`` existiert nicht!").queue();
                    return;
                }

                Team team = DiscordBot.getInstance().getTeamManager().getTeam(commandArray[2]);

                String teamData = "";

                for(int i = 0; i < team.getData().size(); i++) {
                    teamData += (i+1) + ". " + team.getData().get(i) + "\n";
                }

                DiscordBot.getInstance().sendEmbedMessage("Daten des Teams " + team.getTeamName() + ":", teamData, new Color(0,111,255), ChannelConfig.TEAMREGISTRATIONCHANNEL);
            }  else if(commandArray[1].equalsIgnoreCase("approve")) {
                if (!DiscordBot.getInstance().getTeamManager().isTeamExisting(commandArray[2].toLowerCase())) {
                    DiscordBot.getInstance().getCurrentGuild().getTextChannelById(ChannelConfig.TEAMREGISTRATIONCHANNEL).sendMessage("Das Team ``" + commandArray[2] + "`` existiert nicht!").queue();
                    return;
                }

                Team team = DiscordBot.getInstance().getTeamManager().getTeam(commandArray[2]);

                if(team.getStatus().equalsIgnoreCase("Anmeldung eingereicht")) {
                    team.setStatus("Anmeldung erfolgreich");
                    DiscordBot.getInstance().getCurrentGuild().getTextChannelById(ChannelConfig.TEAMREGISTRATIONCHANNEL).sendMessage("Das Team ``" + commandArray[2] + "`` ist nun angemeldet!").queue();
                    DiscordBot.getInstance().getCurrentGuild().getTextChannelById(team.getChannelId()).sendMessage("Eure Teamanmeldung wurde erfolgreich bearbeitet!").queue();
                } else {
                    DiscordBot.getInstance().getCurrentGuild().getTextChannelById(ChannelConfig.TEAMREGISTRATIONCHANNEL).sendMessage("Die Teamanmeldung für ``" + commandArray[2] + "`` ist nicht eingereicht!").queue();
                }
            } else if(commandArray[1].equalsIgnoreCase("reset")) {
                if (!DiscordBot.getInstance().getTeamManager().isTeamExisting(commandArray[2].toLowerCase())) {
                    DiscordBot.getInstance().getCurrentGuild().getTextChannelById(ChannelConfig.TEAMREGISTRATIONCHANNEL).sendMessage("Das Team ``" + commandArray[2] + "`` existiert nicht!").queue();
                    return;
                }

                Team team = DiscordBot.getInstance().getTeamManager().getTeam(commandArray[2]);

                if(team.getStatus().equalsIgnoreCase("Anmeldung erfolgreich")) {
                    team.setStatus("Anmeldung ausstehend");
                    DiscordBot.getInstance().getCurrentGuild().getTextChannelById(ChannelConfig.TEAMREGISTRATIONCHANNEL).sendMessage("Das Team ``" + commandArray[2] + "`` kann nun wieder bearbeitet werden!").queue();
                    DiscordBot.getInstance().getCurrentGuild().getTextChannelById(team.getChannelId()).sendMessage("Euer Team kann nun wieder bearbeitet werden!").queue();
                } else {
                    DiscordBot.getInstance().getCurrentGuild().getTextChannelById(ChannelConfig.TEAMREGISTRATIONCHANNEL).sendMessage("Die Teamanmeldung für ``" + commandArray[2] + "`` ist noch nicht bestätigt!").queue();
                }
            }
        } else if(commandArray.length >= 3) {
            if(commandArray[1].equalsIgnoreCase("list")) {
                String status = "";
                for(int i = 2; i < commandArray.length; i++) {
                    status += commandArray[i] + " ";
                }
                status = status.substring(0, status.length()-1);

                String teamList = "";

                for(Team team : DiscordBot.getInstance().getTeamManager().getTeamWithStatus(status)) {
                    Member teamLeader = DiscordBot.getInstance().getCurrentGuild().retrieveMemberById(team.getTeamLeader()).complete();
                    teamList += "**" + team.getTeamName() + "** \n Teamleiter: " + teamLeader.getAsMention() + "\n" +
                            "Status: " + team.getStatus() + "\n";
                }

                DiscordBot.getInstance().sendEmbedMessage("Auflistung aller Teams (" + status + ")", teamList, new Color(0,111,255), ChannelConfig.TEAMREGISTRATIONCHANNEL);
            }
        }
    }
}
