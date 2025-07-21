package TicTacToe.Game;

public interface GameEventListener {
    void onMoveMade(Position position, Character symbol);
    void onGameStateChanged(GameState state);
}
