package cgl.iotcloud.samples.arducopter.mssg;

import cgl.iotcloud.core.message.data.ObjectDataMessage;

public class StateMessage extends ObjectDataMessage {

    private String mode;
    private boolean armed;
    private boolean guided;

    public String getMode() {
        return mode;
    }

    public boolean isArmed() {
        return armed;
    }

    public boolean isGuided() {
        return guided;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setArmed(boolean armed) {
        this.armed = armed;
    }

    public void setGuided(boolean guided) {
        this.guided = guided;
    }
}
