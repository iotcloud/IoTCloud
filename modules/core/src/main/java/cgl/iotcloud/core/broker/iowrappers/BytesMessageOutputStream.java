package cgl.iotcloud.core.broker.iowrappers;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.io.OutputStream;

public class BytesMessageOutputStream extends OutputStream {
    private final BytesMessage message;

    public BytesMessageOutputStream(BytesMessage message) {
        this.message = message;
    }

    @Override
    public void write(int b) throws JMSExceptionWrapper {
        try {
            message.writeByte((byte)b);
        } catch (JMSException ex) {
            throw new JMSExceptionWrapper(ex);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws JMSExceptionWrapper {
        try {
            message.writeBytes(b, off, len);
        } catch (JMSException ex) {
            throw new JMSExceptionWrapper(ex);
        }
    }

    @Override
    public void write(byte[] b) throws JMSExceptionWrapper {
        try {
            message.writeBytes(b);
        } catch (JMSException ex) {
            throw new JMSExceptionWrapper(ex);
        }
    }
}

