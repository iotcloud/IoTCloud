package cgl.iotcloud.streaming.http.server;

import io.netty.buffer.ChannelBuffers;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelStateEvent;
import io.netty.channel.ExceptionEvent;
import io.netty.channel.MessageEvent;
import io.netty.channel.SimpleChannelUpstreamHandler;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;

public class OutboundHandler extends SimpleChannelUpstreamHandler {
    private Logger log = LoggerFactory.getLogger(OutboundHandler.class);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e)
            throws Exception {
        MessageContext context = (MessageContext) ctx.getChannel().getAttachment();
        if (context != null) {
            Channel inboundChannel = context.getInChannel();
            Object msg = e.getMessage();
//            Lock lock = context.getLock();
//            lock.lock();
            try {
                inboundChannel.write(msg);
                // If inboundChannel is saturated, do not read until notified in
                if (!inboundChannel.isWritable()) {
                    e.getChannel().setReadable(false);
                }
            } finally {
                //lock.unlock();
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
//            Lock lock = context.getLock();
//            lock.lock();
            try {
                if (e.getChannel().isWritable()) {
                    inboundChannel.setReadable(true);
                }
            } finally {
//                lock.unlock();
            }
        }
    }

//    @Override
//    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
//            throws Exception {
//        MessageContext context = (MessageContext) ctx.getChannel().getAttachment();
//        if (context != null) {
//            Channel inboundChannel = context.getInChannel();
//            closeOnFlush(inboundChannel);
//        }
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
//            throws Exception {
//        log.info("Error encountered", e);
//        closeOnFlush(e.getChannel());
//    }

    static void closeOnFlush(Channel ch) {
        if (ch.isConnected()) {
            ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}

