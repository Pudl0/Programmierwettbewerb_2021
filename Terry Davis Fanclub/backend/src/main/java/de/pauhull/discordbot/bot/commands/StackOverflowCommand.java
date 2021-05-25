package de.pauhull.discordbot.bot.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.pauhull.discordbot.bot.DiscordBot;
import discord4j.core.object.Embed;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import io.github.furstenheim.CodeBlockStyle;
import io.github.furstenheim.CopyDown;
import io.github.furstenheim.OptionsBuilder;
import org.apache.commons.text.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class StackOverflowCommand implements Command {

    private CopyDown copyDown;

    public StackOverflowCommand() {

        this.copyDown = new CopyDown(OptionsBuilder.anOptions().withCodeBlockStyle(CodeBlockStyle.FENCED).build());
    }

    @Override
    public void execute(MessageChannel channel, Member member, Message message, String label, String[] args) {

        DiscordBot bot = DiscordBot.getInstance();
        String prefix = bot.getConfig().getCommands().getPrefix();

        if (args.length < 1) {
            bot.sendMessageError(channel, "Verwendung: " + prefix + getLabel() + " <Suche>");
            return;
        }

        String search = String.join(" ", args);

        try {
            String searchQuery = URLEncoder.encode(search, StandardCharsets.UTF_8.toString());

            String apiUrl = "https://api.stackexchange.com/2.2/search?pagesize=1&order=desc&sort=relevance&intitle=%s&site=stackoverflow&filter=withbody";
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(apiUrl, searchQuery)).openConnection();
            String response = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream())))
                    .lines().collect(Collectors.joining("\n"));
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            JsonArray items = json.getAsJsonArray("items");

            if (items.size() == 0) {
                bot.sendMessageNeutral(channel, bot.getConfig().getMessages().getNoQuestionFound());
            } else {
                bot.sendMessageSuccess(channel, bot.getConfig().getMessages().getQuestionFound());
                JsonObject item = items.get(0).getAsJsonObject();
                String title = StringEscapeUtils.unescapeHtml3(item.get("title").getAsString());
                String url = item.get("link").getAsString();
                JsonObject owner = item.get("owner").getAsJsonObject();
                String author = owner.get("display_name").getAsString();

                String authorImg;
                if (owner.has("profile_image")) {
                    authorImg = owner.get("profile_image").getAsString();
                } else {
                    authorImg = null;
                }

                String body = copyDown.convert(StringEscapeUtils.unescapeHtml3(item.get("body").getAsString()));
                StringBuilder descriptionBuilder = new StringBuilder(body);
                if (descriptionBuilder.length() > Embed.MAX_DESCRIPTION_LENGTH - 3) {
                    descriptionBuilder = new StringBuilder(descriptionBuilder.substring(0, Embed.MAX_DESCRIPTION_LENGTH - 3))
                            .append("...");
                }
                String description = descriptionBuilder.toString();

                channel.createEmbed(spec -> {
                    spec.setColor(Color.ORANGE)
                            .setAuthor(author, null, authorImg)
                            .setUrl(url)
                            .setTitle(title)
                            .setThumbnail("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ef/Stack_Overflow_icon.svg/768px-Stack_Overflow_icon.svg.png")
                            .setDescription(description);
                }).block();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLabel() {
        return DiscordBot.getInstance().getConfig().getCommands().getStackOverflow();
    }

    @Override
    public String getDesc() {
        return DiscordBot.getInstance().getConfig().getCommands().getStackOverflowDesc();
    }
}
