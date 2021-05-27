package de.pcvikings.discordbot.teams;

import de.pcvikings.discordbot.DiscordBot;
import de.pcvikings.discordbot.manager.DatabaseManager;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

public class Team {
    @Getter
    private String teamName;
    @Getter
    private Long teamLeader;
    @Getter
    private List<Long> teamMember;
    @Getter
    private List<String> data;
    @Getter @Setter
    private String status;
    @Getter
    private long channelId;
    @Getter
    private long voiceId;
    @Getter @Setter
    private long categoryId;
    @Getter @Setter
    private long roleId;

    public Team(String teamName, Long teamLeader, List<String> teamMember, List<String> data, String status, long channelId, long voiceId, long categoryId, long roleId) {
        this.teamName = teamName;
        this.teamLeader = teamLeader;
        this.teamMember = new ArrayList<>();
        teamMember.forEach(string -> this.teamMember.add(Long.valueOf(string)));
        this.data = new ArrayList<>();
        this.status = status;
        this.channelId = channelId;
        this.voiceId = voiceId;
        this.categoryId = categoryId;
        this.roleId = roleId;
    }

    public void addTeamMember(long longId) {
        this.teamMember.add(longId);
        this.save();
    }

    public void removeTeamMember(long longId) {
        this.teamMember.remove(longId);
        DiscordBot.getInstance().getCurrentGuild().removeRoleFromMember(longId, DiscordBot.getInstance().getCurrentGuild().getRoleById(this.roleId)).queue();
        this.save();
    }

    public void create() {
        Random random = new Random();
        Role teamRole = DiscordBot.getInstance().getCurrentGuild().createRole().setName(teamName).
                setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))).complete();
        this.roleId = teamRole.getIdLong();
        DiscordBot.getInstance().getCurrentGuild().addRoleToMember(teamLeader, teamRole).queue();

        Category category = DiscordBot.getInstance().getCurrentGuild().createCategory(teamName).
                setPosition(DiscordBot.getInstance().getCurrentGuild().getCategories().size()+1).
                addRolePermissionOverride(roleId, EnumSet.of(Permission.VIEW_CHANNEL), null).
                addRolePermissionOverride(844213892196597791L, null, EnumSet.of(Permission.VIEW_CHANNEL)).
                complete();
        TextChannel channel = DiscordBot.getInstance().getCurrentGuild().createTextChannel(teamName + "-chat").setParent(category).syncPermissionOverrides().complete();
        VoiceChannel voiceChannel = DiscordBot.getInstance().getCurrentGuild().createVoiceChannel(teamName + "-voice").setParent(category).syncPermissionOverrides().complete();
        this.channelId = channel.getIdLong();
        this.voiceId = voiceChannel.getIdLong();
        this.categoryId = category.getIdLong();

        String query = "INSERT INTO teams (name,teamleader,teammember,data,status,channelid,voicechannelid,categoryId,roleid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object> values = Arrays.asList(teamName, teamLeader, teamMember.toString(), data.toString(), status, channelId, voiceId, categoryId, roleId);
        DiscordBot.getInstance().getDatabaseManager().doAsync(true, query, values, new DatabaseManager.Callback<HashMap>() {
            @Override
            public void onSuccess(HashMap done) throws SQLException {
                DiscordBot.getInstance().sendEmbedMessage("**Team " + teamName + " erstellt**",
                        "Das Team " + teamName + " wurde erfolgreich erstellt. \n" +
                        "Füge Teammitglieder über '!team add MITGLIED' hinzu. Das Mitglied muss mit ✅ bestätigen und wird dann hinzugefügt. \n" +
                        "Außerdem fehlen noch die Anmeldungsdaten. Mit !team data add [Name, Klasse, Email] können die Daten für jedes Mitglied hinterlegt werden. \n" +
                        "Sobald alle eure Daten angegeben sind, könnt ihr die Anmeldung mit !team submit einreichen.", new Color(0, 227, 73), channel.getIdLong());
            }

            @Override
            public void onFailure(Throwable cause) {

            }
        });
    }

    public void delete() {
        DiscordBot.getInstance().getCurrentGuild().getTextChannelById(this.channelId).delete().queue();
        DiscordBot.getInstance().getCurrentGuild().getVoiceChannelById(this.voiceId).delete().queue();
        DiscordBot.getInstance().getCurrentGuild().getCategoryById(this.categoryId).delete().queue();
        DiscordBot.getInstance().getCurrentGuild().getRoleById(this.roleId).delete().queue();

        DiscordBot.getInstance().getDatabaseManager().doAsync(true, "DELETE FROM teams WHERE teamleader = ?", Arrays.asList(teamLeader), new DatabaseManager.Callback<HashMap>() {
            @Override
            public void onSuccess(HashMap done) throws SQLException {

            }

            @Override
            public void onFailure(Throwable cause) {

            }
        });
    }

    public void save() {
        String teamString = teamMember.toString();
        String dataString = data.toString();
        List<Object> values = Arrays.asList(teamString, status, dataString, teamLeader);

        DiscordBot.getInstance().getDatabaseManager().doAsync(true, "UPDATE teams SET teammember = ?,status = ?,data = ? WHERE teamleader = ?", values, new DatabaseManager.Callback<HashMap>() {
            @Override
            public void onSuccess(HashMap done) throws SQLException {
                System.out.println("Successfully updated data");
            }

            @Override
            public void onFailure(Throwable cause) {
                System.out.println("Couldn't save data successfully");
            }
        });
    }

    public void sendDataList() {
        String teamData = "";

        for(int i = 0; i < data.size(); i++) {
            teamData += (i+1) + ". " + data.get(i) + "\n";
        }

        DiscordBot.getInstance().sendEmbedMessage("Daten des Teams " + teamName + ":", teamData, new Color(0,111,255), channelId);
    }
}
