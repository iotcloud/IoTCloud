package cgl.iotcloud.streaming.http.client;

import java.io.IOException;
import java.io.OutputStream;

public class HttpOutputStream extends OutputStream {
    @Override
    public void write(int i) throws IOException {

    }

    @Override
    public void write(byte[] bytes) throws IOException {
        super.write(bytes);
    }

    @Override
    public void write(byte[] bytes, int i, int i1) throws IOException {
        super.write(bytes, i, i1);
    }
}
