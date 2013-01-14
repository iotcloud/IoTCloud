package cgl.iotcloud.samples.arducopter.sensor;

public class ArduCopterController {
    private double leftX;

    private double leftY;

    private double rightX;

    private double rightY;

    private boolean active = false;

    public double getLeftX() {
        return leftX;
    }

    public double getLeftY() {
        return leftY;
    }

    public double getRightX() {
        return rightX;
    }

    public double getRightY() {
        return rightY;
    }

    public boolean isActive() {
        return active;
    }

    public void setLeftX(double leftX) {
        this.leftX = leftX;
    }

    public void setLeftY(double leftY) {
        this.leftY = leftY;
    }

    public void setRightX(double rightX) {
        this.rightX = rightX;
    }

    public void setRightY(double rightY) {
        this.rightY = rightY;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float getPitch() {
        return (float) leftY;
    }

    public float getRoll() {
        return (float) leftX;
    }

    public float getYaw() {
        return (float) rightX;
    }

    public float getThrust() {
        return (float) rightY;
    }
}
