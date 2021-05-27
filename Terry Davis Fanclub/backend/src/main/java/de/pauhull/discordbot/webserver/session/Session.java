package de.pauhull.discordbot.webserver.session;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Session {

    @Setter(AccessLevel.NONE)
    private String id;
    private boolean authorized;
}
