package com.lld.tictactoe.factory;

import com.lld.tictactoe.model.Player;
import com.lld.tictactoe.strategy.PlayerStrategy;

public class PlayerFactory {
    public static Player createPlayer(Character symbol, PlayerStrategy playerStrategy) {
        return new Player(symbol,playerStrategy);
    }
}
