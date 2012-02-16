package cgl.iotcloud.streaming.http.server;

import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutboundHandler extends SimpleChannelUpstreamHandler {
    private Logger log = LoggerFactory.getLogger(OutboundHandler.class);

//    @Override
//    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e)
//            throws Exception {
//        log.info("Message received");
//        MessageContext context = (MessageContext) ctx.getChannel().getAttachment();
//        if (context != null) {
//
//        }
//    }

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
}

