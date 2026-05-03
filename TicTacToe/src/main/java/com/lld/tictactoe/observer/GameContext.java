package com.lld.tictactoe.observer;

import com.lld.tictactoe.model.GameState;
import com.lld.tictactoe.model.Player;
import com.lld.tictactoe.model.InProgressState;
import lombok.Getter;
import lombok.Setter;

public class GameContext {
    @Getter
    @Setter
    private GameState currentState;

    public GameContext(Player player) {
        currentState = new InProgressState(player);
    }

    public boolean isGameOver() {
        return currentState.isGameOver();
    }
}
