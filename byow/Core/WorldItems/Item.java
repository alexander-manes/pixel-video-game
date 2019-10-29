package byow.Core.WorldItems;

import byow.Core.Position;
import byow.TileEngine.TETile;

import java.io.Serializable;

import static byow.TileEngine.Tileset.FLOOR;

public class Item implements Serializable {
    protected Position position;
    protected TETile costume;

    public Item(Position position, TETile costume, TETile[][] world) {
        this.position = position;
        this.costume = costume;
        world[position.getX()][position.getY()] = costume;
    }

    public Position getPosition() {
        return position;
    }

    public void createOverlay(TETile[][] actualWorld, TETile[][] renderedWorld) {
        for (int i = -1; i <= 1; i++) {
            int x = position.getX() + i;
            for (int j = -1; j <= 1; j++) {
                int y = position.getY() + j;
                if (inBounds(actualWorld, x, y)) {
                    renderedWorld[x][y] = actualWorld[x][y];
                }
            }
        }
    }

    protected boolean inBounds(TETile[][] world, int x, int y) {
        return x >= 0 && x < world.length && y >= 0 && y < world[0].length;
    }
}
