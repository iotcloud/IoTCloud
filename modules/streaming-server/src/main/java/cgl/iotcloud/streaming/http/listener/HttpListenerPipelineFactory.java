package cgl.iotcloud.streaming.http.listener;


import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPipelineFactory;
import io.netty.example.http.file.HttpStaticFileServerHandler;
import io.netty.handler.codec.http.HttpChunkAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import static io.netty.channel.Channels.pipeline;

public class HttpListenerPipelineFactory implements ChannelPipelineFactory {
    private MessageReceiver receiver;

    private String path;

    public HttpListenerPipelineFactory(MessageReceiver receiver, String path) {
        this.receiver = receiver;
        this.path = path;
    }

    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        // Uncomment the following line if you want HTTPS
        //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
        //engine.setUseClientMode(false);
        //pipeline.addLast("ssl", new SslHandler(engine));

        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

        // for now we use a chunk aggregator . we have to fix this
        pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));

        pipeline.addLast("handler", new HttpListenerHandler(receiver, path));
        return pipeline;
    }
}
