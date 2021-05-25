package de.pauhull.discordbot.bot.manager;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.util.ColorUtil;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.rest.util.PermissionSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class TeamManager {

    private File teamsFile;
    private List<Team> teams;
    @Getter
    private List<JoinRequest> joinRequests;

    public TeamManager() {

        this.teamsFile = new File("teams.json");
        this.teams = new ArrayList<>();
        this.joinRequests = new ArrayList<>();

        try {
            this.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() throws IOException {

        String fileContent = "[]";
        if (teamsFile.exists()) {
            fileContent = Files.lines(teamsFile.toPath(), StandardCharsets.UTF_8).collect(Collectors.joining("\n"));
        }
        JsonElement element = JsonParser.parseString(fileContent);

        if (!element.isJsonArray()) {
            return;
        }

        Team[] teamArray = DiscordBot.getInstance().getGson().fromJson(element, Team[].class);
        teams.clear();
        teams.addAll(Arrays.asList(teamArray));
    }

    public void save() throws IOException {

        String fileContent = DiscordBot.getInstance().getGson().toJson(teams.toArray(new Team[0]));
        Files.write(teamsFile.toPath(), fileContent.getBytes(StandardCharsets.UTF_8));
    }

    public boolean removeTeam(Team team) {

        try {
            teams.remove(team);
            save();

            DiscordBot.getInstance().getClient().getGuilds()
                    .flatMap(Guild::getRoles)
                    .filter(role -> role.getName().equalsIgnoreCase(team.getName()))
                    .toIterable().forEach(role -> role.delete().block());

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void clearTeams(Member member) {

        DiscordBot bot = DiscordBot.getInstance();

        member.getRoles()
                .filter(role -> bot.getTeamManager().getTeam(role.getName()) != null)
                .toIterable().forEach(role -> member.removeRole(role.getId()).block());
    }

    public void assignTeam(Member member, Team team) {

        clearTeams(member);

        Objects.requireNonNull(member.getGuild().block()).getRoles()
                .filter(role -> role.getName().equalsIgnoreCase(team.getName()))
                .toIterable().forEach(role -> member.addRole(role.getId()).block());
    }

    public Team addTeam(String name, String color, Long owner) {

        if (getTeam(name) != null) {
            return null;
        }

        DiscordBot bot = DiscordBot.getInstance();

        for (Guild guild : bot.getClient().getGuilds().toIterable()) {
            for (Role role : guild.getRoles().toIterable()) {
                if (role.getName().equalsIgnoreCase(name)) {
                    return null;
                }
            }
        }

        try {
            Team team = new Team(name, color, owner);
            teams.add(team);
            save();

            bot.getClient().getGuilds().toIterable().forEach(guild -> guild.createRole(spec -> {
                spec.setName(name)
                        .setColor(ColorUtil.getDiscordColor(color))
                        .setHoist(true)
                        .setMentionable(false)
                        .setPermissions(PermissionSet.none());
            }).block());
            return team;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Team getTeam(String name) {
        for (Team team : teams) {
            if (team.getName().equalsIgnoreCase(name)) {
                return team;
            }
        }
        return null;
    }

    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Team {
        private String name;
        private String color;
        private Long owner;
    }

    @Getter
    @AllArgsConstructor
    public static class JoinRequest {
        private Message message;
        private Member member;
        private Member owner;
        private Team team;
    }
}
