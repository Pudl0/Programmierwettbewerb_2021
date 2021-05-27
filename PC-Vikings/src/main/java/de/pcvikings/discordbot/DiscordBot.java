package de.pcvikings.discordbot;

import de.pcvikings.discordbot.configs.*;
import de.pcvikings.discordbot.manager.DatabaseManager;
import de.pcvikings.discordbot.teams.TeamManager;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.HashMap;

public class DiscordBot {

    public static DiscordBot instance;

    @Getter
    private TeamManager teamManager;
    @Getter
    private DatabaseManager databaseManager;

    @Getter
    private ChannelConfig channelConfig;
    @Getter
    private EasyCommandConfig easyCommandConfig;
    @Getter
    private FilterConfig filterConfig;

    @Getter
    private JDA jda;

    @Getter
    private Guild currentGuild;

    public DiscordBot(JDA jda) {
        instance = this;

        this.jda = jda;

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        databaseManager = new DatabaseManager(DatabaseConfig.HOST, DatabaseConfig.PORT, DatabaseConfig.DATABASE, DatabaseConfig.USERNAME, DatabaseConfig.PASSWORD);
        teamManager = new TeamManager();
        loadConfigs();

        this.currentGuild = this.jda.getGuildById(ChannelConfig.GUILDID); //CAN'T CHANGE AT THE MOMENT
        RoleConfig.initialiseRoles();
    }

    public void loadConfigs() {
        this.channelConfig = new ChannelConfig();
        this.channelConfig.load();

        this.easyCommandConfig = new EasyCommandConfig();
        this.easyCommandConfig.load();

        this.filterConfig = new FilterConfig();
        this.filterConfig.load();
    }

    public void reloadConfigs() {
        this.easyCommandConfig.reload();
        this.filterConfig.reload();
    }

    public void setupServer() { //DID NOT FINISH
        HashMap<String, Long> newChannels = new HashMap<>();

        this.channelConfig.getChannelMap().forEach((name, id) -> {
            if(id == -1) {
                TextChannel channel = this.getCurrentGuild().createTextChannel(name).complete();
                newChannels.put(name, channel.getIdLong());
            }
        });

        newChannels.forEach((name, id) -> {
            this.channelConfig.getChannelMap().remove(name);
            this.channelConfig.getChannelMap().put(name, id);
        });

        newChannels.clear();

        this.channelConfig.save();
    }

    public static DiscordBot getInstance() {
        return instance;
    }

    public void sendEmbedMessage(String title, String text, Color color, long textChannelId) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(title);
        embedBuilder.setDescription(text);
        embedBuilder.setColor(color);

        TextChannel textChannel = this.getCurrentGuild().getTextChannelById(textChannelId);
        if(textChannel == null) {
            return;
        }
        textChannel.sendMessage(embedBuilder.build()).queue();
    }
}
