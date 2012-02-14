package cgl.iotcloud.streaming.http.server;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPipelineFactory;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.logging.InternalLogLevel;
import org.slf4j.Logger;

import static io.netty.channel.Channels.pipeline;

public class ClientPipelineFactory implements ChannelPipelineFactory {
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();

        Logger log = org.slf4j.LoggerFactory.getLogger("cgl.iotcloud.streaming.http.server.outbound.logger");
        if (log.isDebugEnabled()) {
            pipeline.addLast("log", new LoggingHandler(InternalLogLevel.INFO));
        }
        //pipeline.addLast("log", new LoggingHandler(InternalLogLevel.INFO));

//        if (ssl) {
//            SSLEngine engine =
//                SecureChatSslContextFactory.getClientContext().createSSLEngine();
//            engine.setUseClientMode(true);
//
//            pipeline.addLast("ssl", new SslHandler(engine));
//        }

        pipeline.addLast("codec", new HttpClientCodec());

        // Remove the following line if you don't want automatic content decompression.
        pipeline.addLast("inflater", new HttpContentDecompressor());

        // to be used since huge file transfer
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

        // Uncomment the following line if you don't want to handle HttpChunks.
        //pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));

        pipeline.addLast("handler", new OutboundHandler());
        return pipeline;
    }
}
