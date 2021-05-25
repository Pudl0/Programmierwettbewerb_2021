package de.pauhull.discordbot.bot.tictactoe;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicTacToe {

    @Getter
    private Map<User, Game> games;
    @Getter
    private List<GameRequest> requests;
    @Getter
    private Solver solver;

    public TicTacToe() {
        this.games = new HashMap<>();
        this.requests = new ArrayList<>();
        this.solver = new Solver();
    }

    public void sendGame(MessageChannel channel, Game game) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(game.render(150), "png", out);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        channel.createMessage(spec -> {
            spec.addFile("tic-tac-toe.png", input);
        }).block();
    }

    @AllArgsConstructor
    @Getter
    public static class GameRequest {
        private User user;
        private Message message;
    }
}
