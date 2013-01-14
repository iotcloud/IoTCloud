package cgl.iotcloud.samples.arducopter.mssg;

import cgl.iotcloud.core.message.data.ObjectDataMessage;

public class ControllerMessage extends ObjectDataMessage {
    private double leftX;

    private double leftY;

    private double rightX;

    private double rightY;

    private boolean active = false;

    public ControllerMessage(double leftX, double leftY, double rightX, double rightY) {
        this.leftX = leftX;
        this.leftY = leftY;
        this.rightX = rightX;
        this.rightY = rightY;
    }

    public ControllerMessage() {
    }

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

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
