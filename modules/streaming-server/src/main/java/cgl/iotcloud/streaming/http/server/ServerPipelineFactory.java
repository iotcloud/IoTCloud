package cgl.iotcloud.streaming.http.server;

import static io.netty.channel.Channels.*;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPipelineFactory;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.logging.InternalLogLevel;
import org.slf4j.Logger;


public class ServerPipelineFactory implements ChannelPipelineFactory {
    private ServerConfiguration configuration;

    public ServerPipelineFactory(
            ServerConfiguration configuration) {
        this.configuration = configuration;
    }

    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        Logger log = org.slf4j.LoggerFactory.getLogger("cgl.iotcloud.streaming.http.server.inbound.logger");
        if (log.isDebugEnabled()) {
            pipeline.addLast("log", new LoggingHandler(InternalLogLevel.INFO));
        }

        // Uncomment the following line if you want HTTPS
        //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
        //engine.setUseClientMode(false);
        //pipeline.addLast("ssl", new SslHandler(engine));

        pipeline.addLast("decoder", new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        //pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        pipeline.addLast("deflater", new HttpContentCompressor());
        pipeline.addLast("handler", new InboundHandler(configuration));
        return pipeline;
    }
}
