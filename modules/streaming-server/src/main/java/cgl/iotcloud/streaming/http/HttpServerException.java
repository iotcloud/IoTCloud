package cgl.iotcloud.streaming.http;

/**
 * A Generic exception used by the module
 */
public class HttpServerException extends Exception {
    public HttpServerException() {
        super();
    }

    public HttpServerException(String s) {
        super(s);
    }
}
