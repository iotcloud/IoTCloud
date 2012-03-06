package cgl.iotcloud.streaming.http.listener;

import io.netty.buffer.ChannelBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Semaphore;

public class HttpInputStream extends InputStream {
    private Semaphore count = new Semaphore(0);

    private ChannelBuffer currentBuffer = null;

    @Override
    public int read() throws IOException {
        return 0;
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        return super.read(bytes);
    }

    @Override
    public int read(byte[] bytes, int i, int i1) throws IOException {
        return super.read(bytes, i, i1);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
    }
}
