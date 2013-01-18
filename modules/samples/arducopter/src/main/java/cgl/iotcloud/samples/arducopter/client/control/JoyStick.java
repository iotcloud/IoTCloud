package cgl.iotcloud.samples.arducopter.client.control;

public class JoyStick {
    private int xrange[] = new int[2];
    private int yrange[] = new int[2];

    private int initialX = 0;

    private int initialY = 0;

    private int xChange = 1;

    private int yChange = 1;

    private int x;
    private int y;

    public enum Action {
        INCR,
        DECR
    }

    public JoyStick(int x, int y,
                    int xrange[], int yrange[],
                    int xChange, int yChange) {
        this.x = x;
        this.y = y;

        initialX = x;
        initialY = y;

        this.xrange = xrange;
        this.yrange = yrange;

        this.xChange = xChange;
        this.yChange = yChange;
    }

    public void change(Controller.Axis axis, Action action) {
        if (axis == Controller.Axis.X) {
            if (action == Action.INCR) {
                if (x + xChange <= xrange[1]) {
                    x += xChange;
                }
            } else if (action == Action.DECR) {
                if (x - xChange >= xrange[0]) {
                    x -= xChange;
                }
            }
        } else {
            if (action == Action.INCR) {
                if (y + yChange <= yrange[1]) {
                    y += yChange;
                }
            } else if (action == Action.DECR) {
                if (y - yChange >= yrange[0]) {
                    y -= yChange;
                }
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void reset() {
        x = initialX;
        y = initialY;
    }

    public int[] getXrange() {
        return xrange;
    }

    public int[] getYrange() {
        return yrange;
    }

    public int getInitialX() {
        return initialX;
    }

    public int getInitialY() {
        return initialY;
    }

    public int getxChange() {
        return xChange;
    }

    public int getyChange() {
        return yChange;
    }
}