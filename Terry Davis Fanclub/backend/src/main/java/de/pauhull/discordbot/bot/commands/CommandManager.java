package de.pauhull.discordbot.bot.commands;

import de.pauhull.discordbot.bot.DiscordBot;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandManager {

    @Getter
    private List<Command> commands;
    private ExecutorService executorService;

    public CommandManager() {

        this.executorService = Executors.newCachedThreadPool();
        this.commands = new ArrayList<>();
        this.commands.add(new HelpCommand());
        this.commands.add(new JoinCommand());
        this.commands.add(new LeaveCommand());
        this.commands.add(new JokeCommand());
        this.commands.add(new StackOverflowCommand());
        this.commands.add(new ApplicationCommand());
        this.commands.add(new CreateTeamCommand());
        this.commands.add(new TicTacToeCommand());
        this.commands.add(new PasteCommand());
    }

    public void runCommand(MessageChannel channel, Member member, Message messageObject, String label, String[] args) {

        DiscordBot bot = DiscordBot.getInstance();
        Command command = null;
        for (Command check : commands) {
            if (check.getLabel().equalsIgnoreCase(label)) {
                command = check;
                break;
            }
        }
        for (Command check : bot.getCustomCommandManager().getCustomCommands()) {
            if (check.getLabel().equalsIgnoreCase(label)) {
                command = check;
                break;
            }
        }

        if (command != null) {
            Command finalCommand = command;
            executorService.execute(() -> finalCommand.execute(channel, member, messageObject, label, args));
            return;
        }

        bot.sendMessageError(channel, DiscordBot.getInstance().getConfig().getMessages().getCommandNotFound());
    }
}
