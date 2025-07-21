package TicTacToe.Game;

import TicTacToe.Player.Player;

public class WonState extends GameState {
    public WonState(Player player){
        super(player);
    }
    @Override
    public boolean isGameOver() { return true; }
}