package byow.Core.WorldGeneration;

import byow.Core.Direction;
import byow.Core.Position;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

import static byow.Core.Direction.*;

public class Miner {

    private Position position;
    private Direction direction;
    private int i = 0;
    private TETile[][] world;
    private int[][] fullPositions;
    private int roomId;
    private Random random;

    public Miner(Position position, Direction direction, TETile[][] world, Random random,
                 int[][] fullPositions, int roomId) {
        this.position = position;
        this.direction = direction;
        this.world = world;
        this.random = random;
        this.fullPositions = fullPositions;
        this.roomId = roomId;
    }

    public int mine() {
        int hitWall = -1;
        while (hitWall == -1) {
            hitWall = buildStraight(6);
            setNewDirection();
        }
        return hitWall;
    }

    private void setNewDirection() {
        if (random.nextInt(1) == 0) {
            switch (direction) {
                case DOWN: //bottom
                    if (position.getX() > 2) {
                        direction = LEFT;
                    } else {
                        direction = RIGHT;
                    }
                    break;
                case RIGHT: //right
                    if (position.getY() > 2) {
                        direction = DOWN;
                    } else {
                        direction = UP;
                    }
                    break;
                case UP: //top
                    if (position.getX() + 2 < world.length) {
                        direction = RIGHT;
                    } else {
                        direction = LEFT;
                    }
                    break;
                case LEFT: //left
                    if (position.getY() + 2 < world[0].length) {
                        direction = UP;
                    } else {
                        direction = DOWN;
                    }
                    break;
                default:
                    break;
            }
        } else {
            switch (direction) {
                case DOWN: //top
                    if (position.getX() + 2 < world.length) {
                        direction = RIGHT;
                    } else {
                        direction = LEFT;
                    }
                    break;
                case RIGHT: //left
                    if (position.getY() + 2 < world[0].length) {
                        direction = UP;
                    } else {
                        direction = DOWN;
                    }
                    break;
                case UP: //bottom
                    if (position.getX() > 2) {
                        direction = LEFT;
                    } else {
                        direction = RIGHT;
                    }
                    break;
                case LEFT: //right
                    if (position.getY() > 2) {
                        direction = DOWN;
                    } else {
                        direction = UP;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private int buildStraight(int cap) {
        for (int j = 0; j < cap; j++) {
            checkDirection();

            if (direction == UP || direction == DOWN) {
                if (direction == UP) {
                    position.incrementY();
                } else {
                    position.decrementY();
                }
            } else {
                if (direction == RIGHT) {
                    position.incrementX();
                } else {
                    position.decrementX();
                }
            }

            if (fullPositions[position.getX()][position.getY()] != -1) {
                return fullPositions[position.getX()][position.getY()];
            }

            if (position.getX() + 1 < fullPositions.length
                    && fullPositions[position.getX() + 1][position.getY()] != -1
                    && fullPositions[position.getX() + 1][position.getY()] != roomId) {
                direction = RIGHT;
            } else if (position.getX() > 0
                    && fullPositions[position.getX() - 1][position.getY()] != -1
                    && fullPositions[position.getX() - 1][position.getY()] != roomId) {
                direction = LEFT;
            } else if (position.getY() < fullPositions.length
                    && fullPositions[position.getX() + 1][position.getY()] != -1
                    && fullPositions[position.getX() + 1][position.getY()] != roomId) {
                direction = UP;
            } else if (position.getY() > 0
                    && fullPositions[position.getX()][position.getY() - 1] != -1
                    && fullPositions[position.getX()][position.getY()] != roomId) {
                direction = LEFT;
            }

            world[position.getX()][position.getY()] = Tileset.FLOOR;
            fullPositions[position.getX()][position.getY()] = roomId;
        }
        return -1;
    }

    private void checkDirection() {
        switch (direction) {
            case DOWN:
                if (position.getY() <= 1) {
                    setNewDirection();
                }
                break;
            case RIGHT:
                if (position.getX() + 2 >= world.length) {
                    setNewDirection();
                }
                break;
            case UP:
                if (position.getY() + 2 >= world[0].length) {
                    setNewDirection();
                }
                break;
            case LEFT:
                if (position.getX() <= 1) {
                    setNewDirection();
                }
                break;
            default:
                break;
        }
    }

}
