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
        JoyStick stick;
        if (s == StickPos.LEFT) {
            stick = left;
        } else {
            stick = right;
        }

        if (d == Direction.UP) {
            stick.change(Axis.Y, .1);
        } else if (d == Direction.DOWN) {
            stick.change(Axis.Y, -.1);
        } else if (d == Direction.LEFT) {
            stick.change(Axis.X, .1);
        } else if (d == Direction.)

    }


}
