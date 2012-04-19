package cgl.iotcloud.streaming.http.server;

import io.netty.channel.*;
import io.netty.handler.codec.http.HttpChunk;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;

public class OutboundHandler extends SimpleChannelUpstreamHandler {
    private Logger log = LoggerFactory.getLogger(OutboundHandler.class);

    private boolean readingChunks = false;

    private HttpResponse response = null;

    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e)
            throws Exception {
        HttpClientEndpoint endpoint = (HttpClientEndpoint) ctx.getChannel().getAttachment();
        if (!readingChunks) {
            response = (HttpResponse) e.getMessage();
            if (!response.isChunked()) {
                if (!isKeepAlive(response)) {
                    log.debug("Closing the connection");
                    endpoint.responseDone(Channels.succeededFuture(e.getChannel()), true);
                } else {
                    log.debug("Keeping the connection");
                    endpoint.responseDone(Channels.succeededFuture(e.getChannel()), false);
                }
            } else {
                readingChunks = true;
            }
        } else {
            HttpChunk chunk = (HttpChunk) e.getMessage();
            if (chunk.isLast() && !isKeepAlive(response)) {
                log.debug("Closing the connection");
                endpoint.responseDone(Channels.succeededFuture(e.getChannel()), true);
            } else if (chunk.isLast() && isKeepAlive(response)) {
                endpoint.responseDone(Channels.succeededFuture(e.getChannel()), false);
            }
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        log.error("Exception occured:", e.getCause());
        if (e.getChannel().isConnected()) {
            e.getChannel().close();
        }
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        log.info("Channel closed........................");
    }
}

