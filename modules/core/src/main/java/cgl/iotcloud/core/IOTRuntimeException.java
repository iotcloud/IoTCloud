package cgl.iotcloud.core;

/**
 * Generic exception thrown by the SC.
 */
public class IOTRuntimeException extends RuntimeException {
    public IOTRuntimeException() {
        super();
    }

    public IOTRuntimeException(String s) {
        super(s);
    }

    public IOTRuntimeException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
