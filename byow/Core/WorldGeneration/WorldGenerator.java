package byow.Core.WorldGeneration;

import byow.Core.Direction;
import byow.Core.Position;
import byow.Core.UnionFind;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

import static byow.Core.Direction.*;
import static byow.TileEngine.Tileset.*;

public class WorldGenerator {
    private Random RANDOM;

    private int height;
    private int width;
    private int space;
    private int numTrees;
    private int spaceRemaining;
    private int totalFloorSpace = 0;
    private int floorSpaceRemaining = 0;
    private List<Room> rooms;
    private int[][] fullPositions;
    private TETile[][] world;

    public WorldGenerator(int height, int width, long seed) {
        // Checks for collisions of rooms
        this.height = height;
        this.world = new TETile[width][this.height];
        this.fullPositions = new int[width][this.height];
        this.width = width;
        this.space = this.height * width;
        spaceRemaining = space;
        RANDOM = new Random(seed);
        setTiles();
        setRooms(); //failing seed: N9339833829
        setHallways();
        setWalls();
        numTrees = placeForest();
    }

    private static void printArray(int[][] a) {
        for (int j = a[0].length - 1; j >= 0; j--) {
            for (int i = 0; i < a.length; i++) {
                if (a[i][j] < 10 && a[i][j] >= 0) {
                    System.out.print(a[i][j] + "  ");
                } else {
                    System.out.print(a[i][j] + " ");

                }
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
        System.out.println();

    }

    private static void printArray2(TETile[][] a) {
        for (int j = a[0].length - 1; j >= 0; j--) {
            for (int i = 0; i < a.length; i++) {
                if (a[i][j].equals(WALL)) {
                    System.out.print("W ");
                } else if (a[i][j].equals(FLOOR)) {
                    System.out.print("F ");
                } else {
                    System.out.print("0 ");
                }
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
        System.out.println();
    }

    public static Position getRandomPosition(TETile[][] world, int seed) {
        Random random = new Random(seed);
        ArrayList<Position> possiblePositions = new ArrayList<>();
        for (int j = 0; j < world.length; j++) {
            for (int k = 0; k < world[0].length; k++) {
                if (world[j][k] == FLOOR) {
                    possiblePositions.add(new Position(j, k));
                }
            }
        }
        return possiblePositions.get(random.nextInt(possiblePositions.size()));
    }

    public int getNumTrees() {
        return numTrees;
    }

    private void setTiles() {
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                if (i == width - 1 || i == 0 || j == height - 1 || j == 0) {
                    world[i][j] = NOTHING;
                    fullPositions[i][j] = -2;
                } else {
                    world[i][j] = NOTHING;
                    fullPositions[i][j] = -1;
                }
            }
        }
        // System.out.println(world[0][0] == Tileset.NOTHING);
    }

    // Sets number of rooms and initiates the room creation sequence
    private void setRooms() {
        rooms = new ArrayList<>();
        // Update space remaining and try and catch
        try {
            int i = 0;
            while (spaceRemaining > 0.6 * space) {
                Room r = setRoomCharacteristics();
                r.setId(i);
                placeRoom(r);
                rooms.add(r);
                spaceRemaining = spaceRemaining - ((r.getHeight() + 2) * (r.getWidth() + 2));
                i++;
            }
        } catch (StackOverflowError e) {
            return;
        }
    }

    private void placeRoom(Room room) {
        Position bottomLeft = room.getPosition();
        for (int i = bottomLeft.getX(); i < room.getWidth() + bottomLeft.getX(); i++) {
            for (int j = bottomLeft.getY(); j < room.getHeight() + bottomLeft.getY(); j++) {
                if ((i != room.getWidth() + bottomLeft.getX() - 1)
                        && (j != room.getHeight() + bottomLeft.getY() - 1)
                        && (i != bottomLeft.getX())
                        && (j != bottomLeft.getY())) {
                    fullPositions[i][j + 1] = room.getId();
                    fullPositions[i + 1][j] = room.getId();
                    fullPositions[i][j] = room.getId();
                    fullPositions[i - 1][j] = room.getId();
                    fullPositions[i][j - 1] = room.getId();
                }
                world[i][j] = FLOOR;
            }
        }
        totalFloorSpace += (room.getHeight() * room.getWidth());
        floorSpaceRemaining += (room.getHeight() * room.getWidth());
    }

    private int placeForest() {
        int count = 0;
//        while (floorSpaceRemaining > 0.8 * totalFloorSpace) {
        while (count < 1) {
            Position pos = getRandomPosition(world, RANDOM.nextInt());
            world[pos.getX()][pos.getY()] = TREE;
            floorSpaceRemaining--;
            count++;
        }
        return count;
    }

    // Room Method that sets the position of each room in the room positions array
    private Room setRoomCharacteristics() {
        // First create maxpos hashmap
        // While max does not hit full position get
        // the max y and add to max possibility key value pair
        // Checking if the point is placed in a full position, if it is pick again.
        int x = RANDOM.nextInt(width - 1) + 1;

        int y = RANDOM.nextInt(height - 1) + 1;
        while (fullPositions[x][y] != -1) {
            x = RANDOM.nextInt(width);
            y = RANDOM.nextInt(height);
        }
        Map<Integer, Integer> maxBoxes = new HashMap<>();
        int possibleWidth = 0;
        int capX = width / 4;
        // Creates a hashmap of possible box placements by maximum
        while (x + possibleWidth < width
                && (fullPositions[x + possibleWidth][y] == -1)
                && possibleWidth < capX) {
            int possibleHeight = 0;
            int capY = height / 4;
            while ((y + possibleHeight + 1 < height)
                    && (fullPositions[x + possibleWidth][y + possibleHeight] == -1)
                    && possibleHeight < capY) {
                if (possibleHeight >= 4 && possibleWidth >= 4) {
                    maxBoxes.put(possibleHeight, possibleWidth);
                }
                possibleHeight++;
            }

            if ((fullPositions[x + possibleWidth][y + possibleHeight] != -1)
                    && (possibleHeight < 4 || possibleWidth < 4)) {
                return setRoomCharacteristics();
            }
            possibleWidth++;
        }
        // If there are no possibilites in the region try again
        if (maxBoxes.size() == 0) {
            return setRoomCharacteristics();
        }

        int heightRoof = 4;
        int widthRoof = 4;
        int selection = RANDOM.nextInt(maxBoxes.size());
        int j = 0;
        for (Integer maxHeight : maxBoxes.keySet()) {
            if (j == selection) {
                heightRoof = maxHeight;
                widthRoof = maxBoxes.get(heightRoof);
                break;
            }
            j++;
        }
        int roomWidth = RANDOM.nextInt(widthRoof - 3) + 5;
        int roomHeight = RANDOM.nextInt(heightRoof - 3) + 5;

        return new Room(new Position(x, y), roomHeight, roomWidth);

    }

    private void setHallways() {
        UnionFind roomTracker = new UnionFind(rooms.size());
        while (!roomTracker.allConnected()) {
            Room room = rooms.get(RANDOM.nextInt(rooms.size()));
            int id = setNewHallway(room);

            roomTracker.union(id, room.getId());
        }
    }

    private int setNewHallway(Room room) {
        // Pick an origin room randomly

        final int distanceFromEdge = 2;
        // Pick a random side
        Position startingPoint = null;

        Direction wall = Direction.valueOf(RANDOM.nextInt(3));

        int roomX = room.getPosition().getX();
        int roomY = room.getPosition().getY();
        if (wall == DOWN) {
            if (roomY - distanceFromEdge > 0) {
                int x = RANDOM.nextInt(room.getWidth() - 3) + roomX + 1;
                startingPoint = new Position(x, roomY);
            } else {
                wall = RIGHT;
            }
        }
        if (wall == RIGHT) {
            wall = RIGHT;
            if (roomX + room.getWidth() + distanceFromEdge < width) {
                int y = RANDOM.nextInt(room.getHeight() - 3) + roomY + 1;
                startingPoint = new Position(roomX + room.getWidth() - 1, y);
            } else {
                wall = UP;
            }
        }
        if (wall == UP) {
            wall = UP;
            if (roomY + room.getHeight() + distanceFromEdge < height) {
                int x = RANDOM.nextInt(room.getWidth() - 3) + roomX + 1;
                startingPoint = new Position(x, roomY + room.getHeight() - 1);
            } else {
                wall = LEFT;
            }
        }
        if (wall == LEFT) {
            wall = LEFT;
            if (roomX - distanceFromEdge > 0) {
                int y = RANDOM.nextInt(room.getHeight() - 3) + roomY + 1;
                startingPoint = new Position(roomX, y);
            } else {
                wall = RIGHT;
            }
        }
        if (wall == RIGHT) {
            wall = RIGHT;
            int y = RANDOM.nextInt(room.getHeight() - 3) + roomY + 1;
            startingPoint = new Position(roomX + room.getWidth() - 1, y);
        }

        Miner hallwayMiner = new Miner(startingPoint, wall,
                world, RANDOM, fullPositions, room.getId());
        return hallwayMiner.mine();
    }

    // SET WALLS FUNCTION
    private void setWalls() {
        for (int i = 1; i < world.length - 1; i++) {
            for (int j = 1; j < world[0].length - 1; j++) {
                if (world[i][j] == NOTHING && (world[i + 1][j] == FLOOR
                        || (world[i - 1][j] == FLOOR) || (world[i][j + 1] == FLOOR)
                        || (world[i][j - 1] == FLOOR) || (world[i + 1][j - 1] == FLOOR)
                        || (world[i + 1][j + 1] == FLOOR) || (world[i - 1][j - 1] == FLOOR)
                        || (world[i - 1][j + 1] == FLOOR))) {
                    world[i][j] = Tileset.WALL;
                }
            }
        }
        for (int j = 1; j < world[0].length - 1; j++) {
            if (world[0][j] == NOTHING) {
                if ((world[1][j] == FLOOR)
                        || (world[0][j + 1] == FLOOR) || (world[0][j - 1] == FLOOR)
                        || (world[1][j - 1] == FLOOR) || (world[1][j + 1] == FLOOR)) {
                    world[0][j] = Tileset.WALL;
                }
            }
        }
        if (world[0][0] == NOTHING) {
            if ((world[0][1] == FLOOR) || (world[1][0] == FLOOR) || (world[1][1] == FLOOR)) {
                world[0][0] = Tileset.WALL;
            }
        }
        if (world[0][world[0].length - 1] == NOTHING) {
            if ((world[0][world[0].length - 2] == FLOOR)
                    || (world[1][world[0].length - 1] == FLOOR)
                    || (world[1][world[0].length - 2] == FLOOR)) {
                world[0][world[0].length - 1] = Tileset.WALL;
            }
        }
        for (int j = 1; j < world[0].length - 1; j++) {
            if (world[world.length - 1][j] == NOTHING) {
                if ((world[world.length - 1][j + 1] == FLOOR)
                        || (world[world.length - 2][j] == FLOOR)
                        || (world[world.length - 1][j - 1] == FLOOR)
                        || (world[world.length - 2][j - 1] == FLOOR)
                        || (world[world.length - 2][j + 1] == FLOOR)) {
                    world[world.length - 1][j] = Tileset.WALL;
                }
            }
        }
        if (world[world.length - 1][0] == NOTHING) {
            if ((world[world.length - 2][0] == FLOOR) || (world[world.length - 1][1] == FLOOR)
                    || (world[world.length - 2][1] == FLOOR)) {
                world[world.length - 1][0] = Tileset.WALL;
            }

        }
        if (world[world.length - 1][world[0].length - 1] == NOTHING) {
            if ((world[world.length - 1][world[0].length - 2] == FLOOR)
                    || (world[world.length - 2][world[0].length - 1] == FLOOR)
                    || (world[world.length - 2][world[0].length - 2] == FLOOR)) {
                world[world.length - 1][world[0].length - 1] = Tileset.WALL;
            }
        }
        for (int j = 1; j < world.length - 1; j++) {
            if (world[j][0] == NOTHING) {
                if ((world[j][1] == FLOOR)
                        || (world[j + 1][0] == FLOOR) || (world[j - 1][0] == FLOOR)
                        || (world[j - 1][1] == FLOOR) || (world[j + 1][1] == FLOOR)) {
                    world[j][0] = Tileset.WALL;
                }
            }
        }
        for (int j = 1; j < world.length - 1; j++) {
            if (world[j][world[0].length - 1] == NOTHING) {
                if ((world[j + 1][world[0].length - 1] == FLOOR)
                        || (world[j - 1][world[0].length - 1] == FLOOR)
                        || (world[j][world[0].length - 2] == FLOOR)
                        || (world[j - 1][world[0].length - 2] == FLOOR)
                        || (world[j + 1][world[0].length - 2] == FLOOR)) {
                    world[j][world[0].length - 1] = Tileset.WALL;
                }
            }
        }
    }

    public TETile[][] getWorld() {
        return world;
    }
}
