package de.pcvikings.discordbot.listeners;

import de.pcvikings.discordbot.DiscordBot;
import de.pcvikings.discordbot.configs.ChannelConfig;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class GuildMemberJoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        String name = event.getMember().getNickname() == null ? event.getMember().getEffectiveName() : event.getMember().getNickname();

        String message = "Willkommen beim Schüler-Hackathon 2021, " + name + "!";
        DiscordBot.getInstance().sendEmbedMessage("Willkommen", message, new Color(0, 227, 73), ChannelConfig.WELCOMECHANNEL);
    }

}
