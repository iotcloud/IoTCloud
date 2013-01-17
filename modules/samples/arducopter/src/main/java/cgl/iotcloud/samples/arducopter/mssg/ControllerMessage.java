package cgl.iotcloud.samples.arducopter.mssg;

import cgl.iotcloud.core.message.data.ObjectDataMessage;

public class ControllerMessage extends ObjectDataMessage {
    private float leftX;

    private float leftY;

    private float rightX;

    private float rightY;

    private boolean active = false;

    public ControllerMessage(boolean active, float leftX, float leftY, float rightX, float rightY) {
        this.leftX = leftX;
        this.leftY = leftY;
        this.rightX = rightX;
        this.rightY = rightY;
        this.active = active;
    }

    public ControllerMessage() {
    }

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

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
