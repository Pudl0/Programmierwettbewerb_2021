package de.pauhull.discordbot.bot.commands;

import de.pauhull.discordbot.bot.DiscordBot;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

import java.util.Random;

public class JokeCommand implements Command {

    private Random random;

    public JokeCommand() {
        this.random = new Random();
    }

    @Override
    public void execute(MessageChannel channel, Member member, Message message, String label, String[] args) {

        DiscordBot bot = DiscordBot.getInstance();
        String joke = bot.getJokes().get(random.nextInt(bot.getJokes().size()));
        bot.sendMessageNeutral(channel, joke);
    }

    @Override
    public String getLabel() {
        return DiscordBot.getInstance().getConfig().getCommands().getJoke();
    }

    @Override
    public String getDesc() {
        return DiscordBot.getInstance().getConfig().getCommands().getJokeDesc();
    }
}
