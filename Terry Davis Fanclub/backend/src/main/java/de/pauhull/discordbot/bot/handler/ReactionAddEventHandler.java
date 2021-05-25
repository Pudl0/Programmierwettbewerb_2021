package de.pauhull.discordbot.bot.handler;

import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.bot.manager.TeamManager;
import de.pauhull.discordbot.bot.tictactoe.Game;
import de.pauhull.discordbot.bot.tictactoe.TicTacToe;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji.Unicode;

import java.util.function.Consumer;

public class ReactionAddEventHandler implements Consumer<ReactionAddEvent> {

    @Override
    public void accept(ReactionAddEvent event) {

        DiscordBot bot = DiscordBot.getInstance();
        Message message = event.getMessage().block();
        Member reacting = event.getMember().orElse(null);

        if (reacting == null || bot.getClient().getSelf().block().equals(reacting)) {
            return;
        }

        for (TeamManager.JoinRequest joinRequest : bot.getTeamManager().getJoinRequests()) {
            if (joinRequest.getMessage().equals(message)) {

                message.removeReaction(event.getEmoji(), reacting.getId()).block();

                if (!reacting.equals(joinRequest.getOwner())) {
                    return;
                }

                String reaction = event.getEmoji().asUnicodeEmoji().orElse(Unicode.unicode("")).getRaw();
                if (reaction.equals("\uD83D\uDC4D") || reaction.equals("❌")) {
                    message.delete().block();
                    if (reaction.equals("\uD83D\uDC4D")) {
                        bot.getTeamManager().assignTeam(joinRequest.getMember(), joinRequest.getTeam());
                        bot.sendMessageSuccess(message.getChannel().block(), String.format(bot.getConfig().getMessages().getRequestAccepted(),
                                joinRequest.getMember().getNicknameMention(), joinRequest.getTeam().getName()));
                    } else {
                        bot.sendMessageError(message.getChannel().block(), String.format(bot.getConfig().getMessages().getRequestDeclined(),
                                joinRequest.getMember().getNicknameMention(), joinRequest.getTeam().getName()));
                    }
                    bot.getTeamManager().getJoinRequests().removeIf(request -> request.getMessage().equals(message));
                }

                return;
            }
        }

        for (TicTacToe.GameRequest request : bot.getTicTacToe().getRequests()) {
            if (request.getMessage().equals(message)) {

                message.removeReaction(event.getEmoji(), reacting.getId()).block();

                if (!reacting.equals(request.getUser())) {
                    return;
                }

                String reaction = event.getEmoji().asUnicodeEmoji().orElse(Unicode.unicode("")).getRaw();

                int difficulty;
                switch (reaction) {
                    case "\uD83D\uDE03":
                        difficulty = 0;
                        break;
                    case "\uD83E\uDD14":
                        difficulty = 1;
                        break;
                    case "\uD83D\uDE20":
                        difficulty = 2;
                        break;
                    case "\uD83E\uDD2C":
                        difficulty = 3;
                        break;
                    default:
                        return;
                }

                String mention = reacting.getNicknameMention() + " ";
                MessageChannel channel = message.getChannel().block();
                message.delete().block();
                bot.sendMessageNeutral(channel, mention + String.format(bot.getConfig().getMessages().getTicTacToeStart(),
                        bot.getConfig().getCommands().getPrefix() + bot.getConfig().getCommands().getTicTacToe()));
                Game game = new Game(difficulty);
                bot.getTicTacToe().getGames().put(request.getUser(), game);
                bot.getTicTacToe().sendGame(channel, game);
                bot.getTicTacToe().getRequests().removeIf(remove -> remove == request);
                return;
            }
        }
    }
}
