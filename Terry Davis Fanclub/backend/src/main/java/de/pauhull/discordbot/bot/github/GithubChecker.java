package de.pauhull.discordbot.bot.github;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.pauhull.discordbot.Config;
import de.pauhull.discordbot.bot.DiscordBot;
import discord4j.rest.util.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GithubChecker implements Runnable {

    private ScheduledExecutorService scheduler;
    private List<Long> announcedIds;
    private SimpleDateFormat dateFormat;

    public GithubChecker() {

        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        // api request every 90 seconds (public rate limit: 60 requests/hour)
        this.scheduler.scheduleAtFixedRate(this, 0, 90, TimeUnit.SECONDS);
        this.announcedIds = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @Override
    public void run() {

        DiscordBot bot = DiscordBot.getInstance();

        try {
            Config config = bot.getConfig();
            String repoName = String.format("%s/%s", config.getGithub().getUser(), config.getGithub().getRepo());
            URL requestUrl = new URL(String.format("https://api.github.com/repos/%s/pulls", repoName));
            String content = new BufferedReader(new InputStreamReader(requestUrl.openStream(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
            JsonElement json = JsonParser.parseString(content);

            if (!json.isJsonArray()) {
                // invalid repo name or rate limit exceeded
                return;
            }

            JsonArray pullRequests = JsonParser.parseString(content).getAsJsonArray();
            for (int i = 0; i < pullRequests.size(); i++) {

                JsonObject pullRequest = pullRequests.get(i).getAsJsonObject();
                long id = pullRequest.get("id").getAsLong();

                if (announcedIds.contains(id)) {
                    continue;
                }

                String createdAt = pullRequest.get("created_at").getAsString();
                long age = System.currentTimeMillis() - (dateFormat.parse(createdAt).getTime());

                if (age > 0 && age < TimeUnit.MINUTES.toMillis(5)) {

                    JsonObject user = pullRequest.get("user").getAsJsonObject();
                    String author = user.get("login").getAsString();
                    String authorAvatarUrl = user.get("avatar_url").getAsString();
                    String authorProfileUrl = user.get("url").getAsString();

                    String title = String.format(bot.getConfig().getMessages().getPullRequest(), repoName);
                    String url = pullRequest.get("html_url").getAsString();
                    String pullTitle = pullRequest.get("title").getAsString();
                    String description = String.format("\"%s\" von %s", pullTitle, author);

                    bot.inTextChannels(bot.getConfig().getChannels().getAnnouncements(), channel -> {
                        channel.createEmbed(spec -> {
                            spec.setColor(Color.DISCORD_WHITE)
                                    .setAuthor(author, authorProfileUrl, authorAvatarUrl)
                                    .setTitle(title)
                                    .setUrl(url)
                                    .setDescription(description)
                                    .setThumbnail("https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png");
                        }).block();
                    });

                    bot.log(String.format("Announced pull request by %s", author));
                    announcedIds.add(id);
                }
            }

        } catch (IOException | ParseException e) {
            bot.log("GitHub service unavailable: %s", e);
        }
    }
}
