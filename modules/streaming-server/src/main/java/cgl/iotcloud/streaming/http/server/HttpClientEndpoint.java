package cgl.iotcloud.streaming.http.server;

import io.netty.bootstrap.ClientBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpChunk;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HttpClientEndpoint extends HttpEndpoint {
    private static Logger log = LoggerFactory.getLogger(HttpClientEndpoint.class);

    private String host;
    
    private ServerConfiguration configuration;

    private final LinkedList<ChannelFuture> openChannels = new LinkedList<ChannelFuture>();
    
    private final Map<String, Channel> workingChannels = new HashMap<String, Channel>();

    private final Map<String, ConcurrentLinkedQueue<Object>> messageList =
            new ConcurrentHashMap<String, ConcurrentLinkedQueue<Object>>();
    
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

    public void writeRequest(final HttpRequest request, String id) {
        final class OnConnect {
            public ChannelFuture onConnect(final ChannelFuture cf) {
                return cf.getChannel().write(request);
            }
        }

        // put the message in to the queue
        ConcurrentLinkedQueue<Object> messages =
                new ConcurrentLinkedQueue<Object>();
        messageList.put(id, messages);

        if (request.isChunked()) {
            request.removeHeader(HttpHeaders.Names.CONTENT_LENGTH);
        }

        final OnConnect onConnect = new OnConnect();

        ChannelFuture future = getChannelFuture();
        if (future != null) {
            log.debug("Using existing connection...");
            if (future.getChannel().isConnected()) {
                workingChannels.put(id, future.getChannel());
                future.getChannel().write(request).addListener(new HandleEvent(id));
            } else {
                // there is messages to follow. so put the message in a the map
                messages.offer(request);
                future.addListener(new HandleEvent(id));
            }
        } else {
            messages.offer(request);

            final ChannelFuture cf = newChannelFuture();
            cf.addListener(new HandleEvent(id));
        }        
    }

    private class HandleEvent implements ChannelFutureListener {
        private String id;

        private HandleEvent(String id) {
            this.id = id;
        }

        public void operationComplete(ChannelFuture channelFuture)
                throws Exception {

            final ConcurrentLinkedQueue<Object> messages =
                    messageList.get(id);

            if (messages != null) {
                Object message = messages.poll();
                if (message != null) {
                    if (channelFuture.getChannel().isConnected()) {
                        workingChannels.put(id, channelFuture.getChannel());
                        log.info("writing the message after event completion.........");
                        ChannelFuture future = channelFuture.getChannel().write(message);

                        if ((message instanceof HttpRequest && !((HttpRequest) message).isChunked()) ||
                                (message instanceof HttpChunk && ((HttpChunk) message).isLast())) {
                            workingChannels.remove(id);
                            messageList.remove(id);
                        } else {
                            future.addListener(new HandleEvent(id));
                        }
                    } else {
                        channelFuture.addListener(new HandleEvent(id));
                    }
                }
            }
        }
    }
    
    public void writeChunk(final HttpChunk chunk, String messageId) {
        // put the message in to the queue
        final ConcurrentLinkedQueue<Object> messages =
                messageList.get(messageId);

        if (messages == null) {
            log.warn("a chunk came without a list associated it with it...");
            return;
        }

        if (messages.isEmpty()) {
            Channel channel = workingChannels.get(messageId);
            if (channel != null) {
                if (channel.isConnected()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Writing the chunk to the out channel..");
                    }
                    channel.write(chunk).addListener(new HandleEvent(messageId));
                } else {
                    log.warn("Trying to write a chunk without a connected channel:" + messageId);
                }
            } else {
                log.warn("Doesn't have a channel associated with Message id:" + messageId);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Offering the chunk to the queue..");
            }
            messages.offer(chunk);
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
        if (log.isDebugEnabled()) {
            log.debug("Starting new connection to: {}", host + port);
        }
        
        // Configure the client.
        ClientBootstrap cb = configuration.getClientBootStrap();
        
        cb.setOption("connectTimeoutMillis", 40*1000);                        
        final ChannelFuture future =
                cb.connect(new InetSocketAddress(host, port));
        future.getChannel().setAttachment(this);
        return future;
    }
    
    public void responseDone(ChannelFuture future, boolean close) {
        if (!close) {
            synchronized (openChannels) {
                openChannels.offer(future);
            }
        } else {
            if (future != null) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
}
