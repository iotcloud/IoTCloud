package cgl.iotcloud.streaming.http.server;

import io.netty.buffer.ChannelBuffers;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelStateEvent;
import io.netty.channel.ExceptionEvent;
import io.netty.channel.MessageEvent;
import io.netty.channel.SimpleChannelUpstreamHandler;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;

public class InboundHandler extends SimpleChannelUpstreamHandler {
    private static Logger log = LoggerFactory.getLogger(InboundHandler.class);

    private ServerConfiguration configuration;

    public InboundHandler(
            ServerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelConnected(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e)
            throws Exception {
        Object message = e.getMessage();
        if (message instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) e.getMessage();
            String url = request.getUri();

            RoutingRule rule = null;
            for (RoutingRule r : configuration.getRoutingRules()) {
                if (r.isMatch(url)) {
                    rule = r;
                }
            }

            if (rule != null) {
                MessageContext context = new MessageContext(request, e.getChannel(), rule);
                Worker worker = new Worker(context);
//                ctx.getChannel().setAttachment(context);
//                HttpClientEndpoint endpoint = rule.getEndpoint();
//
//                endpoint.connect(context);
                configuration.getWorkerExecutor().execute(worker);
            } else {
                // close the connection
            }
        } else {
            MessageContext context = (MessageContext) ctx.getChannel().getAttachment();
            context.getOutChannel().write(e.getMessage());
        }
    }

    @Override
    public void channelInterestChanged(ChannelHandlerContext ctx,
            ChannelStateEvent e) throws Exception {
        MessageContext context = (MessageContext) ctx.getChannel().getAttachment();
        if (context != null) {
            Lock lock = context.getLock();
            // If inboundChannel is not saturated anymore, continue accepting
            // the incoming traffic from the outboundChannel.
//            lock.lock();
            try {
                context.getOutChannel().setReadable(true);
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
//
//        }
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
//            throws Exception {
//        log.info("Exception occurred: " + e);
//        closeOnFlush(e.getChannel());
//    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     * @param ch channel
     */
    static void closeOnFlush(Channel ch) {
        if (ch.isConnected()) {
            ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
