package cgl.iotcloud.streaming.http.server;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPipelineFactory;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.logging.InternalLogLevel;
import org.slf4j.Logger;

import static io.netty.channel.Channels.pipeline;

/**
 * Sets up the pipeline for the client part of the server.
 */
public class ClientPipelineFactory implements ChannelPipelineFactory {
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();

        Logger log = org.slf4j.LoggerFactory.getLogger("cgl.iotcloud.streaming.http.server.outbound.logger");
        if (log.isDebugEnabled()) {
            pipeline.addLast("log", new LoggingHandler(InternalLogLevel.INFO));
        }
//        pipeline.addLast("encoder", new HttpRequestEncoder());
//        pipeline.addLast("decoder",
//            new HttpResponseDecoder(8192, 8192*2, 8192*2));

        pipeline.addLast("codec", new HttpClientCodec());
        // Remove the following line if you don't want automatic content decompression.
        // pipeline.addLast("inflater", new HttpContentDecompressor());

        // to be used since huge file transfer
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

        // Uncomment the following line if you don't want to handle HttpChunks.
        // pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));

        pipeline.addLast("handler", new OutboundHandler());
        return pipeline;
    }
}
