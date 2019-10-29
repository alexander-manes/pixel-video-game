package byow.Core.WorldItems;

import byow.Core.Direction;
import byow.Core.Position;
import byow.TileEngine.TETile;

import java.io.Serializable;

import static byow.Core.Direction.UP;
import static byow.TileEngine.Tileset.FLOOR;
import static byow.TileEngine.Tileset.TREE;

public class Avatar extends Item {
    private Direction direction;
    private boolean hitLantern;
    private boolean hasAxe;
    private boolean wasUnderTree;
    private int numTrees;

    public Avatar(Position startingPosition, TETile costume, TETile[][] world) {
        super(startingPosition, costume, world);
        this.direction = UP;
        this.hitLantern = false;
        this.hasAxe = false;
        this.wasUnderTree = false;
        numTrees = 0;
    }

    /**
     * Moves avatar if it is possible ie not running into a wall
     *
     * @param world
     * @param newDirection
     */
    public boolean move(TETile[][] world, Direction newDirection) {
        int x = position.getX();
        int y = position.getY();
        this.direction = newDirection;
        switch (this.direction) {
            case UP:
                if (y < world[0].length - 1 && !world[x][y + 1].description().equals("wall")) {
                    return updateWorld(world, x, y + 1);
                }
                break;
            case DOWN:
                if (y > 0 && !world[x][y - 1].description().equals("wall")) {
                    return updateWorld(world, x, y - 1);
                }
                break;
            case RIGHT:
                if (x < world.length - 1 && !world[x + 1][y].description().equals("wall")) {
                    return updateWorld(world, x + 1, y);
                }
                break;
            case LEFT:
                if (x > 0 && !world[x - 1][y].description().equals("wall")) {
                    return updateWorld(world, x - 1, y);
                }
                break;
            default:
                break;
        }
        return false;
    }

    private boolean updateWorld(TETile[][] world, int newX, int newY) {
        boolean choppedTree = false;
        if (world[newX][newY].description().equals("lantern")) {
            hitLantern = true;
        } else if (world[newX][newY].description().equals("axe")) {
            hasAxe = true;
        }
        if (wasUnderTree) {
            world[position.getX()][position.getY()] = TREE;
        } else {
            world[position.getX()][position.getY()] = FLOOR;
        }

        if (!world[newX][newY].description().equals("tree") || hasAxe) {
            if (world[newX][newY].description().equals("tree")) {
                choppedTree = true;
            }
            world[newX][newY] = costume;
            wasUnderTree = false;
        } else {
            wasUnderTree = true;
        }
        position = new Position(newX, newY);
        return choppedTree;
    }

    @Override
    public void createOverlay(TETile[][] actualWorld, TETile[][] renderedWorld) {
        switch (direction) {
            case UP:
                for (int i = -1; i <= 1; i++) {
                    boolean hitWall = false;
                    int x = position.getX() + i;
                    int y = position.getY();
                    while (inBounds(actualWorld, x, y)
                            && !(hitWall && !actualWorld[x][y].description().equals("wall"))
                            && !actualWorld[x][y].description().equals("nothing")) {
                        if (actualWorld[x][y].description().equals("wall")) {
                            hitWall = true;
                        }
                        renderedWorld[x][y] = actualWorld[x][y];
                        y++;
                    }
                }
                break;
            case DOWN:
                for (int i = -1; i <= 1; i++) {
                    boolean hitWall = false;
                    int x = position.getX() + i;
                    int y = position.getY();
                    while (inBounds(actualWorld, x, y)
                            && !(hitWall && !actualWorld[x][y].description().equals("wall"))
                            && !actualWorld[x][y].description().equals("nothing")) {
                        if (actualWorld[x][y].description().equals("wall")) {
                            hitWall = true;
                        }
                        renderedWorld[x][y] = actualWorld[x][y];
                        y--;
                    }
                }
                break;
            case LEFT:
                for (int i = -1; i <= 1; i++) {
                    boolean hitWall = false;
                    int x = position.getX();
                    int y = position.getY() + i;
                    while (inBounds(actualWorld, x, y)
                            && !(hitWall && !actualWorld[x][y].description().equals("wall"))
                            && !actualWorld[x][y].description().equals("nothing")) {
                        if (actualWorld[x][y].description().equals("wall")) {
                            hitWall = true;
                        }
                        renderedWorld[x][y] = actualWorld[x][y];
                        x--;
                    }
                }
                break;
            case RIGHT:
                for (int i = -1; i <= 1; i++) {
                    boolean hitWall = false;
                    int x = position.getX();
                    int y = position.getY() + i;
                    while (inBounds(actualWorld, x, y)
                            && !(hitWall && !actualWorld[x][y].description().equals("wall"))
                            && !actualWorld[x][y].description().equals("nothing")) {
                        if (actualWorld[x][y].description().equals("wall")) {
                            hitWall = true;
                        }
                        renderedWorld[x][y] = actualWorld[x][y];
                        x++;
                    }
                }
                break;
            default:
                break;
        }
    }

    public boolean hasHitLantern() {
        return hitLantern;
    }

    public boolean hasAxe() {
        return hasAxe;
    }
}
