package cgl.iotcloud.samples.arducopter.client.control;

public class JoyStick {
    private float xrange[] = new float[2];
    private float yrange[] = new float[2];

    private float initialX = 0;

    private float initialY = 0;

    private float xChange = .1f;

    private float yChange = .1f;

    private float x;
    private float y;

    public enum Action {
        INCR,
        DECR
    }

    public JoyStick(float x, float y,
                    float xrange[], float yrange[],
                    float xChange, float yChange) {
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
                if (x - xChange >= xrange[1]) {
                    x -= xChange;
                }
            }
        } else {
            if (action == Action.INCR) {
                if (y + yChange <= yrange[1]) {
                    y += yChange;
                }
            } else if (action == Action.DECR) {
                if (y - yChange >= yrange[1]) {
                    y -= yChange;
                }
            }
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void reset() {
        x = initialX;
        y = initialY;
    }
}