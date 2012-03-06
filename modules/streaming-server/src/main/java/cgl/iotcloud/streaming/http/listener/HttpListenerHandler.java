package cgl.iotcloud.streaming.http.listener;

import io.netty.buffer.ChannelBufferInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageEvent;
import io.netty.channel.SimpleChannelUpstreamHandler;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class HttpListenerHandler extends SimpleChannelUpstreamHandler {
    private Logger log = LoggerFactory.getLogger(HttpListenerHandler.class);

    /** Message receiver to be called */
    private MessageReceiver receiver;

    /** Path of the URL */
    private String path;

    public HttpListenerHandler(MessageReceiver receiver, String path) {
        this.receiver = receiver;
        this.path = path;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (!(e instanceof HttpRequest)) {
            log.error("Unexpected message received..");
            return;
        }


        HttpRequest request = (HttpRequest) e.getMessage();

        String uri = request.getUri();
        if (uri != null && uri.startsWith("http://")) {
            // get the path portion
            URL url = new URL(uri);
            if (url.getPath() != null && !url.getPath().equals(path)) {
                return;
            }
        } else {
            if (!path.equals(uri)) {
                return;
            }
        }

        ChannelBufferInputStream stream = new ChannelBufferInputStream(request.getContent());

        // call the message receiver
        receiver.messageReceived(stream);
    }
}
