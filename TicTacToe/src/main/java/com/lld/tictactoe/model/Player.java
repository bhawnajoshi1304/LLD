package com.lld.tictactoe.model;

import com.lld.tictactoe.strategy.PlayerStrategy;
import lombok.Getter;

@Getter
public class Player{
    private final Character symbol;
    private final PlayerStrategy playerStrategy;
    public Player(Character symbol, PlayerStrategy playerStrategy){
        this.symbol = symbol;
        this.playerStrategy = playerStrategy;
    }
}
