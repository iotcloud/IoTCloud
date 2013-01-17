package cgl.iotcloud.samples.arducopter.sensor;

public class ArduCopterController {
    private int yaw;

    private int thrust;

    private int roll;

    private int pitch;

    private int r5, r6, r7, r8;

    private boolean active = false;

    private boolean newData = false;

    public int getYaw() {
        return yaw;
    }

    public int getThrust() {
        return thrust;
    }

    public int getRoll() {
        return roll;
    }

    public int getPitch() {
        return pitch;
    }

    public boolean isActive() {
        return active;
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
    }

    public void setThrust(int thrust) {
        this.thrust = thrust;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
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

    public boolean isNewData() {
        return newData;
    }

    public void setNewData(boolean newData) {
        this.newData = newData;
    }
}
