package cgl.iotcloud.streaming.http.server;

import io.netty.channel.*;
import io.netty.handler.codec.http.HttpChunk;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;


public class InboundHandler extends SimpleChannelUpstreamHandler {
    private static Logger log = LoggerFactory.getLogger(InboundHandler.class);
    private ServerConfiguration configuration;

    private boolean readingChunks = false;

    private HttpRequest request = null;

    public InboundHandler(
            ServerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e)
            throws Exception {
        Object message = e.getMessage();
        if (message instanceof HttpRequest) {
            request = (HttpRequest) e.getMessage();
            String url = request.getUri();

            RoutingRule rule = null;
            for (RoutingRule r : configuration.getRoutingRules()) {
                if (r.isMatch(url)) {
                    rule = r;
                }
            }

            readingChunks = request.isChunked();

            if (rule != null) {
                e.getChannel().setReadable(false);
                MessageContext context = new MessageContext(request, e.getChannel(), rule);
                Worker worker = new Worker(context);
                configuration.getWorkerExecutor().execute(worker);
            } else {
                // close the connection
            }
        } else if (readingChunks) {
            MessageContext context = (MessageContext) ctx.getChannel().getAttachment();
            ChannelFuture future = context.getOutChannel().write(e.getMessage());
            HttpChunk chunk = (HttpChunk) e.getMessage();
            // when the write operation is completed we have to write the 202
            if (chunk.isLast()) {
                future.addListener(new ResponseWriter(e.getChannel(), isKeepAlive(request), request.isChunked(), context.getOutChannel()));
            }
        }
    }

    @Override
    public void channelInterestChanged(ChannelHandlerContext ctx,
            ChannelStateEvent e) throws Exception {
        MessageContext context = (MessageContext) ctx.getChannel().getAttachment();
        if (context != null) {
            if (e.getChannel().isWritable()) {
                // log.info("outbout readable true");
                context.getOutChannel().setReadable(true);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        log.error("Exception occured:", e.getCause());
        e.getChannel().close();
    }
}
