package cgl.iotcloud.samples.arducopter.client.control;

import cgl.iotcloud.samples.arducopter.client.ArduClient;
import cgl.iotcloud.samples.arducopter.mssg.ControllerMessage;

public class Controller {
    private JoyStick left;

    private JoyStick right;

    private ArduClient client;

    private boolean active;

    public Controller(ArduClient client) {
        this.client = client;
        left = new JoyStick(1507, 1127, new int[]{1113, 1901}, new int[]{1127, 1895}, 10, 10);
        right = new JoyStick(1497, 1507, new int[]{1113, 1901}, new int[]{1116, 1897}, 10, 10);
    }

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
        } else if (s == StickPos.RIGHT) {
            stick = right;
        } else {
            return;
        }

        if (d == Direction.UP) {
            stick.change(Axis.Y, JoyStick.Action.INCR);
        } else if (d == Direction.DOWN) {
            stick.change(Axis.Y, JoyStick.Action.DECR);
        } else if (d == Direction.RIGHT) {
            stick.change(Axis.X, JoyStick.Action.INCR);
        } else if (d == Direction.LEFT) {
            stick.change(Axis.X, JoyStick.Action.DECR);
        }
        // send a message
        client.getControlsSender().send(createControllerMessage());
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        client.getControlsSender().send(createControllerMessage());
    }

    private ControllerMessage createControllerMessage() {
        ControllerMessage message = new ControllerMessage(active, left.getX(), left.getY(), right.getY(), right.getX());
        message.setR5(1901);
        message.setR6(1114);
        message.setR7(1552);
        message.setR8(900);
        return message;
    }

    public JoyStick getRight() {
        return right;
    }

    public JoyStick getLeft() {
        return left;
    }

    public void reset() {
        left.reset();
        right.reset();
    }
}
