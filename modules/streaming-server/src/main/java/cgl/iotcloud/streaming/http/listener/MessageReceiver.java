package cgl.iotcloud.streaming.http.listener;

import java.io.InputStream;

public interface MessageReceiver {
    void messageReceived(InputStream in);
}
