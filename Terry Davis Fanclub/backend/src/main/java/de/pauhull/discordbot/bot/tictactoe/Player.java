package de.pauhull.discordbot.bot.tictactoe;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Player {

    NONE(0), AI(1), HUMAN(-1);

    private int score;
}
