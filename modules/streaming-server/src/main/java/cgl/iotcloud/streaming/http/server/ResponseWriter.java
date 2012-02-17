package cgl.iotcloud.streaming.http.server;

import io.netty.buffer.ChannelBuffers;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Values.*;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ResponseWriter implements ChannelFutureListener {
    private Channel channel = null;

    private boolean close = false;

    private boolean chunked = false;

    private Channel outChannel = null;

    public ResponseWriter(Channel channel, boolean close, boolean chunked, Channel outChannel) {
        this.channel = channel;
        this.close = close;
        this.chunked = chunked;
        this.outChannel = channel;
    }

    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, ACCEPTED);
//        if (chunked) {
//            response.setHeader(TRANSFER_ENCODING, CHUNKED);
//        } else {
        response.setHeader(CONTENT_LENGTH, 0);
        ChannelFuture future = channel.write(response);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                outChannel.close();
                channel.close();
            }
        });

//        }

        //String buf = "\r\n\r\n";
        //response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
        //response.setHeader(CONNECTION, "Close");
        //response.setContent(ChannelBuffers.EMPTY_BUFFER);

        if (close) {
//            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
