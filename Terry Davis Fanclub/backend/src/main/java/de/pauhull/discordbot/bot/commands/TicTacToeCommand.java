package de.pauhull.discordbot.bot.commands;

import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.bot.tictactoe.Game;
import de.pauhull.discordbot.bot.tictactoe.Player;
import de.pauhull.discordbot.bot.tictactoe.TicTacToe;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.reaction.ReactionEmoji;

import java.util.concurrent.TimeUnit;

public class TicTacToeCommand implements Command {

    @Override
    public void execute(MessageChannel channel, Member member, Message message, String label, String[] args) {

        DiscordBot bot = DiscordBot.getInstance();
        TicTacToe ticTacToe = bot.getTicTacToe();
        String prefix = bot.getConfig().getCommands().getPrefix();
        User player = member;
        String mention = "";

        if (channel instanceof PrivateChannel) {
            player = ((PrivateChannel) channel).getRecipients()
                    .filter(user -> !bot.getClient().getSelf().block().equals(user))
                    .blockFirst();
        } else {
            mention = member.getNicknameMention() + " ";
        }
        if (player == null) {
            return;
        }

        Game game = ticTacToe.getGames().get(player);
        if (game != null && game.getAge() > TimeUnit.MINUTES.toMillis(10)) {
            ticTacToe.getGames().remove(player);
            game = null;
        }

        if (game == null) {
            Message start = bot.sendMessageNeutral(channel, mention + bot.getConfig().getMessages().getTicTacToeWelcome());
            User finalPlayer = player;
            ticTacToe.getRequests().removeIf(request -> request.getUser().equals(finalPlayer));
            ticTacToe.getRequests().add(new TicTacToe.GameRequest(player, start));
            start.addReaction(ReactionEmoji.unicode("\uD83D\uDE03")).block();
            start.addReaction(ReactionEmoji.unicode("\uD83E\uDD14")).block();
            start.addReaction(ReactionEmoji.unicode("\uD83D\uDE20")).block();
            start.addReaction(ReactionEmoji.unicode("\uD83E\uDD2C")).block();

        } else {

            if (args.length < 1) {
                bot.sendMessageError(channel, mention + bot.getConfig().getMessages().getInvalidNumber());
                return;
            }

            int number;
            try {
                number = Integer.parseInt(args[0]) - 1;
                if (number < 0 || number > 8) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                bot.sendMessageError(channel, mention + bot.getConfig().getMessages().getInvalidNumber());
                return;
            }

            if (game.getBoard()[number] != Player.NONE) {
                bot.sendMessageError(channel, mention + bot.getConfig().getMessages().getFieldNotEmpty());
                return;
            }

            game.getBoard()[number] = Player.HUMAN;
            if (checkWinner(channel, player, mention, game)) {
                return;
            }

            ticTacToe.getSolver().nextMove(game);

            if (!checkWinner(channel, player, mention, game)) {
                bot.sendMessageNeutral(channel, mention + bot.getConfig().getMessages().getNextMove());
                ticTacToe.sendGame(channel, game);
            }
        }
    }

    private boolean checkWinner(MessageChannel channel, User player, String mention, Game game) {

        DiscordBot bot = DiscordBot.getInstance();
        Player winner = game.getWinner();

        if (winner != null) {
            if (winner == Player.AI) {
                bot.sendMessageNeutral(channel, mention + bot.getConfig().getMessages().getAiWon());
            } else if (winner == Player.HUMAN) {
                bot.sendMessageNeutral(channel, mention + bot.getConfig().getMessages().getHumanWon());
            } else if (winner == Player.NONE) {
                bot.sendMessageNeutral(channel, mention + bot.getConfig().getMessages().getTie());
            }
            bot.getTicTacToe().sendGame(channel, game);
            bot.getTicTacToe().getGames().remove(player);
            return true;
        }

        return false;
    }

    @Override
    public String getLabel() {
        return DiscordBot.getInstance().getConfig().getCommands().getTicTacToe();
    }

    @Override
    public String getDesc() {
        return DiscordBot.getInstance().getConfig().getCommands().getTicTacToeDesc();
    }

}
