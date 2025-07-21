package TicTacToe.Game;

import TicTacToe.Player.Player;

public class InProgressState extends GameState {
    public InProgressState(Player player){
        super(player);
    }
    @Override
    public boolean isGameOver() {
        return false;
    }
}

