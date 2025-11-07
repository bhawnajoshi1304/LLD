package Rules;

import java.util.ArrayList;
import java.util.List;

public class MoveUtils {
    public static List<Position> rayMoves(Position start, int[][] directions){
        List<Position> moves = new ArrayList<>();
        for (int[] dir : directions) {
            int dx = dir[0], dy = dir[1];
                Position current = start.offset(dx, dy);
            while (current!=null) {
                moves.add(current);
                current = current.offset(dx, dy);
            }
        }
        return moves;
    }
    public static List<Position> positionMoves(Position start, int[][] directions){
        List<Position> moves = new ArrayList<>();
        for (int[] dir : directions) {
            int dx = dir[0], dy = dir[1];
            Position current = start.offset(dx, dy);
            if (current!=null) {
                moves.add(current);
            }
        }
        return moves;
    }
}
