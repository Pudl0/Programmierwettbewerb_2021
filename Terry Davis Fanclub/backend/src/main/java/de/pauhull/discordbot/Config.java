package de.pauhull.discordbot;

import com.google.gson.Gson;
import lombok.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Getter
@Data
@NoArgsConstructor
public class Config {

    @Getter(AccessLevel.NONE)
    @Setter
    private transient File file;
    @Getter(AccessLevel.NONE)
    @Setter
    private transient Gson gson;

    private Discord discord = new Discord();
    private WebConsole webConsole = new WebConsole();
    private Channels channels = new Channels();
    private Messages messages = new Messages();
    private Commands commands = new Commands();
    private Twitch twitch = new Twitch();
    private Github github = new Github();
    private Paste paste = new Paste();

    public void save() {

        String fileContent = gson.toJson(this);
        try {
            Files.write(file.toPath(), fileContent.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Discord {
        private String token = "Discord Token hier einfügen";
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class WebConsole {
        private int port = 8000;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Channels {
        private String welcome = "willkommen";
        private String announcements = "ankündigungen";
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Messages {
        private String welcome = "Willkommen, %s! \uD83D\uDE04";
        private String pullRequest = "\uD83D\uDCC1 Neuer Pull Request in %s";
        private String streamStarted = "%s streamt jetzt \"%s\"! ⏰";
        private String helpMessage = "\uD83D\uDCD6 Liste aller Befehle:\\n";
        private String commandNotFound = "Befehl wurde nicht gefunden \uD83D\uDE41\\nNutze !hilfe, um dir alle Befehle anzeigen zu lassen \uD83E\uDD13";
        private String noPrivateMessage = "Dafür musst du auf einem Server sein \uD83D\uDE15";
        private String teamDoesntExist = "Dieses Team existiert nicht \uD83E\uDD14";
        private String teamAssigned = "Dir wurde das Team \"%s\" zugewiesen \uD83C\uDF89";
        private String teamRemoved = "Du wurdest aus dem Team \"%s\" entfernt.";
        private String notInTeam = "Du bist in keinem Team!";
        private String questionFound = "Ich habe folgendes gefunden \uD83E\uDD13:";
        private String noQuestionFound = "Dazu konnte ich nichts finden \uD83E\uDD14\\nVersuche doch einen anderen Suchbegriff";
        private String onlyPrivateChat = "Anmeldungen nur im Privatchat ⚠";
        private String invalidEmail = "Ungültige E-Mail \uD83D\uDCEC";
        private String alreadyRegistered = "E-Mail bereits angemeldet ❌";
        private String applicationSuccessful = "Anmeldung erfolgreich! \uD83E\uDD73";
        private String teamAlreadyExists = "Dieses Team existiert bereits ☹";
        private String invalidColor = "Ungültige Farbe \uD83C\uDFA8";
        private String teamCreated = "Team erfolgreich erstellt ✅";
        private String teamDeleted = "Dein Team wurde aufgelöst \uD83D\uDCA5";
        private String teamJoinRequest = "Du hast eine Anfrage gesendet, um \"%s\" beizutreten \uD83D\uDC68\u200D\uD83D\uDCBB\\n%s muss mit \uD83D\uDC4D reagieren, um dich anzunehmen.";
        private String noTeamJoin = "Konnte keine Beitrittsanfrage senden \uD83D\uDE41";
        private String requestAccepted = "%s Deine Beitrittsanfrage für \"%s\" wurde angenommen \uD83C\uDF89";
        private String requestDeclined = "%s Deine Beitrittsanfrage für \"%s\" wurde abgelehnt ☹";
        private String ticTacToeWelcome = "\\nWillkommen bei Tic Tac Toe! \uD83D\uDC4B\\n\\n" +
                "Es gibt 4 Schwierigkeitsstufen:\\n\uD83D\uDE03 - Einfach\\n\uD83E\uDD14 - Mittel\\n\uD83D\uDE20 - Schwer\\n\uD83E\uDD2C - Unmöglich\\n\\nSuche dir eine aus, indem du auf diese Nachricht reagierst.";
        private String ticTacToeStart = "Okay, es geht los! \uD83E\uDD13\\nDu darfst anfangen: Nutze %s <Zahl von 1 bis 9> um ein 'x' auf dem Feld zu platzieren \uD83D\uDE09";
        private String invalidNumber = "Bitte gib eine Zahl von 1 bis 9 an \uD83E\uDD14";
        private String fieldNotEmpty = "Dieses Feld ist bereits benutzt \uD83E\uDD28";
        private String humanWon = "\\nHerzlichen Glückwunsch! Du hast gewonnen \uD83C\uDF89\\nWie wäre es mit noch einer Runde?";
        private String tie = "Unentschieden! Wie wäre es mit einer Revanche? \uD83D\uDE09";
        private String aiWon = "\\nDu hast verloren!\\nSei nicht traurig, vielleicht klappt es ja nächstes Mal \uD83D\uDE1C";
        private String nextMove = "Ich habe gespielt, du bist dran \uD83D\uDCDD";
        private String sendLink = "Dein Link: %s";
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Commands {
        private String prefix = "!";
        private String help = "hilfe";
        private String join = "beitreten";
        private String leave = "verlassen";
        private String joke = "witz";
        private String stackOverflow = "stackoverflow";
        private String application = "anmeldung";
        private String createTeam = "neuesteam";
        private String ticTacToe = "ttt";
        private String paste = "paste";
        private String helpDesc = "Alle Befehle auflisten \uD83D\uDCDC";
        private String joinDesc = "Team beitreten \uD83D\uDCBB";
        private String leaveDesc = "Momentanes Team verlassen \uD83D\uDEAA";
        private String jokeDesc = "Einen Witz hören \uD83D\uDE01";
        private String stackOverflowDesc = "Auf Stack Overflow suchen \uD83D\uDD0D";
        private String applicationDesc = "Eine Anmeldung einreichen \uD83D\uDCDD";
        private String createTeamDesc = "Neues Team erstellen \uD83D\uDCBE";
        private String ticTacToeDesc = "Tic Tac Toe spielen \uD83C\uDFAE";
        private String pasteDesc = "Paste erstellen \uD83D\uDCCB";
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Twitch {
        private String channel = "hackathonleer";
        private String clientId = "Client ID";
        private String clientSecret = "Client secret";
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Github {
        private String user = "Pudl0";
        private String repo = "Programmierwettbewerb_2021";
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Paste {
        private String url = "http://paste.pauhull.de/";
    }
}
