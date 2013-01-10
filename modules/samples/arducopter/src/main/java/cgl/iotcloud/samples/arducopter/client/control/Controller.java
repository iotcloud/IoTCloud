package cgl.iotcloud.samples.arducopter.client.control;

public class Controller {
    private JoyStick left;

    private JoyStick right;

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
        JoyStick stick = null;
        if (s == StickPos.LEFT) {
            stick = left;
        } else if (s == StickPos.RIGHT) {
            stick = right;
        } else {
            return;
        }

        if (d == Direction.UP) {
            stick.change(Axis.Y, JoyStick.Action.INCR);
        } else if (d == Direction.DOWN) {
            stick.change(Axis.Y, JoyStick.Action.DECR);
        } else if (d == Direction.LEFT) {
            stick.change(Axis.X, JoyStick.Action.INCR);
        } else if (d == Direction.RIGHT) {
            stick.change(Axis.X, JoyStick.Action.DECR);
        }
    }
}
