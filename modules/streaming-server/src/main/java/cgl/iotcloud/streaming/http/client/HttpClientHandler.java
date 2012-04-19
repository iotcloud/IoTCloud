package cgl.iotcloud.streaming.http.client;

import io.netty.buffer.ChannelBuffer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageEvent;
import io.netty.channel.SimpleChannelUpstreamHandler;
import io.netty.handler.codec.http.HttpChunk;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * We don't expect a response. So we don't do anything.
 */
public class HttpClientHandler extends SimpleChannelUpstreamHandler {
    private static Logger log = LoggerFactory.getLogger(HttpClientHandler.class);

    private boolean readingChunks;

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (!readingChunks) {
            HttpResponse response = (HttpResponse) e.getMessage();

            log.debug("STATUS: " + response.getStatus());
            log.debug("VERSION: " + response.getProtocolVersion());

            if (!response.getHeaderNames().isEmpty()) {
                for (String name: response.getHeaderNames()) {
                    for (String value: response.getHeaders(name)) {
                        log.debug("HEADER: " + name + " = " + value);
                    }
                }

            }

            if (response.isChunked()) {
                readingChunks = true;
                log.debug("CHUNKED CONTENT {");
            } else {
                ChannelBuffer content = response.getContent();
                if (content.readable()) {
                    log.debug("CONTENT {");
                    log.debug(content.toString(CharsetUtil.UTF_8));
                    log.debug("} END OF CONTENT");
                }
            }
        } else {
            HttpChunk chunk = (HttpChunk) e.getMessage();
            if (chunk.isLast()) {
                readingChunks = false;
                log.debug("} END OF CHUNKED CONTENT");
            } else {
                log.debug(chunk.getContent().toString(CharsetUtil.UTF_8));
            }
        }
    }
}
