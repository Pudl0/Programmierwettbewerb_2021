package de.pauhull.discordbot.bot.handler;

import de.pauhull.discordbot.bot.DiscordBot;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;

import java.util.function.Consumer;

public class ReadyEventHandler implements Consumer<ReadyEvent> {

    @Override
    public void accept(ReadyEvent event) {

        DiscordBot bot = DiscordBot.getInstance();
        User self = event.getSelf();
        bot.setConsolePrefix(self.getUsername());
        bot.log("Successfully connected to bot");
    }
}
