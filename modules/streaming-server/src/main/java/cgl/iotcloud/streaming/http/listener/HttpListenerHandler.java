package cgl.iotcloud.streaming.http.listener;

import io.netty.buffer.ChannelBufferInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageEvent;
import io.netty.channel.SimpleChannelUpstreamHandler;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpListenerHandler extends SimpleChannelUpstreamHandler {
    private Logger log = LoggerFactory.getLogger(HttpListenerHandler.class);

    private MessageReceiver receiver;

    public HttpListenerHandler(MessageReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (!(e instanceof HttpRequest)) {
            log.error("Unexpected message received..");
            return;
        }

        HttpRequest request = (HttpRequest) e.getMessage();
        ChannelBufferInputStream stream = new ChannelBufferInputStream(request.getContent());

        // call the message receiver
        receiver.messageReceived(stream);
    }
}
