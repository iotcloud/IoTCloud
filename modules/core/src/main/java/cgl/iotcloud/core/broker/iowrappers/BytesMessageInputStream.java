package cgl.iotcloud.core.broker.iowrappers;


import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MessageEOFException;
import java.io.InputStream;

/**
 * Input stream that reads data from a JMS {@link javax.jms.BytesMessage}.
 * Note that since the current position in the message is managed by
 * the underlying {@link javax.jms.BytesMessage} object, it is not possible to
 * use several instances of this class operating on a single
 * {@link javax.jms.BytesMessage} at the same time.
 */
public class BytesMessageInputStream extends InputStream {
    private final BytesMessage message;

    public BytesMessageInputStream(BytesMessage message) {
        this.message = message;
    }

    @Override
    public int read() throws JMSExceptionWrapper {
        try {
            return message.readByte() & 0xFF;
        } catch (MessageEOFException ex) {
            return -1;
        } catch (JMSException ex) {
            throw new JMSExceptionWrapper(ex);
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws JMSExceptionWrapper {
        if (off == 0) {
            try {
                return message.readBytes(b, len);
            } catch (JMSException ex) {
                throw new JMSExceptionWrapper(ex);
            }
        } else {
            byte[] b2 = new byte[len];
            int c = read(b2);
            if (c > 0) {
                System.arraycopy(b2, 0, b, off, c);
            }
            return c;
        }
    }

    @Override
    public int read(byte[] b) throws JMSExceptionWrapper {
        try {
            return message.readBytes(b);
        } catch (JMSException ex) {
            throw new JMSExceptionWrapper(ex);
        }
    }
}

