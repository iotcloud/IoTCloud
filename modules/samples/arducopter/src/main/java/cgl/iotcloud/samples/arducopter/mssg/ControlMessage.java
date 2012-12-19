package cgl.iotcloud.samples.arducopter.mssg;

import cgl.iotcloud.core.message.data.ObjectDataMessage;

public class ControlMessage extends ObjectDataMessage {

    private float pitch;
    private float roll;
    private float thrust;
    private float yaw;

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public float getThrust() {
        return thrust;
    }

    public void setThrust(float thrust) {
        this.thrust = thrust;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
