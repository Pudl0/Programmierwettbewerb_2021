package de.pauhull.discordbot.bot.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.pauhull.discordbot.bot.DiscordBot;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.stream.Collectors;

public class MemeCommand implements Command {

    private Random random;

    public MemeCommand() {
        this.random = new Random();
    }

    @Override
    public void execute(MessageChannel channel, Member member, Message message, String label, String[] args) {

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://www.reddit.com/r/ProgrammerHumor/top/.json").openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36");
            String content = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            JsonObject object = JsonParser.parseString(content).getAsJsonObject();
            JsonArray posts = object.get("data").getAsJsonObject().get("children").getAsJsonArray();

            JsonObject post;
            String title, author, postHint, url, img;
            boolean nsfw;
            do {
                post = posts.get(random.nextInt(posts.size())).getAsJsonObject().get("data").getAsJsonObject();
                title = post.get("title").getAsString();
                author = post.get("author").getAsString();
                nsfw = post.get("over_18").getAsBoolean();
                url = "https://reddit.com" + post.get("permalink").getAsString();
                img = post.get("url").getAsString();
                postHint = post.get("post_hint").getAsString();
            } while (nsfw || !postHint.equals("image"));

            String finalAuthor = author;
            String finalTitle = title;
            String finalUrl = url;
            String finalImg = img;
            channel.createEmbed(spec -> {
                spec.setAuthor(finalAuthor, null, null)
                        .setTitle(finalTitle)
                        .setUrl(finalUrl)
                        .setImage(finalImg)
                        .setColor(Color.ORANGE);
            }).subscribe();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLabel() {
        return DiscordBot.getInstance().getConfig().getCommands().getMeme();
    }

    @Override
    public String getDesc() {
        return DiscordBot.getInstance().getConfig().getCommands().getMemeDesc();
    }
}
