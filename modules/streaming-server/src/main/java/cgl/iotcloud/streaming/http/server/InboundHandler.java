package cgl.iotcloud.streaming.http.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelStateEvent;
import io.netty.channel.MessageEvent;
import io.netty.channel.SimpleChannelUpstreamHandler;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InboundHandler extends SimpleChannelUpstreamHandler {
    private static Logger log = LoggerFactory.getLogger(InboundHandler.class);
    private ServerConfiguration configuration;

    public InboundHandler(
            ServerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e)
            throws Exception {
        Object message = e.getMessage();
        log.info("Message received");
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
                e.getChannel().setReadable(false);
                MessageContext context = new MessageContext(request, e.getChannel(), rule);
                Worker worker = new Worker(context);
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
            if (e.getChannel().isWritable()) {
                log.info("outbout readable true");
                context.getOutChannel().setReadable(true);
            }
        }
    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     * @param ch channel
     */
//    static void closeOnFlush(Channel ch) {
//        if (ch.isConnected()) {
//            ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
//        }
//    }
}
