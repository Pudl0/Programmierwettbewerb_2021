package de.pcvikings.discordbot.teams;

import de.pcvikings.discordbot.DiscordBot;
import de.pcvikings.discordbot.configs.ChannelConfig;
import de.pcvikings.discordbot.manager.DatabaseManager;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TeamManager {

    @Getter
    private final HashMap<String, Team> teams = new HashMap<>();

    public TeamManager() {
        loadTeams();
    }

    public void loadTeams() {
        DiscordBot.getInstance().getDatabaseManager().doAsync(false, "SELECT * FROM teams", Arrays.asList(), new DatabaseManager.Callback<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) throws SQLException {
                ResultSet set = (ResultSet) hashMap.get("ResultSet");

                while(set.next()) {
                    List<String> teamMember = Arrays.asList(set.getString("teammember").replace("[", "").replace("]", "").replace(" ", "").split(","));
                    List<String> data = Arrays.asList(set.getString("data").replace("[", "").replace("]", "").split(","));

                    Team team = new Team(set.getString("name"), set.getLong("teamleader"), teamMember, data, set.getString("status"), set.getLong("channelId"), set.getLong("voiceChannelId"), set.getLong("categoryId"), set.getLong("roleId"));
                    teams.put(set.getString("name").toLowerCase(), team);
                }
            }

            @Override
            public void onFailure(Throwable cause) {
                DiscordBot.getInstance().getCurrentGuild().getTextChannelById(ChannelConfig.ERRORLOG).sendMessage("Während des Ladens der Teams trat ein kritischer Fehler auf!").queue();
            }
        });
    }

    public boolean isTeamExisting(String name) {
        return teams.containsKey(name);
    }

    public Team getTeam(String name) {
        return teams.get(name.toLowerCase());
    }

    public Team getTeam(long userId) {
        for(Team team : teams.values()) {
            if(team.getTeamMember().contains(userId)) {
                return team;
            }
        }

        return null;
    }

    public void registerTeam(String name, long teamLeader) {
        if(isTeamExisting(name)) {
            return;
        }

        Team team = new Team(name, teamLeader, Arrays.asList(teamLeader + ""), Arrays.asList(""), "Anmeldung ausstehend", 0, 0, 0, 0);
        team.create();
        teams.put(name.toLowerCase(), team);
    }

    public void removeTeam(String name) {
        Team team = getTeam(name);
        team.delete();
        teams.remove(name.toLowerCase());
    }

    public List<Team> getTeamWithStatus(String status) {
        List<Team> teamsWithStatus = new ArrayList<>();

        this.teams.forEach((s, team) -> {
            if(team.getStatus().equalsIgnoreCase(status)) {
                teamsWithStatus.add(team);
            }
        });

        return teamsWithStatus;
    }
}
