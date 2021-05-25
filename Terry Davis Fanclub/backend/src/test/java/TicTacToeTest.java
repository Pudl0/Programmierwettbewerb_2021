import de.pauhull.discordbot.bot.tictactoe.Game;
import de.pauhull.discordbot.bot.tictactoe.Player;
import de.pauhull.discordbot.util.ImageUtil;

import java.awt.image.BufferedImage;

public class TicTacToeTest {

    public static void main(String[] args) {

        Game game = new Game(0);
        game.getBoard()[0] = Player.HUMAN;
        game.getBoard()[5] = Player.HUMAN;
        game.getBoard()[2] = Player.AI;
        game.getBoard()[7] = Player.AI;
        BufferedImage image = game.render(300);

        System.out.println(ImageUtil.toBase64(image));
    }
}
