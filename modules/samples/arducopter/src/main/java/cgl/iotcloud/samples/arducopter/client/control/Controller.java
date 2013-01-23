package cgl.iotcloud.samples.arducopter.client.control;

import cgl.iotcloud.samples.arducopter.client.ArduClient;
import cgl.iotcloud.samples.arducopter.client.control.joystick.JS;
import cgl.iotcloud.samples.arducopter.mssg.ControllerMessage;

public class Controller {
    private JoyStickModel left;

    private JoyStickModel right;

    private ArduClient client;

    private boolean active;

    private int[] yawRange = new int[]{1113, 1901};
    private int[] thrustRange = new int[]{1127, 1895};

    private int[] rollChange = new int[]{1113, 1901};
    private int[] pitchChange = new int[]{1116, 1897};

    private JS js;

    public Controller(ArduClient client) {
        this.client = client;
        left = new JoyStickModel(1507, 1127, new int[]{1113, 1901}, new int[]{1127, 1895}, 10, 10);
        right = new JoyStickModel(1497, 1507, new int[]{1113, 1901}, new int[]{1116, 1897}, 10, 10);

        js = new JS(this);
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
        JoyStickModel stick;
        if (s == StickPos.LEFT) {
            stick = left;
        } else if (s == StickPos.RIGHT) {
            stick = right;
        } else {
            return;
        }

        if (d == Direction.UP) {
            stick.change(Axis.Y, JoyStickModel.Action.INCR);
        } else if (d == Direction.DOWN) {
            stick.change(Axis.Y, JoyStickModel.Action.DECR);
        } else if (d == Direction.RIGHT) {
            stick.change(Axis.X, JoyStickModel.Action.INCR);
        } else if (d == Direction.LEFT) {
            stick.change(Axis.X, JoyStickModel.Action.DECR);
        }
        // send a message
        client.getControlsSender().send(createControllerMessage());
    }

    public boolean isActive() {
        return active;
    }

    public void axisChanged(float x, float y, float z, float r) {
        int yaw = (int)(((r + 1) * (yawRange[1] - yawRange[0]) / 2) + yawRange[0]);
        int thrust = (int)(((z + 1) * (thrustRange[1] - thrustRange[0]) / 2) + thrustRange[0]);
        int roll = (int)(((x + 1) * (rollChange[1] - rollChange[0]) / 2) + rollChange[0]);
        int pitch = (int)(((y + 1) * (pitchChange[1] - pitchChange[0]) / 2) + pitchChange[0]);

        ControllerMessage message = new ControllerMessage(active, yaw, thrust, pitch, roll);

        message.setR5(1901);
        message.setR6(1114);
        message.setR7(1552);
        message.setR8(900);

        if (client.getControlsSender() != null ) {
            client.getControlsSender().send(message);
        }
    }

    public void buttonChanged(int num) {

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

    public JoyStickModel getRight() {
        return right;
    }

    public JoyStickModel getLeft() {
        return left;
    }

    public void reset() {
        left.reset();
        right.reset();
    }
}
