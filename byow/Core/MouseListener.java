package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

public class MouseListener {
    private int width;
    private int height;
    public MouseListener(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getMouseX() {
        return (int) Double.max(0, Double.min(StdDraw.mouseX(), width - 1));
    }

    public int getMouseY() {
        return (int) Double.max(0, Double.min(StdDraw.mouseY(), height - 1));
    }

}

