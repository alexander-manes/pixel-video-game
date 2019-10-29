package byow.Core;

import byow.Core.WorldGeneration.WorldGenerator;
import byow.Core.WorldItems.Avatar;
import byow.Core.WorldItems.Item;
import byow.InputDemo.InputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;
import edu.princeton.cs.introcs.Stopwatch;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


import static byow.Core.Direction.*;
import static byow.TileEngine.Tileset.NOTHING;

public class Engine {
    /* Feel free to change the width and height. */
    private static final int WIDTH = 80;
    private static final int HEIGHT = 30;
    private Stopwatch stopwatch;
    private TERenderer ter = new TERenderer();
    // Initializes an inputSource that listens for Keyboard input
    private InputSource inputSource = new KeyboardListener();
    private String name = "";

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        Universe universe = createMainMenu(null);

        // Render again and again until stopping condition is met
        MouseListener mouseListener = new MouseListener(WIDTH, HEIGHT);

        double bestTime = getFastScore();
        double baseTime = universe.getBaseTime();

        // Opens up a main menu and handles input
        TETile[][] world = universe.getWorld();
        Avatar avatar = universe.getAvatar();
        ter.initialize(WIDTH, HEIGHT + 3);
        stopwatch = new Stopwatch();

        boolean running = true;
        while (running) {
            ter.renderFrame(createOverlay(world, universe));
            int xPos = mouseListener.getMouseX();
            int yPos = mouseListener.getMouseY();
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WIDTH - 10, HEIGHT + 1, world[xPos][yPos].description());
            double time = baseTime + stopwatch.elapsedTime();
            StdDraw.text(10, HEIGHT + 1, "Time Elapsed: " + Math.round(time));
            if (bestTime != Double.MAX_VALUE) {
                StdDraw.text(10, HEIGHT + 2, "Best Time is: " + Math.round(bestTime));
            }
            StdDraw.text(30, HEIGHT + 1, "Trees Left: " + universe.getNumTreesLeft());
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                boolean choppedTree = false;
                char nextChar = Character.toUpperCase(StdDraw.nextKeyTyped());
                switch (nextChar) {
                    case 'W':
                        choppedTree = avatar.move(world, UP);
                        break;
                    case 'A':
                        choppedTree = avatar.move(world, LEFT);
                        break;
                    case 'S':
                        choppedTree = avatar.move(world, DOWN);
                        break;
                    case 'D':
                        choppedTree = avatar.move(world, RIGHT);
                        break;
                    case ':':
                        if (Character.toUpperCase(inputSource.getNextKey()) == 'Q') {
                            universe.setBaseTime(time);
                            quit(universe);
                            System.exit(0);
                        }
                        break;
                    default:
                        break;
                }
                if (choppedTree) {
                    universe.decrementNumTreesLeft();
                    if (universe.noTreesLeft()) {
                        running = false;
                        gameOver(time, bestTime);
                    }
                }
            }
        }
        StdDraw.clear();
        ter.initialize(WIDTH, HEIGHT);
        handleExitScreen(bestTime);

    }

    private void gameOver(Double currTime, Double bestTime) {
        if (currTime < bestTime) {
            saveFastScore(currTime);
        }
        System.out.println("This is the line before exitscreen");
//        System.exit(666);
        //handle game over
    }

    private void handleExitScreen(Double bestTime) {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH - 45, HEIGHT  - 10, "You Win!");
        StdDraw.text(WIDTH - 45, HEIGHT - 15, "Your time was: " + bestTime);

        if (bestTime > getFastScore()) {
            StdDraw.text(WIDTH - 45, HEIGHT  - 20, "Your new high score is: " + bestTime);
        } else {
            StdDraw.text(WIDTH - 45, HEIGHT + 25, "The high score is: " + getFastScore());
        }
        StdDraw.show();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        char nextChar;
        Universe universe;

        int i = 0;
        nextChar = Character.toUpperCase(input.charAt(i));

        // Updates char until the world is created, loaded, or game is quit
        while (nextChar != 'N' && nextChar != 'L' && nextChar != 'Q' && i < input.length() - 1) {
            i++;
            nextChar = Character.toUpperCase(input.charAt(i));
        }

        if (nextChar == 'N') {
            i++;
            StringBuilder sb = new StringBuilder();
            nextChar = Character.toUpperCase(input.charAt(i));
            while (nextChar != 'S' && nextChar != 'Q') {
                sb.append(nextChar);
                i++;
                nextChar = Character.toUpperCase(input.charAt(i));
            }

            // At this point we will have a full seed and we can init the universe
            System.out.println(sb.toString());
            universe = initializeNewWorld(sb.toString());
            i++;
            // Now we can handle the rest of the string
            while (i < input.length()) {
                move(universe, input, i);
                i++;
            }
            ter.initialize(WIDTH, HEIGHT + 3);
            ter.renderFrame(universe.getWorld());
            return universe.getWorld();

        } else if (nextChar == 'L') {
            universe = loadGame(null);
            if (universe == null) {
                return null;
            }
            i++;
            while (i < input.length()) {
                move(universe, input, i);
                i++;
            }
            ter.initialize(WIDTH, HEIGHT + 3);
            ter.renderFrame(universe.getWorld());
            return universe.getWorld();

        } else {
            return null;
        }
    }



    // 123456789N12345SWWWASD:Q
    // This method is to abstract away handling the moving part of the input string
    private void move(Universe universe, String input, int i) {
        switch (Character.toUpperCase(input.charAt(i))) {
            case 'W':
                universe.getAvatar().move(universe.getWorld(), UP);
                break;
            case 'A':
                universe.getAvatar().move(universe.getWorld(), LEFT);
                break;
            case 'S':
                universe.getAvatar().move(universe.getWorld(), DOWN);
                break;
            case 'D':
                universe.getAvatar().move(universe.getWorld(), RIGHT);
                break;
            case ':':
                i++;
                if (Character.toUpperCase(input.charAt(i)) == 'Q') {
                    quit(universe);
                }
                break;
            default:
                break;
        }
    }

    private int getSeed(String input) {
        int seed = 0;
        for (int i = 0; i < input.length(); i++) {
            if (Character.isDigit(input.charAt(i))) {
                seed = seed * 10;
                seed += (int) input.charAt(i);
            } else {
                break;
            }
        }
        return seed;
    }

    private Universe initializeNewWorld(String seedString) {
        WorldGenerator worldGenerator = new WorldGenerator(HEIGHT, WIDTH, getSeed(seedString));
        TETile[][] world = worldGenerator.getWorld();
        Position lanternPosition = WorldGenerator.getRandomPosition(world, getSeed(seedString) + 1);
        TETile lanternTile = new TETile('!', Color.black, Color.yellow, "lantern");
        Item lantern = new Item(lanternPosition, lanternTile, world);

        Position axePosition = WorldGenerator.getRandomPosition(world, getSeed(seedString) + 2);
        TETile axeTile = new TETile('%', Color.white, Color.blue, "axe");
        Item axe = new Item(axePosition, axeTile, world);

        Position avatarPosition = WorldGenerator.getRandomPosition(world, getSeed(seedString));
        TETile avatarTile = new TETile('@', Color.white, Color.black, name);
        Avatar avatar = new Avatar(avatarPosition, avatarTile, world);

        stopwatch = new Stopwatch();

        return new Universe(avatar, world, lantern, axe, worldGenerator.getNumTrees(), 0.0);
    }

    private Universe createMainMenu(Universe universe) {
        drawMenuText();
        char nextChar = Character.toUpperCase(inputSource.getNextKey());
        switch (nextChar) {
            case 'N':
                return handleSeedMenu();
            case 'R':
                return handleRename();
            case 'L':
                return loadGame(universe);
            case 'Q':
                System.exit(0);
                return null;
            default:
                return createMainMenu(universe);
        }
    }

    private Universe handleRename() {
        StdDraw.clear();
        StdDraw.text(0.5, 0.5, "Enter a name, then press . to return to get started: ");

        StringBuilder builder = new StringBuilder();
        char nextChar = Character.toUpperCase(inputSource.getNextKey());


        while (nextChar != '.') {
            StdDraw.clear();
            StdDraw.text(0.5, 0.5, "Enter a name, then press . to get started: ");
            builder.append(nextChar);
            StdDraw.text(0.5, 0.2, builder.toString());
            nextChar = Character.toUpperCase(inputSource.getNextKey());
        }

        this.name = builder.toString();
        StdDraw.clear();
        return handleSeedMenu();
    }

    private Universe handleSeedMenu() {
        StdDraw.clear();
        StdDraw.text(0.5, 0.5, "Enter an input string and press S to start: ");

        StringBuilder builder = new StringBuilder();
        char nextChar = Character.toUpperCase(inputSource.getNextKey());

        while (nextChar != 'S') {
            if (Character.isDigit(nextChar)) {
                StdDraw.clear();
                StdDraw.text(0.5, 0.5, "Enter an input string and press S to start: ");
                builder.append(nextChar);
                StdDraw.text(0.5, 0.2, builder.toString());
            }
            nextChar = Character.toUpperCase(inputSource.getNextKey());
        }
        System.out.println(builder.toString());
        StdDraw.clear();
        return initializeNewWorld(builder.toString());
    }

    private double getFastScore() {
        File f = new File("./save_score.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                return (Double) os.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
            } catch (IOException e) {
                System.out.println(e);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
            }
        }
        return Double.MAX_VALUE;
    }

    private void saveFastScore(Double fastScore) {
        File f = new File("./save_score.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(fastScore);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private Universe loadGame(Universe universe) {
        File f = new File("./save_data.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                return (Universe) os.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
        StdDraw.text(0.5, 0.2, "Game not found");
        return universe;
    }

    private Universe quit(Universe save) {
        File f = new File("./save_data.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(save);
            System.exit(0);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    private void drawMenuText() {
        StdDraw.text(0.5, 0.8, "CS61B: LumberJacked");
        StdDraw.text(0.5, 0.6, "New Game (N)");
        StdDraw.text(0.5, 0.5, "Load Game (L)");
        StdDraw.text(0.5, 0.4, "Rename your Avatar (R)");
        StdDraw.text(0.5, 0.3, "Quit (Q)");
    }


    private TETile[][] createOverlay(TETile[][] actualWorld, Universe universe) {
        Avatar avatar = universe.getAvatar();
        if (avatar.hasHitLantern()) {
            return actualWorld;
        }

        TETile[][] renderedWorld = new TETile[actualWorld.length][actualWorld[0].length];
        for (int i = 0; i < renderedWorld.length; i++) {
            for (int j = 0; j < renderedWorld[i].length; j++) {
                renderedWorld[i][j] = NOTHING;
            }
        }

        avatar.createOverlay(actualWorld, renderedWorld);
        universe.getLantern().createOverlay(actualWorld, renderedWorld);

        if (!avatar.hasAxe()) {
            universe.getAxe().createOverlay(actualWorld, renderedWorld);
        }

        return renderedWorld;
    }

}
