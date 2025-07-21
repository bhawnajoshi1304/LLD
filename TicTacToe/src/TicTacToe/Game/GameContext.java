package TicTacToe.Game;

import TicTacToe.Player.Player;

public class GameContext {
    private GameState currentState;

    public GameContext(Player player) {
        currentState = new InProgressState(player);
    }

    public void setCurrentState(GameState state) {
        this.currentState = state;
    }

    public boolean isGameOver() {
        return currentState.isGameOver();
    }

    public GameState getCurrentState() {
        return currentState;
    }
}
