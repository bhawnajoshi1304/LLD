package com.lld.tictactoe.model;

import com.lld.tictactoe.strategy.PlayerStrategy;

public class Player{
    private final Character symbol;
    private final PlayerStrategy playerStrategy;
    public Player(Character symbol, PlayerStrategy playerStrategy){
        this.symbol = symbol;
        this.playerStrategy = playerStrategy;
    }
    public Character getSymbol() {
        return symbol;
    }
    public PlayerStrategy getPlayerStrategy() { return playerStrategy;}
}
