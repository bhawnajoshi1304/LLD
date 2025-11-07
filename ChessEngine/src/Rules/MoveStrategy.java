package Rules;

import Game.Board;

import java.util.List;

public interface MoveStrategy {

    List<Position> possibleMoves(Position fromPosition, Board board);
}
