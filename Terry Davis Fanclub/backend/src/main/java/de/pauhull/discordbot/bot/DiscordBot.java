package de.pauhull.discordbot.bot;

import com.google.gson.Gson;
import de.pauhull.discordbot.Config;
import de.pauhull.discordbot.bot.commands.CommandManager;
import de.pauhull.discordbot.bot.github.GithubChecker;
import de.pauhull.discordbot.bot.handler.*;
import de.pauhull.discordbot.bot.manager.ApplicationManager;
import de.pauhull.discordbot.bot.manager.TeamManager;
import de.pauhull.discordbot.bot.paste.HasteService;
import de.pauhull.discordbot.bot.tictactoe.TicTacToe;
import de.pauhull.discordbot.bot.twitch.TwitchChecker;
import de.pauhull.discordbot.util.RemoteAddressRetriever;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.role.RoleUpdateEvent;
import discord4j.core.object.Embed;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.util.Color;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class DiscordBot {

    @Getter
    private static DiscordBot instance;
    @Getter
    private GatewayDiscordClient client;
    @Getter
    private String remoteAddress;
    @Getter
    @Setter
    private Config config;
    @Getter
    private Gson gson;
    @Setter
    private String consolePrefix;
    @Getter
    private GithubChecker githubChecker;
    @Getter
    private TwitchChecker twitchChecker;
    @Getter
    private TeamManager teamManager;
    @Getter
    private ApplicationManager applicationManager;
    @Getter
    private CommandManager commandManager;
    @Getter
    private List<String> jokes;
    @Getter
    private TicTacToe ticTacToe;
    @Getter
    private HasteService hasteService;

    public DiscordBot(Config config, Gson gson, Runnable onReady) {

        instance = this;
        this.remoteAddress = RemoteAddressRetriever.getRemoteAddress();

        try {
            this.client = Objects.requireNonNull(DiscordClientBuilder.create(config.getDiscord().getToken())
                    .build()
                    .login().block());

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }

        this.config = config;
        this.gson = gson;
        this.teamManager = new TeamManager();
        this.applicationManager = new ApplicationManager();
        this.githubChecker = new GithubChecker();
        this.twitchChecker = new TwitchChecker();
        this.commandManager = new CommandManager();
        this.ticTacToe = new TicTacToe();
        this.jokes = loadJokes();
        this.hasteService = new HasteService();

        client.getEventDispatcher().on(MemberJoinEvent.class).subscribe(new MemberJoinEventHandler());
        client.getEventDispatcher().on(ReadyEvent.class).subscribe(new ReadyEventHandler());
        client.getEventDispatcher().on(ReadyEvent.class).subscribe(e -> onReady.run());
        client.getEventDispatcher().on(GuildCreateEvent.class).subscribe(new GuildCreateEventHandler());
        client.getEventDispatcher().on(MessageCreateEvent.class).subscribe(new MessageCreateEventHandler());
        client.getEventDispatcher().on(ReactionAddEvent.class).subscribe(new ReactionAddEventHandler());
        client.getEventDispatcher().on(GuildDeleteEvent.class).subscribe(new GuildDeleteEventHandler());
        client.getEventDispatcher().on(RoleUpdateEvent.class).subscribe(new RoleUpdateEventHandler());

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        client.onDisconnect().block();
    }

    public void inTextChannels(String channelName, Consumer<TextChannel> consumer) {

        client.getGuilds()
                .flatMap(Guild::getChannels)
                .filter(channel -> channel instanceof TextChannel && channel.getName().equalsIgnoreCase(channelName))
                .map(channel -> (TextChannel) channel)
                .toIterable().forEach(consumer);
    }

    public Message sendMessageNeutral(MessageChannel channel, String message) {
        return this.sendMessage(channel, message, Color.GRAY);
    }

    public Message sendMessageSuccess(MessageChannel channel, String message) {
        return this.sendMessage(channel, message, Color.GREEN);
    }

    public Message sendMessageError(MessageChannel channel, String message) {
        return this.sendMessage(channel, message, Color.RED);
    }

    public Message sendMessage(MessageChannel channel, String message, Color color) {

        String description = message.replace("\\n", "\n");
        if (description.length() > Embed.MAX_DESCRIPTION_LENGTH) {
            description = description.substring(0, Embed.MAX_DESCRIPTION_LENGTH);
        }
        String descriptionFinal = description;

        return channel.createEmbed(spec -> {
            spec.setDescription(descriptionFinal)
                    .setColor(color);
        }).block();
    }

    public void log(String message) {
        System.out.printf("[%s] %s%n", consolePrefix, message);
    }

    private void stop() {
        log("Stopping bot");
        teamManager.getJoinRequests().forEach(request -> request.getMessage().delete().block());
        teamManager.getJoinRequests().clear();
        ticTacToe.getRequests().forEach(request -> request.getMessage().delete());
        ticTacToe.getRequests().clear();
    }

    private List<String> loadJokes() {

        File jokesFile = new File("jokes.txt");
        if (!jokesFile.exists()) {
            try {
                Files.copy(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("jokes.txt")), jokesFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            return Files.readAllLines(jokesFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
