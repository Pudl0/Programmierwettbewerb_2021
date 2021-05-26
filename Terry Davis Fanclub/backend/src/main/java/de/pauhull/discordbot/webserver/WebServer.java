package de.pauhull.discordbot.webserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.ErrorResponse;
import de.pauhull.discordbot.util.PasswordHasher;
import de.pauhull.discordbot.util.RandomStringGenerator;
import de.pauhull.discordbot.webserver.session.Session;
import de.pauhull.discordbot.webserver.session.SessionManager;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer implements HttpHandler {

    @Getter
    private static WebServer instance;
    @Getter
    @Setter
    private String passwordHash;
    @Getter
    private Api api;
    @Getter
    private Gson gson;
    @Getter
    private SessionManager sessionManager;
    private ExecutorService executorService;
    private List<String> paths;

    public WebServer(int port, Gson gson) {

        instance = this;
        this.executorService = Executors.newCachedThreadPool();
        this.gson = gson;
        this.api = new Api(this);
        this.sessionManager = new SessionManager();
        this.paths = Arrays.asList("/", "/announcements", "/home", "/login", "/logout", "/settings", "/teams", "/applications", "/customcommands");

        try {
            this.startServer(port);
            System.out.printf("Webserver started on port %d%n", port);
            this.loadPassword();

        } catch (IOException e) {
            System.err.printf("Couldn't start web server on port localhost:%d%n", port);
        }
    }

    public void savePassword() throws IOException {
        File passwordFile = new File(".pwd");
        Files.write(passwordFile.toPath(), Objects.requireNonNull(passwordHash).getBytes(StandardCharsets.UTF_8));
    }

    private void loadPassword() throws IOException {

        File passwordFile = new File(".pwd");
        if (!passwordFile.exists()) {
            String password = RandomStringGenerator.generate(16, RandomStringGenerator.ALPHANUMERIC);
            passwordHash = PasswordHasher.digest(password);
            savePassword();
            System.out.println();
            System.out.println("====================================");
            System.out.println();
            System.out.printf("Passwort für Konsole: %s%n", password);
            System.out.println("Bitte sofort nach Einloggen ändern!");
            System.out.println();
            System.out.println("====================================");
            System.out.println();
        }

        passwordHash = new String(Files.readAllBytes(passwordFile.toPath()));
    }

    private void startServer(int port) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this);
        server.setExecutor(null);
        server.start();
    }

    @Override
    public void handle(HttpExchange httpExchange) {

        executorService.execute(() -> {
            try {
                String[] path = httpExchange.getRequestURI().getPath().substring(1).split("/");

                if (path[0].equalsIgnoreCase("api")) {
                    serveApi(httpExchange, path);
                } else {
                    serveStatic(httpExchange);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void serveApi(HttpExchange httpExchange, String[] path) throws IOException {

        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        Session session = sessionManager.getSession(httpExchange);
        String[] newPath = new String[path.length - 1];
        System.arraycopy(path, 1, newPath, 0, path.length - 1);
        String content;
        try {
            content = api.handleRequest(newPath, session, httpExchange);
        } catch (Exception e) {
            e.printStackTrace();
            content = gson.toJson(new ErrorResponse(e.getMessage()));
        }
        sessionManager.setSession(httpExchange, session);

        byte[] responseBytes = (content == null ? "" : content).getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream out = httpExchange.getResponseBody();
        out.write(responseBytes);
        out.flush();
        out.close();
    }

    private void serveStatic(HttpExchange httpExchange) throws IOException {

        String requestedFile = httpExchange.getRequestURI().getPath();
        if (this.paths.contains(requestedFile.toLowerCase())) {
            requestedFile = "/index.html";
        }

        File file = new File("static" + requestedFile);
        if (!file.exists()) {
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.getResponseBody().close();
            return;
        }

        byte[] bytes = Files.readAllBytes(file.toPath());
        httpExchange.sendResponseHeaders(200, bytes.length);
        OutputStream out = httpExchange.getResponseBody();
        out.write(bytes);
        out.flush();
        out.close();
    }
}
