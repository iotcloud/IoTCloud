package cgl.iotcloud.streaming.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.*;

public class HttpServerEndpoint extends HttpEndpoint {
    private Logger log = LoggerFactory.getLogger(HttpServerEndpoint.class);

    private ServerConfiguration configuration;

    private final ChannelGroup allChannels =
            new DefaultChannelGroup("http-server-" + path + ":" + port);

    private NioServerSocketChannelFactory socketFactory;

    public HttpServerEndpoint(ServerConfiguration configuration, String path, int port) {
        this(configuration, path, port, false);
    }

    public HttpServerEndpoint(ServerConfiguration configuration, String path, int port, boolean SSL) {
        super(path, port, SSL);
        this.configuration = configuration;
    }

    public void start() {
        log.info("Starting server endpoint on path: " + path + " and port: " + port);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 100, 10,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                new CustomThreadFactory(new ThreadGroup("io"), "server-io-thread"));
        socketFactory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(
                        new CustomThreadFactory(new ThreadGroup("boss"), "server-boss-thread")),
                executor);
        ServerBootstrap sb = new ServerBootstrap(socketFactory);

        sb.setPipelineFactory(new ServerPipelineFactory(configuration));

        Channel channel = sb.bind(new InetSocketAddress(port));
        allChannels.add(channel);
    }

    public void stop() {
        ChannelGroupFuture future = allChannels.close();
        future.awaitUninterruptibly();

        socketFactory.releaseExternalResources();
    }
}
