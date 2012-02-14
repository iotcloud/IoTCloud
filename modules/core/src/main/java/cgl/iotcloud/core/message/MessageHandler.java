package cgl.iotcloud.core.message;

/**
 * This will be used by the listening interfaces to handle a message when it is delivered.
 */
public interface MessageHandler {
    void onMessage(SensorMessage message);
}
