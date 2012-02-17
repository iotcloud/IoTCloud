package cgl.iotcloud.streaming.http.server;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;

public class MessageContext {
    private RoutingRule rule = null;

    private HttpRequest request = null;

    private Channel inChannel = null;

    private Channel outChannel = null;

    public MessageContext(HttpRequest request, Channel channel, RoutingRule rule) {
        this.request = request;
        this.rule = rule;
        this.inChannel = channel;
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

    public Channel getOutChannel() {
        return outChannel;
    }

    public void setOutChannel(Channel outChannel) {
        this.outChannel = outChannel;
    }
}
