package de.pcvikings.discordbot.configs;

import java.util.HashMap;

public class RoleConfig {

    public static final long ADMINROLE = 844215346260344922L;

    public static final long ROLEREACTMESSAGE = 847497562919534592L;
    public static final HashMap<String, Long> REACTIONROLES = new HashMap<>();
    public static final String ROLEREACTTEXT = "";

    public static void initialiseRoles() {
        REACTIONROLES.put("🦠", 846750823211335760L);
        REACTIONROLES.put("⚙️", 846750863590162472L);
    }

}
