package cgl.iotcloud.core.stream;

import cgl.iotcloud.core.Listener;
import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.State;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.data.StreamDataMessage;
import cgl.iotcloud.streaming.http.listener.MessageReceiver;
import cgl.iotcloud.streaming.http.listener.core.HttpCoreListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * A streaming message Listener. This is used by the clients for listening to sensor data.
 */
public class StreamingListener implements Listener {
    private Logger log = LoggerFactory.getLogger(StreamingListener.class);

    /** The listener */
    private HttpCoreListener listener;
    /** Port of listening */
    private int port;
    /** path of listening */
    private String path;
    /** Message handler to be called after receiving a message */
    private MessageHandler messageHandler;
    /** state of the listener */
    private State state = State.DEFAULT;

    /**
     * create a listener withe the given port and the path
     *
     * @param port port to listen
     * @param path path to listen
     */
    public StreamingListener(int port, String path) {
        this.port = port;
        this.path = path;
    }

    /**
     * Set the message handler
     *
     * @param messageHandler message handler to be set
     */
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * Start the listener
     */
    public void start() {
        if (state != State.INITIALIZED) {
            throw new IllegalStateException("State should be initialized");
        }
        state = State.STARTED;
        try {
            listener.start();
        } catch (Exception e) {
            handleException("Failed to start the streaming listener", e);
        }
    }

    /**
     * Stop the listener
     */
    public void stop() {
        if (state != State.STOPPED) {
            throw new IllegalStateException("State should be started");
        }
        state = State.STOPPED;
        listener.stop();
    }

    /**
     * Get the current state of the listener
     *
     * @return current state of the listener
     */
    public String getState() {
        return state.toString();
    }

    /**
     * Initialize the listener. This method has to be called before the start method
     */
    public void init() {
        if (state != State.DEFAULT) {
            throw new IllegalStateException("Cannot initialize an already initialized listener");
        }
        listener = new HttpCoreListener(port, new Receiver(), path);
        state = State.INITIALIZED;
    }

    /**
     * A private message receiver. This will call the message handler
     */
    private class Receiver implements MessageReceiver {
        public void messageReceived(InputStream in) {
            StreamDataMessage dataMessage = new StreamDataMessage();
            dataMessage.setInputStream(in);
            messageHandler.onMessage(dataMessage);
        }
    }

    public void destroy() {
    }

    protected void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }

    protected void handleException(String s) {
        log.error(s);
        throw new SCException(s);
    }
}
