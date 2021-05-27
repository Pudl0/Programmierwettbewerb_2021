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

public class FilterConfig implements Config {

    @Getter
    private List<String> blacklistedwords = new ArrayList<>();

    @Override
    public void load() {
        DiscordBot.getInstance().getDatabaseManager().doAsync(false, "SELECT * FROM chatfilter", Arrays.asList(), new DatabaseManager.Callback<HashMap>() {
            @Override
            public void onSuccess(HashMap done) throws SQLException {
                ResultSet set = (ResultSet) done.get("ResultSet");

                while(set.next()) {
                    blacklistedwords.add(set.getString("word"));
                }
            }

            @Override
            public void onFailure(Throwable cause) {

            }
        });
    }

    @Override
    public void reload() {
        this.blacklistedwords.clear();
        this.load();
    }
}
