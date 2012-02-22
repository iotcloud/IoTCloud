package cgl.iotcloud.streaming.http.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ResponseWriter {
    private Logger log = LoggerFactory.getLogger(ResponseWriter.class);
    private Channel channel = null;

    private boolean close = false;

    private boolean chunked = false;

    public ResponseWriter(Channel channel, boolean close, boolean chunked) {
        this.channel = channel;
        this.close = close;
        this.chunked = chunked;
    }

    public void write() throws Exception {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, ACCEPTED);
        response.setHeader(CONTENT_LENGTH, 0);
        ChannelFuture future = channel.write(response);

        if (close) {
            log.info("Adding closing listener");
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
