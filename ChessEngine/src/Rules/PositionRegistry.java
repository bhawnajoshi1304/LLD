package Rules;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PositionRegistry {
        private static final Map<String, Position> positions = new HashMap<>();

        static {
            for (int row = 1; row <= 8; row++) {
                for (char col = 'A'; col <= 'H'; col++) {
                    Position pos = new Position(col, row);
                    positions.put(key(col, row), pos);
                }
            }
        }

        private static String key(char col, int row) {
            return col + "" + row;
        }

        public static Position get(char col, int row) {
            return positions.get(key(col, row));
        }

        public static Collection<Position> allPositions() {
            return positions.values();
        }
    }
