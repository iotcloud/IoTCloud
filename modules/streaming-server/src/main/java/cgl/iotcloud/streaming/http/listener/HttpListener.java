package cgl.iotcloud.streaming.http.listener;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpListener {
    private final int port;

    private MessageReceiver receiver;

    private String path;

    private ChannelGroup allChannels = null;

    private NioServerSocketChannelFactory channelFactory = null;

    public HttpListener(int port, MessageReceiver receiver) {
        this.port = port;
        this.receiver = receiver;
        allChannels = new DefaultChannelGroup("http-server-" + path + ":" + port);
    }

    public void start() {
        NioServerSocketChannelFactory channelFactory = new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool());
        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(channelFactory);

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new HttpListenerPipelineFactory(receiver));

        // Bind and start to accept incoming connections.
        Channel channel = bootstrap.bind(new InetSocketAddress(port));
        allChannels.add(channel);
    }

    public void stop() {
        ChannelGroupFuture future = allChannels.close();
        future.awaitUninterruptibly();

        channelFactory.releaseExternalResources();
    }
}
