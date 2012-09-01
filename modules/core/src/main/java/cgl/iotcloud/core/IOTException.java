package cgl.iotcloud.core;

public class IOTException extends Exception {
    public IOTException(String message) {
        super(message);
    }

    public IOTException(String message, Throwable cause) {
        super(message, cause);
    }
}
