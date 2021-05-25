package de.pauhull.discordbot.webserver.session;

import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.util.RandomStringGenerator;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private List<Session> sessions;

    public SessionManager() {

        this.sessions = new ArrayList<>();
    }

    public void setSession(HttpExchange httpExchange, Session session) {

        List<String> cookies = new ArrayList<>();
        cookies.add(String.format("sessionId=%s; Max-Age=2592000; Path=/", session.getId()));
        httpExchange.getResponseHeaders().put("Set-Cookie", cookies);
    }

    public Session getSession(HttpExchange httpExchange) {

        List<String> cookies = httpExchange.getRequestHeaders().get("Cookie");
        if (cookies == null) return createNewSession();

        for (String cookieHeader : cookies) {
            for (String cookie : cookieHeader.split(";")) {
                cookie = cookie.trim();
                String[] split = cookie.split("=");
                String key = split[0];
                String value = split[1];
                if (key.equalsIgnoreCase("sessionId")) {
                    for (Session session : sessions) {
                        if (session.getId().equalsIgnoreCase(value)) {
                            return session;
                        }
                    }
                }
            }
        }

        return createNewSession();
    }

    public Session createNewSession() {

        Session session;
        for (; ; ) {
            String id = RandomStringGenerator.generate(32, RandomStringGenerator.ALPHANUMERIC);
            boolean uniqueIdFound = true;
            for (Session s : sessions) {
                if (s.getId().equals(id)) {
                    uniqueIdFound = false;
                    break;
                }
            }
            if (uniqueIdFound) {
                session = new Session(id, false);
                break;
            }
        }

        sessions.add(session);
        return session;
    }
}
