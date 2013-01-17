package cgl.iotcloud.samples.arducopter.mssg;

import cgl.iotcloud.core.message.data.ObjectDataMessage;

public class ControllerMessage extends ObjectDataMessage {
    private boolean active = false;

    private int yaw;

    private int thrust;

    private int pitch;

    private int roll;

    private int r5;

    private int r6;

    private int r7;

    private int r8;

    public ControllerMessage(boolean active, int yaw, int thrust, int pitch, int roll) {
        this.active = active;
        this.yaw = yaw;
        this.thrust = thrust;
        this.pitch = pitch;
        this.roll = roll;
    }

    public ControllerMessage() {
    }

    public boolean isActive() {
        return active;
    }

    public int getYaw() {
        return yaw;
    }

    public int getThrust() {
        return thrust;
    }

    public int getPitch() {
        return pitch;
    }

    public int getRoll() {
        return roll;
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
    }

    public void setThrust(int thrust) {
        this.thrust = thrust;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getR5() {
        return r5;
    }

    public int getR6() {
        return r6;
    }

    public int getR7() {
        return r7;
    }

    public int getR8() {
        return r8;
    }

    public void setR5(int r5) {
        this.r5 = r5;
    }

    public void setR6(int r6) {
        this.r6 = r6;
    }

    public void setR7(int r7) {
        this.r7 = r7;
    }

    public void setR8(int r8) {
        this.r8 = r8;
    }
}
