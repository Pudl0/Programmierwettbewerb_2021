package de.pauhull.discordbot.bot.manager;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.pauhull.discordbot.bot.DiscordBot;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationManager {

    private File applicationsFile;
    private List<Application> applications;

    public ApplicationManager() {

        this.applicationsFile = new File("applications.json");
        this.applications = new ArrayList<>();

        try {
            this.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean removeApplication(String email) {

        Iterator<Application> iterator = applications.iterator();

        while (iterator.hasNext()) {
            Application application = iterator.next();
            if (application.getEmail().equalsIgnoreCase(email)) {
                iterator.remove();
                try {
                    save();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        return false;
    }

    public boolean addApplication(String email, String name) {

        if (getApplication(email) != null) {
            return false;
        }

        applications.add(new Application(email, name, new Date()));

        try {
            save();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void load() throws IOException {

        String fileContent = "[]";
        if (applicationsFile.exists()) {
            fileContent = Files.lines(applicationsFile.toPath(), StandardCharsets.UTF_8).collect(Collectors.joining("\n"));
        }
        JsonElement element = JsonParser.parseString(fileContent);

        if (!element.isJsonArray()) {
            return;
        }

        Application[] applicationArray = DiscordBot.getInstance().getGson().fromJson(element, Application[].class);
        applications.clear();
        applications.addAll(Arrays.asList(applicationArray));
    }

    private void save() throws IOException {

        String fileContent = DiscordBot.getInstance().getGson().toJson(applications.toArray(new Application[0]));
        Files.write(applicationsFile.toPath(), fileContent.getBytes(StandardCharsets.UTF_8));
    }

    public Application getApplication(String email) {
        for (Application application : applications) {
            if (application.getEmail().equalsIgnoreCase(email)) {
                return application;
            }
        }
        return null;
    }

    public List<Application> getApplications() {
        return new ArrayList<>(applications);
    }

    @Getter
    @AllArgsConstructor
    public static class Application {
        private String email;
        private String name;
        private Date created;
    }
}
