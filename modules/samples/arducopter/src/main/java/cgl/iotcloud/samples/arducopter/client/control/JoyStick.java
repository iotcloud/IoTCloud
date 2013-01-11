package cgl.iotcloud.samples.arducopter.client.control;

public class JoyStick {
    private double xrange[] = new double[2];
    private double yrange[] = new double[2];

    private double initialX = 0;

    private double initialY = 0;

    private double xChange = .1;

    private double yChange = .1;

    private double x;
    private double y;

    public enum Action {
        INCR,
        DECR
    }

    public JoyStick(double x, double y,
                     double xrange[], double yrange[],
                     double xChange, double yChange) {
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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void reset() {
        x = initialX;
        y = initialY;
    }
}