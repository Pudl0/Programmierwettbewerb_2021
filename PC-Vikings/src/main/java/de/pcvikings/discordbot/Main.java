package de.pcvikings.discordbot;

import de.pcvikings.discordbot.listeners.GuildMemberJoinListener;
import de.pcvikings.discordbot.listeners.GuildMemberLeaveListener;
import de.pcvikings.discordbot.listeners.MessageReceiveListener;
import de.pcvikings.discordbot.listeners.ReactionListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Main {

    public static DiscordBot bot;

    public static final String TOKEN = "ODQ0MTk4NTk2NjY4MjkzMTkx.YKO7NQ.E0HbGp0hOGmdWWBZruR7FvaP-8Y";

    public static void main(String[] args) throws LoginException {
        JDA jda = JDABuilder.createDefault(TOKEN)
                .setActivity(Activity.watching("dem Entwickler zu"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();

        jda.addEventListener(new MessageReceiveListener());
        jda.addEventListener(new ReactionListener());
        jda.addEventListener(new GuildMemberJoinListener());
        jda.addEventListener(new GuildMemberLeaveListener());

        bot = new DiscordBot(jda);
    }
}
