package cgl.iotcloud.streaming.http.server;

import io.netty.buffer.ChannelBuffers;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ResponseWriter implements ChannelFutureListener {
    private Channel channel = null;

    private boolean close = false;

    public ResponseWriter(Channel channel, boolean close) {
        this.channel = channel;
        this.close = close;
    }

    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, ACCEPTED);
        String buf = "\r\n\r\n";
        response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setHeader(CONNECTION, "Close");
        response.setContent(ChannelBuffers.copiedBuffer(buf, CharsetUtil.UTF_8));
        ChannelFuture future = channel.write(response);
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
