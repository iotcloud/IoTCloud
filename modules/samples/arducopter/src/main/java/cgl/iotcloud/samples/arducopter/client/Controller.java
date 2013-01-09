package cgl.iotcloud.samples.arducopter.client;

public class Controller {
    private Stick left;

    private Stick right;

    public enum Axis {
        X,
        Y
    }

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public enum StickPos {
        LEFT,
        RIGHT
    }

    public void move(StickPos s, Direction d) {
        if (s == StickPos.LEFT) {
            left.
        }
    }

    private class Stick {
        double x;
        double y;

        private Stick(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public void change(Axis a) {
            if (a == Axis.X) {
                x += .1;
            } else {
                y += .1;
            }
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}
