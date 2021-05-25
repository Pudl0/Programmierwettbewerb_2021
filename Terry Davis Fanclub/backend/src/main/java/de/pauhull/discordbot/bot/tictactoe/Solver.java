package de.pauhull.discordbot.bot.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Solver {

    private Random random;

    public Solver() {
        this.random = new Random();
    }

    public void nextMove(Game game) {

        if (game.getWinner() != null) throw new IllegalArgumentException();

        if (Math.random() > game.getPropability()) {
            randomMove(game);
        } else {
            winningMove(game);
        }
    }

    private void randomMove(Game game) {

        List<Integer> possibleMoves = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (game.getBoard()[i] == Player.NONE) {
                possibleMoves.add(i);
            }
        }
        int move = possibleMoves.get(random.nextInt(possibleMoves.size()));
        game.getBoard()[move] = Player.AI;
    }

    private void winningMove(Game game) {

        int bestScore = Integer.MIN_VALUE;
        int move = -1;

        for (int i = 0; i < 9; i++) {
            if (game.getBoard()[i] == Player.NONE) {
                game.getBoard()[i] = Player.AI;
                int score = minimax(game, false);
                game.getBoard()[i] = Player.NONE;
                if (score > bestScore) {
                    move = i;
                    bestScore = score;
                }
            }
        }

        game.getBoard()[move] = Player.AI;
    }

    private int minimax(Game game, boolean maximizing) {

        Player winner = game.getWinner();
        if (winner != null) {
            return winner.getScore();
        }

        int bestScore = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int i = 0; i < 9; i++) {
            if (game.getBoard()[i] == Player.NONE) {
                game.getBoard()[i] = maximizing ? Player.AI : Player.HUMAN;
                int score = minimax(game, !maximizing);
                game.getBoard()[i] = Player.NONE;
                bestScore = maximizing ? Math.max(score, bestScore) : Math.min(score, bestScore);
            }
        }

        return bestScore;
    }
}
