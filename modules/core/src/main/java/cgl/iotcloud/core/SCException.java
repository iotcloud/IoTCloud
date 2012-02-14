package cgl.iotcloud.core;

/**
 * Generic exception thrown by the SC.
 */
public class SCException extends RuntimeException {
    public SCException() {
        super();
    }

    public SCException(String s) {
        super(s);
    }

    public SCException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
