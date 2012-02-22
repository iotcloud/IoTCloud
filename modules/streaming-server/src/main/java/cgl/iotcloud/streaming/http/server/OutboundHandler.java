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
        if (!readingChunks) {
            response = (HttpResponse) e.getMessage();
            if (!response.isChunked()) {
//                if (!isKeepAlive(response)) {
//                    log.info("Closing the connection");
//                    e.getChannel().close();
//                }
                log.info("Closing the connection");
                e.getChannel().close();
            } else {
                readingChunks = true;
            }
        } else {
            HttpChunk chunk = (HttpChunk) e.getMessage();
//            if (chunk.isLast() && !isKeepAlive(response)) {
//                log.info("Closing the connection");
//                e.getChannel().close();
//            }

            if (chunk.isLast()) {
                log.info("Closing the connection");
                e.getChannel().close();
            }
        }
    }

    @Override
    public void channelInterestChanged(ChannelHandlerContext ctx,
                                       ChannelStateEvent e) throws Exception {
        // If outboundChannel is not saturated anymore, continue accepting
        // the incoming traffic from the inboundChannel.
        MessageContext context = (MessageContext) ctx.getChannel().getAttachment();
        if (context != null) {
            Channel inboundChannel = context.getInChannel();
            if (inboundChannel != null && e.getChannel().isWritable()) {
                //log.info("inbound readable true");
                inboundChannel.setReadable(true);
            }
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        log.error("Exception occured:", e.getCause());
        e.getChannel().close();
    }
}

