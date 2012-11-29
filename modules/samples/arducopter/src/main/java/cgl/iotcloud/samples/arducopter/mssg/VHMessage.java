package cgl.iotcloud.samples.arducopter.mssg;

import cgl.iotcloud.core.message.data.ObjectDataMessage;

public class VHMessage extends ObjectDataMessage {

    private float airSpeed;
    private float alt;
    private float climb;
    private float groundSpeed;
    private short heading;
    private short throttle;

    public float getAirSpeed() {
        return airSpeed;
    }

    public void setAirSpeed(float airSpeed) {
        this.airSpeed = airSpeed;
    }

    public float getAlt() {
        return alt;
    }

    public void setAlt(float alt) {
        this.alt = alt;
    }

    public float getClimb() {
        return climb;
    }

    public void setClimb(float climb) {
        this.climb = climb;
    }

    public float getGroundSpeed() {
        return groundSpeed;
    }

    public void setGroundSpeed(float groundSpeed) {
        this.groundSpeed = groundSpeed;
    }

    public short getHeading() {
        return heading;
    }

    public void setHeading(short heading) {
        this.heading = heading;
    }

    public short getThrottle() {
        return throttle;
    }

    public void setThrottle(short throttle) {
        this.throttle = throttle;
    }
}
