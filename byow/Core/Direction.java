package byow.Core;

/**
 * Data class to represent the direction for Hallway generation
 */
public enum Direction {
    LEFT,
    RIGHT,
    UP,
    DOWN;

    public static Direction valueOf(int d) {
        switch (d) {
            case 0:
                return DOWN;
            case 1:
                return RIGHT;
            case 2:
                return UP;
            case 3:
                return LEFT;
            default:
                return RIGHT;
        }
    }
}

