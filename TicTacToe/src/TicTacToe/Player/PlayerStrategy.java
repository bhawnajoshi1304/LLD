package TicTacToe.Player;

import TicTacToe.Game.Board;
import TicTacToe.Game.Position;

public interface PlayerStrategy{
    Position makeMove(Board board);
}