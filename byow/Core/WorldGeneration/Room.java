package byow.Core.WorldGeneration;

import byow.Core.Position;

public class Room {
    private Position bottomLeft;
    private int height;
    private int width;
    private int id;

    public Room(Position bottomLeft, int height, int width) {
        this.bottomLeft = bottomLeft;
        this.height = height;
        this.width = width;
    }

    public Position getPosition() {
        return bottomLeft;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
