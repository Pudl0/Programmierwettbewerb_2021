package de.pcvikings.discordbot.configs;

import de.pcvikings.discordbot.DiscordBot;
import de.pcvikings.discordbot.manager.DatabaseManager;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

public class EasyCommandConfig implements Config {

    @Getter
    private HashMap<String, String> easyCommandMap = new HashMap<>();

    @Override
    public void load() {
        DiscordBot.getInstance().getDatabaseManager().doAsync(false, "SELECT * FROM easycommands", Arrays.asList(), new DatabaseManager.Callback<HashMap>() {
            @Override
            public void onSuccess(HashMap done) throws SQLException {
                ResultSet set = (ResultSet) done.get("ResultSet");

                while(set.next()) {
                    easyCommandMap.put(set.getString("command"), set.getString("output"));
                }
            }

            @Override
            public void onFailure(Throwable cause) {

            }
        });
    }

    @Override
    public void reload() {
        this.easyCommandMap.clear();
        this.load();
    }
}
