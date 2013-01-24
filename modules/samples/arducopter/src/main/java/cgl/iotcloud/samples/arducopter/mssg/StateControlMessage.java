package cgl.iotcloud.samples.arducopter.mssg;

import cgl.iotcloud.core.message.data.ObjectDataMessage;

public class StateControlMessage extends ObjectDataMessage {
    private boolean active;

    public StateControlMessage() {
    }

    public StateControlMessage(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
