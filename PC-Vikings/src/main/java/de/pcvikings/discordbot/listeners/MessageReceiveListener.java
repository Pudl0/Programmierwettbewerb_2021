package de.pcvikings.discordbot.listeners;

import de.pcvikings.discordbot.DiscordBot;
import de.pcvikings.discordbot.commands.TeamAdminCommand;
import de.pcvikings.discordbot.commands.TeamCommand;
import de.pcvikings.discordbot.configs.ChannelConfig;
import de.pcvikings.discordbot.configs.FilterConfig;
import de.pcvikings.discordbot.configs.RoleConfig;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class MessageReceiveListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String command = message.split(" ")[0].replace("!", "");

        if(message.length() == 0) {
            return;
        }

        for(String blacklistedWord : DiscordBot.getInstance().getFilterConfig().getBlacklistedwords()) {
            if(message.toLowerCase().contains(blacklistedWord.toLowerCase())) {
                event.getMessage().delete().queue();

                DiscordBot.getInstance().sendEmbedMessage("Chatverstoß", event.getMember().getAsMention() + " hat gegen den Chatfilter verstoßen: \n" +
                        message + "\n Verstoß: " + blacklistedWord, new Color(232, 35, 32), ChannelConfig.CHATFILTERCHANNEL);
                return;
            }
        }

        if(message.charAt(0) != '!') {
            return;
        }

        if(event.getMember().getRoles().contains(DiscordBot.getInstance().getCurrentGuild().getRoleById(RoleConfig.ADMINROLE))) {
            if(command.equalsIgnoreCase("teamadmin")) {
                new TeamAdminCommand(event.getMessage(), event.getAuthor());
                deleteMessage(event.getMessage());
                return;
            } else if(command.equalsIgnoreCase("reload")) {
                DiscordBot.getInstance().reloadConfigs();
                deleteMessage(event.getMessage());
                return;
            } else if(command.equalsIgnoreCase("setup")) {
                DiscordBot.getInstance().setupServer();
                deleteMessage(event.getMessage());
                return;
            }
        }

        if(command.equalsIgnoreCase("status")) {
            event.getChannel().sendMessage("Der Bot ist momentan **online**!").queue();
            return;
        } else if(command.equalsIgnoreCase("team")) {
            new TeamCommand(event.getMessage(), event.getAuthor());
            return;
        } else if(command.equalsIgnoreCase("hilfe")) {
            String helpMessage = "!team create (Name) - Erstelle das angegebene Team \n" +
                    "!team add @USER - Füge das Mitglied zu deinem Team hinzu \n" +
                    "!team info - Rufe die Info für dein Team aus \n" +
                    "!team data (add [Daten] | remove [index]) - Ruft die hinterlegten Daten ab, fügt einen Eintrag hinzu oder löscht den angegebenen Eintrag \n" +
                    "!team delete - Löscht dein Team \n" +
                    "!team leave - Verlasse das aktuelle Team" +
                    "!team kick @MITGLIED - Kicke als Teamleiter ein Teammitglied \n" +
                    "Weitere Befehle: \n";

            //ADDING THE "EASYCOMMANDS" TO THE HELPMESSAGE
            String easyCommands = "";
            for(String easyCommand : DiscordBot.getInstance().getEasyCommandConfig().getEasyCommandMap().keySet()) {
                easyCommands += "!" + easyCommand + ", ";
            }
            easyCommands = easyCommands.substring(0, easyCommands.length()-2); //REMOVE ", " FOR LAST "EASYCOMMAND"

            DiscordBot.getInstance().sendEmbedMessage("**Übersicht der Befehle:**", helpMessage + easyCommands, new Color(0,111,255), event.getChannel().getIdLong());
            deleteMessage(event.getMessage());
            return;
        } else if(DiscordBot.getInstance().getEasyCommandConfig().getEasyCommandMap().containsKey(command.toLowerCase())) {
            deleteMessage(event.getMessage());
            DiscordBot.getInstance().sendEmbedMessage(command, DiscordBot.getInstance().getEasyCommandConfig().getEasyCommandMap().get(command.toLowerCase()), new Color(0,111,255), event.getChannel().getIdLong());
            return;
        }

        deleteMessage(event.getMessage());
        DiscordBot.getInstance().sendEmbedMessage("Befehl nicht gefunden", "Der Befehl ``" + message.split(" ")[0] + "`` existiert nicht! Gebe '!hilfe' ein, um eine Liste der Befehle zu erhalten", new Color(235, 36, 26), event.getChannel().getIdLong());
    }

    private void deleteMessage(Message message) {
        if(message != null) {
            message.delete().queue();
        }
    }
}
