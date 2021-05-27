package de.pcvikings.discordbot.listeners;

import de.pcvikings.discordbot.DiscordBot;
import de.pcvikings.discordbot.configs.ChannelConfig;
import de.pcvikings.discordbot.teams.Team;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class GuildMemberLeaveListener extends ListenerAdapter {

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        Member member = event.getMember();

        if(DiscordBot.getInstance().getTeamManager().getTeam(member.getIdLong()) == null) {
            return;
        }

        Team team = DiscordBot.getInstance().getTeamManager().getTeam(member.getIdLong());

        if(team.getTeamLeader() == event.getMember().getIdLong()) {
            DiscordBot.getInstance().sendEmbedMessage("Team entfernt", member.getAsMention() + " hat das Team " + team.getTeamName() + " durch Verlassen aufgelöst!", new Color(235, 36, 26), ChannelConfig.TEAMREGISTRATIONCHANNEL);
            DiscordBot.getInstance().getTeamManager().removeTeam(team.getTeamName());
        } else {
            team.removeTeamMember(event.getMember().getIdLong());
        }
    }

}
