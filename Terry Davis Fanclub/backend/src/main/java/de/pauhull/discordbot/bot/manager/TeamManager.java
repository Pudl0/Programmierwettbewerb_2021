package de.pauhull.discordbot.bot.manager;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.util.ColorUtil;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.*;
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

    public void save() {

        try {
            String fileContent = DiscordBot.getInstance().getGson().toJson(teams);
            Files.write(teamsFile.toPath(), fileContent.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Team> getOwnedTeams(Member member) {

        return getTeams(member).stream()
                .filter(team -> team.getOwner().equals(member.getId().asString()))
                .collect(Collectors.toList());
    }

    public List<Team> getTeams(Member member) {

        return member.getRoles()
                .map(role -> Optional.ofNullable(getTeam(role.getName())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collectList().block();
    }

    public boolean removeTeam(Team team) {

        teams.remove(team);
        save();

        DiscordBot.getInstance().getClient().getGuilds()
                .flatMap(Guild::getRoles)
                .filter(role -> role.getName().equalsIgnoreCase(team.getName()))
                .toIterable().forEach(role -> role.delete().block());

        return true;
    }

    public void unassignTeam(Member member, Team team) {

        member.getRoles()
                .filter(role -> role.getName().equalsIgnoreCase(team.getName()))
                .toIterable()
                .forEach(role -> member.removeRole(role.getId()));

        team.getMembers().remove(member.getId().asString());
        save();
    }

    public void assignTeam(String member, Team team) {

        DiscordBot.getInstance().getClient().getGuilds()
                .flatMap(Guild::getRoles)
                .filter(role -> role.getName().equalsIgnoreCase(team.getName()))
                .toIterable().forEach(role -> {
            role.getGuild().block()
                    .getMemberById(Snowflake.of(member)).block()
                    .addRole(role.getId()).block();
        });

        team.getMembers().add(member);
        save();
    }

    public Team addTeam(String name, String color, String owner) {

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

        Team team = new Team(name, color, owner, new HashSet<>());
        if (owner != null) {
            team.getMembers().add(owner);
        }
        teams.add(team);
        save();

        bot.getClient().getGuilds().toIterable().forEach(guild -> guild.createRole(spec -> {
            spec.setName(name)
                    .setColor(ColorUtil.getDiscordColor(color))
                    .setHoist(true)
                    .setMentionable(false)
                    .setPermissions(PermissionSet.none());
        }).block());

        if (owner != null) {
            bot.getClient().getGuilds()
                    .flatMap(Guild::getRoles)
                    .filter(role -> role.getName().equalsIgnoreCase(team.getName()))
                    .toIterable().forEach(role -> {
                role.getGuild().block()
                        .getMemberById(Snowflake.of(owner)).block()
                        .addRole(role.getId()).block();
            });
        }

        return team;
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
        private String owner;
        private Set<String> members;

        public String getOwnerName() {
            if (owner == null) {
                return "Öffentlich";
            }
            return Objects.requireNonNull(
                    DiscordBot.getInstance().getClient().getUserById(Snowflake.of(owner)).block()
            ).getUsername();
        }

        public List<String> getMemberNames() {
            DiscordBot bot = DiscordBot.getInstance();
            return members.stream()
                    .map(id -> Optional.ofNullable(bot.getClient().getUserById(Snowflake.of(id)).block()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(User::getUsername)
                    .collect(Collectors.toList());
        }
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
