package cgl.iotcloud.core.broker.iowrappers;

import javax.jms.JMSException;
import java.io.IOException;

public class JMSExceptionWrapper extends IOException {
    private static final long serialVersionUID = 852441109009079511L;

    public JMSExceptionWrapper(JMSException ex) {
        initCause(ex);
    }
}

