package de.pcvikings.discordbot.configs;

import de.pcvikings.discordbot.DiscordBot;
import de.pcvikings.discordbot.manager.DatabaseManager;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ChannelConfig implements Config {

    public static final long GUILDID = 844213892196597791L;

    public static final long WELCOMECHANNEL = 847496547934928936L;
    public static final long STATUSLOG = 847496550116098068L;
    public static final long ERRORLOG = 847496549402935336L;
    public static final long TEAMCREATIONCHANNEL = 847496547238281237L;
    public static final long CHATFILTERCHANNEL = 847496546383429652L;
    public static final long TEAMREGISTRATIONCHANNEL = 847496548681646140L;

    @Getter
    private final HashMap<String, Long> channelMap = new HashMap<String, Long>();

    @Getter
    private final List<String> alreadyExistingChannel = new ArrayList<>();

    public ChannelConfig() {
        initialiseChannelList();
        this.load();
    }

    private void initialiseChannelList() {
        channelMap.put("willkommen", (long) -1);
        channelMap.put("bot-status", (long) -1);
        channelMap.put("bot-fehler", (long) -1);
        channelMap.put("teamverwaltung", (long) -1);
        channelMap.put("team-anmeldungen", (long) -1);
        channelMap.put("chatfilter", (long) -1);
    }

    @Override
    public void load() {
        DiscordBot.getInstance().getDatabaseManager().doAsync(false, "SELECT * FROM channel", Arrays.asList(), new DatabaseManager.Callback<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) throws SQLException {
                ResultSet set = (ResultSet) hashMap.get("ResultSet");

                while (set.next()) {
                    if (channelMap.containsKey(set.getString("name"))) {
                        channelMap.remove(set.getString("name"));
                    }

                    channelMap.put(set.getString("name"), set.getLong("channelId"));
                    alreadyExistingChannel.add(set.getString("name"));
                }
            }

            @Override
            public void onFailure(Throwable cause) {

            }
        });


    }

    @Override
    public void reload() {
        this.channelMap.clear();
        initialiseChannelList();
        this.load();
    }

    public void save() {
        this.channelMap.forEach((name, id) -> {
            if (!this.alreadyExistingChannel.contains(name)) {
                String query = "INSERT INTO channel (name, channelId) VALUES (?, ?)";
                DiscordBot.getInstance().getDatabaseManager().doAsync(true, query, Arrays.asList(name, id), new DatabaseManager.Callback<HashMap>() {
                    @Override
                    public void onSuccess(HashMap done) throws SQLException {

                    }

                    @Override
                    public void onFailure(Throwable cause) {

                    }
                });
            }
        });
    }
}
