package cgl.iotcloud.samples.arducopter.mssg;

import cgl.iotcloud.core.message.data.ObjectDataMessage;

public class AttitudeMessage extends ObjectDataMessage {

    private float pitch;
    private float pitchSpeed;
    private float roll;
    private float rollSpeed;
    private float yaw;
    private float yawSpeed;

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setPitchSpeed(float pitchSpeed) {
        this.pitchSpeed = pitchSpeed;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public void setRollSpeed(float rollSpeed) {
        this.rollSpeed = rollSpeed;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setYawSpeed(float yawSpeed) {
        this.yawSpeed = yawSpeed;
    }

    public float getPitch() {
        return pitch;
    }

    public float getPitchSpeed() {
        return pitchSpeed;
    }

    public float getRoll() {
        return roll;
    }

    public float getRollSpeed() {
        return rollSpeed;
    }

    public float getYaw() {
        return yaw;
    }

    public float getYawSpeed() {
        return yawSpeed;
    }
}
