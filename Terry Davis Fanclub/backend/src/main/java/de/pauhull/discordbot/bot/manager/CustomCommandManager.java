package de.pauhull.discordbot.bot.manager;

import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.bot.commands.Command;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomCommandManager {

    private File customCommandFile;
    @Getter
    private List<CustomCommand> customCommands;

    public CustomCommandManager() {

        this.customCommandFile = new File("customCommands.json");
        this.customCommands = this.load();
    }

    public CustomCommand getCommand(String name) {

        for (CustomCommand command : customCommands) {
            if (command.getLabel().equalsIgnoreCase(name)) {
                return command;
            }
        }

        return null;
    }

    private List<CustomCommand> load() {

        List<CustomCommand> customCommands = new ArrayList<>();
        if (!customCommandFile.exists()) {
            customCommands.add(new CustomCommand("twitch", "Unser Twitch-Kanal \uD83D\uDCFA",
                    "\uD83D\uDCFA Hier kommst du zu unserem Twitch-Kanal: https://www.twitch.tv/hackathonleer"));
            return customCommands;
        }

        try {
            String content = Files.lines(customCommandFile.toPath()).collect(Collectors.joining("\n"));
            CustomCommand[] customCommandArray = DiscordBot.getInstance().getGson().fromJson(content, CustomCommand[].class);
            customCommands.addAll(Arrays.asList(customCommandArray));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return customCommands;
    }

    public void save() {

        String content = DiscordBot.getInstance().getGson().toJson(customCommands);

        try {
            Files.write(customCommandFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class CustomCommand implements Command {

        private String label;
        private String desc;
        private String response;

        @Override
        public void execute(MessageChannel channel, Member member, Message message, String label, String[] args) {
            DiscordBot.getInstance().sendMessageNeutral(channel, response);
        }
    }
}
