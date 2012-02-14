package cgl.iotcloud.streaming.http.server;

/**
 * Represent basic information needed by a HttpEndpoint.
 */
public abstract class HttpEndpoint {
    /** URL path */
    protected String path;
    /** Port */
    protected int port;
    /** Weather SSL is enabled */
    private boolean isSSL = false;

    /**
     * Create a HttpEndpoint using path and port. SSL is turned off.
     * @param path url path
     * @param port port
     */
    public HttpEndpoint(String path, int port) {
        this.path = path;
        this.port = port;
    }

    /**
     * Create a HttpEndpoint using path port and SSL param. If SSL param is true
     * this is a Https endpoint.
     * @param path url path
     * @param port port
     * @param SSL HTTPS or HTTP
     */
    public HttpEndpoint(String path, int port, boolean SSL) {
        this.path = path;
        this.port = port;
        isSSL = SSL;
    }

    /**
     * Get the URL path
     * @return url path as a string
     */
    public String getPath() {
        return path;
    }

    /**
     * Get the port
     * @return port as an integer
     */
    public int getPort() {
        return port;
    }

    /**
     * Return weather HTTPs or HTTP
     * @return true if HTTPS and false if HTTP
     */
    public boolean isSSL() {
        return isSSL;
    }
}
