package de.pauhull.discordbot.bot.tictactoe;

import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;

public class Game {

    @Getter
    private Player[] board;
    @Getter
    private long createdAt;
    @Getter
    private float propability;

    public Game(Player[] board, int difficulty) {
        this.board = board;
        this.createdAt = System.currentTimeMillis();
        this.propability = (difficulty + 1) / 4.0f;
    }

    public Game(int difficulty) {
        this(Stream.generate(() -> Player.NONE).limit(9).toArray(Player[]::new), difficulty);
    }

    public long getAge() {
        return System.currentTimeMillis() - createdAt;
    }

    public Player getWinner() {

        // vertical
        for (int x = 0; x < 3; x++) {
            if (board[x] == board[x + 3] && board[x] == board[x + 6] && board[x] != Player.NONE) {
                return board[x];
            }
        }
        // horizontal
        for (int y = 0; y < 3; y++) {
            if (board[y * 3] == board[y * 3 + 1] && board[y * 3] == board[y * 3 + 2] && board[y * 3] != Player.NONE) {
                return board[y * 3];
            }
        }
        // diagonal (top left to bottom right)
        if (board[0] == board[4] && board[0] == board[8] && board[0] != Player.NONE) {
            return board[0];
        }
        // diagonal (top right to bottom left)
        if (board[2] == board[4] && board[2] == board[6] && board[2] != Player.NONE) {
            return board[2];
        }
        // is not tie
        for (int i = 0; i < 9; i++) {
            if (board[i] == Player.NONE) {
                return null;
            }
        }
        // tie
        return Player.NONE;
    }

    public BufferedImage render(int size) {
        float strokeWidth = size / 60f;
        int fieldSize = size / 3;
        int padding = size / 20;
        int fontSize = size / 15;
        int crossPadding = (int) (padding * 1.75f);

        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setBackground(Color.WHITE);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.clearRect(0, 0, size, size);
        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(strokeWidth));
        graphics.setFont(new Font("Helvetica", Font.PLAIN, fontSize));
        for (int x = 1; x < 3; x++) {
            graphics.drawLine(x * fieldSize, 0, x * fieldSize, size);
        }
        for (int y = 1; y < 3; y++) {
            graphics.drawLine(0, y * fieldSize, size, y * fieldSize);
        }
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int i = x + y * 3;
                if (board[i] == Player.HUMAN) {
                    graphics.drawLine(x * fieldSize + crossPadding, y * fieldSize + crossPadding, (x + 1) * fieldSize - crossPadding, (y + 1) * fieldSize - crossPadding);
                    graphics.drawLine((x + 1) * fieldSize - crossPadding, y * fieldSize + crossPadding, x * fieldSize + crossPadding, (y + 1) * fieldSize - crossPadding);
                } else if (board[i] == Player.AI) {
                    graphics.drawOval(x * fieldSize + padding, y * fieldSize + padding, fieldSize - padding * 2, fieldSize - padding * 2);
                }
                graphics.drawString(Integer.toString(i + 1), x * fieldSize + strokeWidth * 2, y * fieldSize + graphics.getFontMetrics().getHeight() - strokeWidth);
            }
        }
        graphics.dispose();
        return image;
    }
}
