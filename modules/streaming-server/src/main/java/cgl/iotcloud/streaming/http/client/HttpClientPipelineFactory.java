package cgl.iotcloud.streaming.http.client;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPipelineFactory;
import io.netty.example.http.snoop.HttpSnoopClientHandler;
import io.netty.example.securechat.SecureChatSslContextFactory;
import io.netty.handler.codec.http.HttpChunkAggregator;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.logging.InternalLogLevel;

import javax.net.ssl.SSLEngine;

import static io.netty.channel.Channels.pipeline;

public class HttpClientPipelineFactory implements ChannelPipelineFactory {

    private final boolean ssl;

    public HttpClientPipelineFactory(boolean ssl) {
        this.ssl = ssl;
    }

    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        pipeline.addLast("log", new LoggingHandler(InternalLogLevel.INFO));
        // Enable HTTPS if necessary.
        if (ssl) {
            SSLEngine engine =
                    SecureChatSslContextFactory.getClientContext().createSSLEngine();
            engine.setUseClientMode(true);

            pipeline.addLast("ssl", new SslHandler(engine));
        }

        pipeline.addLast("codec", new HttpClientCodec());

        // Remove the following line if you don't want automatic content decompression.
        pipeline.addLast("inflater", new HttpContentDecompressor());

        // Uncomment the following line if you don't want to handle HttpChunks.
        pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));

        pipeline.addLast("handler", new HttpSnoopClientHandler());
        return pipeline;
    }
}
