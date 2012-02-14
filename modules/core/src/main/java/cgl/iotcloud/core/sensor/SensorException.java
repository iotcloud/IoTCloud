package cgl.iotcloud.core.sensor;

/**
 * A checked exception to be thrown for invalid configuration handling
 */
public class SensorException extends Exception {
    public SensorException() {
        super();
    }

    public SensorException(String s) {
        super(s);
    }

    public SensorException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
