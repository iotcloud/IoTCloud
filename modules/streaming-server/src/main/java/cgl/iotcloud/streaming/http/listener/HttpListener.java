package cgl.iotcloud.streaming.http.listener;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpListener {
    private final int port;

    private MessageReceiver receiver;

    public HttpListener(int port, MessageReceiver receiver) {
        this.port = port;
        this.receiver = receiver;
    }

    public void start() {
        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new HttpListenerPipelineFactory(receiver));

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(port));
    }
}
