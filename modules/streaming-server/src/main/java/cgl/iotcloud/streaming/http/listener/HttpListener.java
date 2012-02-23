package cgl.iotcloud.streaming.http.listener;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.nio.NioServerSocketChannelFactory;
import io.netty.example.http.upload.HttpUploadServerPipelineFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpListener {
    private final int port;

    public HttpListener(int port) {
        this.port = port;
    }

    public void start() {
        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new HttpUploadServerPipelineFactory());

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(port));
    }
}
