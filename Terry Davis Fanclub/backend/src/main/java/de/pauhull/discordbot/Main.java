package de.pauhull.discordbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.webserver.WebServer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main {

    public Main() throws IOException {

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Config config = loadConfig(gson);
        int port = config.getWebConsole().getPort();

        new DiscordBot(config, gson, () -> {
            executorService.execute(() -> {
                new WebServer(port, gson);
            });
        });
    }

    private Config loadConfig(Gson gson) throws IOException, NullPointerException {

        File configFile = new File("config.json");
        if (!configFile.exists()) {
            String defaultConfig = gson.toJson(new Config());
            Files.write(configFile.toPath(), defaultConfig.getBytes(StandardCharsets.UTF_8));
        }

        Config config = gson.fromJson(Files.lines(configFile.toPath(), StandardCharsets.UTF_8).collect(Collectors.joining("\n")), Config.class);
        config.setFile(configFile);
        config.setGson(gson);
        return config;
    }

    public static void main(String[] args) {
        try {
            new Main();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
