package cgl.iotcloud.streaming.http.server;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageContext {
    private boolean inReadReady;

    private boolean outReadRead;

    private boolean inWriteReady;

    private boolean outWriteReady;

    private RoutingRule rule = null;

    private Lock lock = new ReentrantLock();

    protected HttpRequest request = null;

    private Channel inChannel = null;

    private Channel outChannel = null;

    public MessageContext(HttpRequest request, Channel channel, RoutingRule rule) {
        this.request = request;
        this.rule = rule;
        this.inChannel = channel;
    }

    public Lock getLock() {
        return lock;
    }

    public boolean isInReadReady() {
        return inReadReady;
    }

    public boolean isOutReadRead() {
        return outReadRead;
    }

    public boolean isInWriteReady() {
        return inWriteReady;
    }

    public boolean isOutWriteReady() {
        return outWriteReady;
    }

    public void setInReadReady(boolean inReadReady) {
        this.inReadReady = inReadReady;
    }

    public void setOutReadRead(boolean outReadRead) {
        this.outReadRead = outReadRead;
    }

    public void setInWriteReady(boolean inWriteReady) {
        this.inWriteReady = inWriteReady;
    }

    public void setOutWriteReady(boolean outWriteReady) {
        this.outWriteReady = outWriteReady;
    }

    public RoutingRule getRule() {
        return rule;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public Channel getInChannel() {
        return inChannel;
    }

    public void setInChannel(Channel inChannel) {
        this.inChannel = inChannel;
    }

    public Channel getOutChannel() {
        return outChannel;
    }

    public void setOutChannel(Channel outChannel) {
        this.outChannel = outChannel;
    }
}
