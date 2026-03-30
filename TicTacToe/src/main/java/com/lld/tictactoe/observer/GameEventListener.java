package com.lld.tictactoe.observer;

import com.lld.tictactoe.model.Position;

public interface GameEventListener {
    void onMoveMade(Position position, Character symbol);
    void onGameStateChanged(GameState state);
}
