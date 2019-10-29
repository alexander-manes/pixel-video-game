package byow.Core;

import byow.InputDemo.InputSource;
import edu.princeton.cs.introcs.StdDraw;

import java.util.ArrayList;

// Nested class that handles listening for keyboard input
public class KeyboardListener implements InputSource {
    ArrayList<Character> list = new ArrayList<>();
    @Override
    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                return Character.toUpperCase(StdDraw.nextKeyTyped());
            }
        }
    }

    @Override
    public boolean possibleNextInput() {
        return true;
    }
}
