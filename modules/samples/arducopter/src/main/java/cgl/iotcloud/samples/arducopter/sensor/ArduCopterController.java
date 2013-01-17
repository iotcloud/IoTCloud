package cgl.iotcloud.samples.arducopter.sensor;

public class ArduCopterController {
    private float leftX;

    private float leftY;

    private float rightX;

    private float rightY;

    private boolean active = false;

    public float getLeftX() {
        return leftX;
    }

    public float getLeftY() {
        return leftY;
    }

    public float getRightX() {
        return rightX;
    }

    public float getRightY() {
        return rightY;
    }

    public boolean isActive() {
        return active;
    }

    public void setLeftX(float leftX) {
        this.leftX = leftX;
    }

    public void setLeftY(float leftY) {
        this.leftY = leftY;
    }

    public void setRightX(float rightX) {
        this.rightX = rightX;
    }

    public void setRightY(float rightY) {
        this.rightY = rightY;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float getPitch() {
        return leftY;
    }

    public float getRoll() {
        return leftX;
    }

    public float getYaw() {
        return rightX;
    }

    public float getThrust() {
        return rightY;
    }
}
