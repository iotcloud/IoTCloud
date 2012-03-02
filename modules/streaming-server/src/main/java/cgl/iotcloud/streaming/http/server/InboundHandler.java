package cgl.iotcloud.streaming.http.server;

import io.netty.bootstrap.ClientBootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.HttpChunk;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.UUID;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;


public class InboundHandler extends SimpleChannelUpstreamHandler {
    private static Logger log = LoggerFactory.getLogger(InboundHandler.class);
    private ServerConfiguration configuration;

    private boolean readingChunks = false;

    private HttpRequest request = null;

    private String messageId = null;

    private RoutingRule rule = null;
    
    public InboundHandler(
            ServerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e)
            throws Exception {
        Object message = e.getMessage();
        if (!readingChunks) {
            request = (HttpRequest) e.getMessage();
            String url = request.getUri();

            for (RoutingRule r : configuration.getRoutingRules()) {
                if (r.isMatch(url)) {
                    rule = r;
                }
            }

            readingChunks = request.isChunked();

            if (rule != null) {
                // e.getChannel().setReadable(false);
                MessageContext context = new MessageContext(request, e.getChannel(), rule);
                //Worker worker = new Worker(context);
                //configuration.getWorkerExecutor().execute(worker);
                messageId = UUID.randomUUID().toString();
                rule.getEndpoint().writeRequest(request, messageId);

                if (!readingChunks) {
                    new ResponseWriter(e.getChannel(), !isKeepAlive(request), request.isChunked()).write();
                }
            } else {
                // close the connection
            }
        } else {
            //MessageContext context = (MessageContext) ctx.getChannel().getAttachment();
            //ChannelFuture future = context.getOutChannel().write(e.getMessage());
            HttpChunk chunk = (HttpChunk) e.getMessage();
            // when the write operation is completed we have to write the 202
            rule.getEndpoint().writeChunk(chunk, messageId);

            if (chunk.isLast()) {
                new ResponseWriter(e.getChannel(), !isKeepAlive(request), request.isChunked()).write();
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
        if (e.getChannel().isConnected()) {
            e.getChannel().close();
        }
    }

    private ChannelFuture newChannelFuture(final HttpRequest httpRequest,
                                           final Channel browserToProxyChannel, String host, int port) {
        if (port == -1) {
            port = 80;
        }

        ClientBootstrap cb = new ClientBootstrap(configuration.getClientSocketChannelFactory());

        cb.setPipelineFactory(new ClientPipelineFactory());
        cb.setOption("connectTimeoutMillis", 60 * 1000);
        log.info("Starting new connection to: {}", host + ":" + port);
        final ChannelFuture future =
                cb.connect(new InetSocketAddress(host, port));
        return future;
    }
}
