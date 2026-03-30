package com.lld.tictactoe.observer;

import com.lld.tictactoe.model.GameState;
import com.lld.tictactoe.model.Player;
import com.lld.tictactoe.model.InProgressState;

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
