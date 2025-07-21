package TicTacToe.Player;

public class PlayerFactory {
    public static Player createPlayer(Character symbol, PlayerStrategy playerStrategy) {
        return new Player(symbol,playerStrategy);
    }
}
