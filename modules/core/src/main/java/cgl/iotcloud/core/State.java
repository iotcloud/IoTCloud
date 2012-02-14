package cgl.iotcloud.core;

/**
 * Define states used by various SC classes
 */
public enum State {
    DEFAULT("DEFAULT", 0),
    INITIALIZED("INIT", 1),
    STARTED("STARTED", 2),
    STOPPED("STOPPED", 5),
    DESTROYED("DESTROYED", 6);

    private final String state;

    private final int stateValue;

    State(String state, int stateValue) {
        this.state = state;
        this.stateValue = stateValue;
    }

    public String getState() {
        return state;
    }

    public int getStateValue() {
        return stateValue;
    }

    @Override
    public String toString() {
        return state;
    }
}
