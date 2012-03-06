package cgl.iotcloud.streaming.http.client;

import io.netty.bootstrap.ClientBootstrap;
import io.netty.buffer.ChannelBufferOutputStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioClientSocketChannelFactory;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;

public class HttpClient {
    private static Logger log = LoggerFactory.getLogger(HttpClient.class);

    private final URI uri;

    public HttpClient(URI uri) {
        this.uri = uri;
    }

    public void send(ChannelBufferOutputStream outputStream) {
        String scheme = uri.getScheme() == null? "http" : uri.getScheme();
        String host = uri.getHost() == null? "localhost" : uri.getHost();
        int port = uri.getPort();

        if (port == -1) {
            if (scheme.equalsIgnoreCase("http")) {
                port = 80;
            } else if (scheme.equalsIgnoreCase("https")) {
                port = 443;
            }
        }

        if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
            log.error("Only HTTP(S) is supported.");
            return;
        }

        boolean ssl = scheme.equalsIgnoreCase("https");

        // Configure the client.
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new HttpClientPipelineFactory(ssl));

        // Start the connection attempt.
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));

        // Wait until the connection attempt succeeds or fails.
        Channel channel = future.awaitUninterruptibly().getChannel();
        if (!future.isSuccess()) {
            future.getCause().printStackTrace();
            bootstrap.releaseExternalResources();
            return;
        }

        // Prepare the HTTP request.
        HttpRequest request = new DefaultHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
        request.setHeader(HttpHeaders.Names.HOST, host);
        request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
        // request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);

        // set the content
        request.setContent(outputStream.buffer());

        // Send the HTTP request.
        channel.write(request);

        // Wait for the server to close the connection.
        channel.getCloseFuture().awaitUninterruptibly();

        // Shut down executor threads to exit.
        bootstrap.releaseExternalResources();
    }
}
