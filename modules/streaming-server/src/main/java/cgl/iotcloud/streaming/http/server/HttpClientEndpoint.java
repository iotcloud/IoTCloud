package cgl.iotcloud.streaming.http.server;

import io.netty.bootstrap.ClientBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpChunk;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.*;

public class HttpClientEndpoint extends HttpEndpoint {
    private static Logger log = LoggerFactory.getLogger(HttpClientEndpoint.class);

    private String host;
    
    private ServerConfiguration configuration;

    private final Queue<ChannelFuture> openChannels = new LinkedList<ChannelFuture>();
    
    private final Map<String, ChannelFuture> workingChannels = new HashMap<String, ChannelFuture>(); 
    
    private ChannelGroup channelGroup = null;

    public HttpClientEndpoint(ServerConfiguration configuration, 
                              String path, String host, int port) {
        this(configuration, path, host, port, false);
    }

    public HttpClientEndpoint(ServerConfiguration configuration, String path, 
                              String host, int port, boolean SSL) {
        super(path, port, SSL);
        this.configuration = configuration;
        this.host = host;
    }

    public ChannelFuture connect(final MessageContext context) {
        ClientBootstrap cb = configuration.getClientBootStrap();

//        ChannelFuture f = cb.connect(new InetSocketAddress(host, port));
//
//        final Channel outboundChannel = f.getChannel();
//        context.setOutChannel(outboundChannel);
//        f.addListener(new ChannelFutureListener() {
//            public void operationComplete(ChannelFuture future) throws Exception {
//                if (future.isSuccess()) {
//                    // Connection attempt succeeded:
//                    // Begin to accept incoming traffic.
//                    context.getInChannel().setReadable(true);
//                    context.getOutChannel().write(context.getRequest());
//
//                    // If outboundChannel is saturated, do not read until notified in
//                    // OutboundHandler.channelInterestChanged().
//                    if (!context.getOutChannel().isWritable()) {
//                        // log.info("inbound readable false");
//                        context.getInChannel().setReadable(false);
//                    }
//                    outboundChannel.setAttachment(context);
//                } else {
//                    // Close the connection if the connection attempt has failed.
//                    // context.getInChannel().close();
//                }
//            }
//        });

        if (port == -1) {
            port = 80;
        }

        // ClientBootstrap cb = new ClientBootstrap(configuration.getClientSocketChannelFactory());

        cb.setPipelineFactory(new ClientPipelineFactory());
        cb.setOption("connectTimeoutMillis", 60 * 1000);
        log.info("Starting new connection to: {}", host + ":" + port);
        final ChannelFuture future =
                cb.connect(new InetSocketAddress(host, port));
        return future;
    }

    public void writeRequest(final HttpRequest request, String id) {
        final class OnConnect {
            public ChannelFuture onConnect(final ChannelFuture cf) {
                return cf.getChannel().write(request);
            }
        }

        final OnConnect onConnect = new OnConnect();

        ChannelFuture future = getChannelFuture();
        if (future != null) {
            log.info("Using existing connection...");
            if (future.getChannel().isConnected()) {
                onConnect.onConnect(future);
            } else {
                final ChannelFutureListener cfl = new ChannelFutureListener() {
                    public void operationComplete(final ChannelFuture future)
                            throws Exception {
                        onConnect.onConnect(future);
                    }
                };
                future.addListener(cfl);
            }
        } else {
            final ChannelFuture cf = newChannelFuture();
            final class LocalChannelFutureListener implements ChannelFutureListener {

                public void operationComplete(final ChannelFuture future)
                        throws Exception {
                    final Channel channel = future.getChannel();
                    if (channelGroup != null) {
                        channelGroup.add(channel);
                    }
                    if (future.isSuccess()) {
                        log.info("Connected successfully to: {}", channel);
                        log.info("Writing message on channel...");
                        final ChannelFuture wf = onConnect.onConnect(cf);
                        wf.addListener(new ChannelFutureListener() {
                            public void operationComplete(final ChannelFuture wcf)
                                    throws Exception {
                                log.info("Finished write: " + wcf + " to: " +
                                        request.getMethod() + " " +
                                        request.getUri());
                            }
                        });
                    } else {
                        log.info("Could not connect to " + host + ":" + port,
                                future.getCause());
                    }
                }
            }

            cf.addListener(new LocalChannelFutureListener());
        }        
    }
    
    public void writeChunk(final HttpChunk chunk, String messageId) {
        final ChannelFuture currentChannelFuture = workingChannels.remove(messageId);
        if (currentChannelFuture == null) {
            return;
        }
        // We don't necessarily know the channel is connected yet!! This can
        // happen if the client sends a chunk directly after the initial 
        // request.
        if (currentChannelFuture.getChannel().isConnected()) {
            currentChannelFuture.getChannel().write(chunk);
        } else {
            currentChannelFuture.addListener(new ChannelFutureListener() {

                public void operationComplete(final ChannelFuture future)
                        throws Exception {
                    currentChannelFuture.getChannel().write(chunk);
                }
            });
        }
    } 

    private ChannelFuture getChannelFuture() {
        synchronized (this.openChannels) {            
            if (openChannels.isEmpty()) {
                return null;
            }
            final ChannelFuture cf = openChannels.remove();

            if (cf != null && cf.isSuccess() &&
                    !cf.getChannel().isConnected()) {
                // In this case, the future successfully connected at one
                // time, but we're no longer connected. We need to remove the
                // channel and open a new one.
                openChannels.remove(cf);
                return null;
            }
            
            return cf;
        }
    }

    private ChannelFuture newChannelFuture() {
        // Configure the client.
        ClientBootstrap cb = configuration.getClientBootStrap();
        
        cb.setOption("connectTimeoutMillis", 40*1000);
        log.info("Starting new connection to: {}", host + port);
        final ChannelFuture future =
                cb.connect(new InetSocketAddress(host, port));
        future.getChannel().setAttachment(this);
        return future;
    }
    
    public void responseDone(ChannelFuture future, boolean close) {
        if (!close) {
            openChannels.offer(future);
        } else {
            if (future != null) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
}
