package TicTacToe.Player;

public class Player{
    private final Character symbol;
    private final PlayerStrategy playerStrategy;
    Player(Character symbol, PlayerStrategy playerStrategy){
        this.symbol = symbol;
        this.playerStrategy = playerStrategy;
    }
    public Character getSymbol() {
        return symbol;
    }
    public PlayerStrategy getPlayerStrategy() { return playerStrategy;}
}