package cgl.iotcloud.samples.arducopter.client;

public interface Updater {
    public void updateAttitudeData(float pitch, float pitchSpeed, float roll,
                                   float rollSpeed, float yaw, float yawSpeed);

    public void updateControlData(float pitch, float roll, float thrust, float yaw);

    public void updateMRIData(long timeUsec, int xacc, int xgyro, int xmag,
                              int yacc, int ygyro, int ymag, int zacc, int zgyro, int zmag);

    public void updateStateData(String mode, boolean armed, boolean guided);

    public void updateVHData(float airSpeed, float alt, float climb,
                             float groundSpeed, short heading, short throttle);
}
