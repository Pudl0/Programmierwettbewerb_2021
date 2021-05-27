package de.pauhull.discordbot.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class BotInfoResponse {

    private String username;
    private String discriminator;
    private String profileImg;
    private String clientId;
    private List<Guild> guilds;

    @AllArgsConstructor
    @Getter
    public static class Guild {
        private String id;
        private String name;
        private String img;
    }
}
