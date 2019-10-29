package byow.Core;

import byow.Core.WorldItems.Avatar;
import byow.Core.WorldItems.Item;
import byow.TileEngine.TETile;

import java.io.Serializable;

public class Universe implements Serializable {
    private final Item lantern;
    private final Item axe;
    private final Avatar avatar;
    private final TETile[][] world;
    private int numTreesLeft;
    private double baseTime;

    public Universe(Avatar avatar, TETile[][] world, Item lantern, Item axe, int numTreesLeft, double baseTime) {
        this.avatar = avatar;
        this.world = world;
        this.lantern = lantern;
        this.axe = axe;
        this.numTreesLeft = numTreesLeft;
        baseTime = 0.0;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public TETile[][] getWorld() {
        return world;
    }

    public Item getLantern() {
        return lantern;
    }

    public Item getAxe() {
        return axe;
    }

    public void decrementNumTreesLeft() {
        numTreesLeft--;
    }

    public boolean noTreesLeft() {
        return numTreesLeft == 0;
    }

    public double getBaseTime() {
        return baseTime;
    }

    public int getNumTreesLeft() {
        return numTreesLeft;
    }

    public void setBaseTime(double baseTime) {
        this.baseTime = baseTime;
    }
}
