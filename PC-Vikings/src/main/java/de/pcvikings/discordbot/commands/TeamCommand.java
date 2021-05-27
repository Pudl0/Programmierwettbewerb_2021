package de.pcvikings.discordbot.commands;

import de.pcvikings.discordbot.DiscordBot;
import de.pcvikings.discordbot.configs.ChannelConfig;
import de.pcvikings.discordbot.teams.Team;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class TeamCommand {

    public TeamCommand(Message message, User user) {
        String[] commandArray = message.getContentRaw().split(" ");
        Member member = DiscordBot.getInstance().getCurrentGuild().retrieveMember(user).complete();

        boolean inTeam = DiscordBot.getInstance().getTeamManager().getTeam(member.getIdLong()) != null;
        Team team = inTeam ? DiscordBot.getInstance().getTeamManager().getTeam(member.getIdLong()) : null;
        long channelId = inTeam ? team.getChannelId() : ChannelConfig.TEAMCREATIONCHANNEL;

        if(team == null) {
            message.delete().queue();
            if(commandArray[1].equalsIgnoreCase("create")) {
                if(DiscordBot.getInstance().getTeamManager().getTeam(member.getIdLong()) != null || DiscordBot.getInstance().getTeamManager().getTeam(commandArray[2]) != null) {
                    DiscordBot.getInstance().sendEmbedMessage("Fehler", "Das Team existiert bereits oder du bist schon in einem Team!", new Color(235, 36, 26), ChannelConfig.TEAMCREATIONCHANNEL);
                    return;
                }

                DiscordBot.getInstance().getTeamManager().registerTeam(commandArray[2], member.getIdLong());

                DiscordBot.getInstance().sendEmbedMessage("Neues Team", member.getAsMention() + " hat das Team " + commandArray[2] + " erstellt!", new Color(0, 227, 73), ChannelConfig.TEAMREGISTRATIONCHANNEL);
            } else {
                DiscordBot.getInstance().sendEmbedMessage("Ohne Team", "Du bist in keinem Team!", new Color(235, 36, 26), channelId);
            }
        } else {
            TextChannel textChannel = DiscordBot.getInstance().getCurrentGuild().getTextChannelById(channelId);
            if(textChannel == null) {
                return;
            }

            if(commandArray.length == 3) {
                if(commandArray[1].equalsIgnoreCase("add")) {
                    if(team.getTeamLeader() != member.getIdLong()) {
                        DiscordBot.getInstance().sendEmbedMessage("Kein Teamleiter", "Du bist kein Teamleiter!", new Color(235, 36, 26), channelId);
                        return;
                    }

                    if(message.getMentionedMembers().size() != 1) {
                        DiscordBot.getInstance().sendEmbedMessage("Kein Mitglied angegeben", "Du hast kein Mitglied angegeben!", new Color(235, 36, 26), channelId);
                    }
                } else if(commandArray[1].equalsIgnoreCase("kick")) {
                    message.delete().queue();
                    if(team.getTeamLeader() != member.getIdLong()) {
                        DiscordBot.getInstance().sendEmbedMessage("Kein Teamleiter", "Du bist kein Teamleiter!", new Color(235, 36, 26), channelId);
                        return;
                    }

                    if(message.getMentionedMembers().size() != 1 || !team.getTeamMember().contains(message.getMentionedMembers().get(0).getIdLong())) {
                        DiscordBot.getInstance().sendEmbedMessage("Kein Mitglied angegeben", "Du musst ein Mitglied angeben!", new Color(235, 36, 26), channelId);
                        return;
                    }

                    Member kickedMember = message.getMentionedMembers().get(0);
                    team.removeTeamMember(kickedMember.getIdLong());

                    DiscordBot.getInstance().sendEmbedMessage("Kick", kickedMember.getAsMention() + " wurde von " + member.getAsMention() + " gekickt!", new Color(235, 36, 26), channelId);
                }
            } else if(commandArray.length == 2) {
                message.delete().queue();
                if(commandArray[1].equalsIgnoreCase("delete")) {
                    if(team.getTeamLeader() != member.getIdLong()) {
                        DiscordBot.getInstance().sendEmbedMessage("Kein Teamleiter", "Du bist kein Teamleiter!", new Color(235, 36, 26), channelId);
                        return;
                    }

                    DiscordBot.getInstance().sendEmbedMessage("Teamauflösung", "Das Team wurde aufgelöst!", new Color(235, 36, 26), channelId);
                    DiscordBot.getInstance().sendEmbedMessage("Team entfernt", member.getAsMention() + " hat das Team " + team.getTeamName() + " aufgelöst!", new Color(235, 36, 26), ChannelConfig.TEAMREGISTRATIONCHANNEL);
                    DiscordBot.getInstance().getTeamManager().removeTeam(team.getTeamName());
                } else if(commandArray[1].equalsIgnoreCase("leave")) {
                    if(team.getTeamLeader() == member.getIdLong()) {
                        DiscordBot.getInstance().sendEmbedMessage("Als Teamleiter nicht möglich", "Als Teamleiter kannst du das Team nicht verlassen. Nutze !team delete, um das Team zu löschen!", new Color(235, 36, 26), channelId);
                        return;
                    }

                    team.removeTeamMember(member.getIdLong());
                    DiscordBot.getInstance().sendEmbedMessage("Teamleave", member.getAsMention() + " hat das Team verlassen!", new Color(235, 36, 26), channelId);
                } else if(commandArray[1].equalsIgnoreCase("data")) {
                    team.sendDataList();
                } else if(commandArray[1].equalsIgnoreCase("submit")) {
                    if(team.getStatus().equalsIgnoreCase("Anmeldung ausstehend")) {
                        DiscordBot.getInstance().sendEmbedMessage("Anmeldung eingereicht", "Eure Anmeldung wurde erfolgreich eingereicht!", new Color(0, 227, 73), textChannel.getIdLong());
                    }
                } else if(commandArray[1].equalsIgnoreCase("info")) {
                    String infoMessage = "";
                    Member teamLeader = DiscordBot.getInstance().getCurrentGuild().retrieveMemberById(team.getTeamLeader()).complete();
                    infoMessage += "Teamleiter: " + teamLeader.getAsMention() + "\nTeammitglieder:";
                    for(long teamMember : team.getTeamMember()) {
                        Member retrievedTeamMember = DiscordBot.getInstance().getCurrentGuild().retrieveMemberById(teamMember).complete();
                        infoMessage += "\n" + retrievedTeamMember.getAsMention();
                    }
                    infoMessage += "\nStatus des Teams: " + team.getStatus();

                    DiscordBot.getInstance().sendEmbedMessage("**Informationen zum Team " + team.getTeamName() + "**", infoMessage, new Color(0,111,255), textChannel.getIdLong());
                }
            } else if(commandArray.length > 3) {
                message.delete().queue();
                if(commandArray[1].equalsIgnoreCase("data")) {
                    if(!team.getStatus().equalsIgnoreCase("Anmeldung ausstehend")) {
                        DiscordBot.getInstance().sendEmbedMessage("Anmeldung bereits eingereicht", "Eure Anmeldung ist bereits eingereicht. Melde dich bei einem Administrator, um die Daten wieder bearbeiten zu können!", new Color(0,111,255), textChannel.getIdLong());
                        return;
                    }

                    if(commandArray[2].equalsIgnoreCase("add")) {
                        String newData = "";
                        for(int i = 3; i < commandArray.length; i++) {
                            newData += commandArray[i] + " ";
                        }
                        team.getData().add(newData);

                        team.save();
                    } else if(commandArray[2].equalsIgnoreCase("remove")) {
                        team.getData().remove(Integer.parseInt(commandArray[3])-1);
                        team.save();
                    }
                }
            }
        }
    }
}
