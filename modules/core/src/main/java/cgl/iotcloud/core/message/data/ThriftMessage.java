package cgl.iotcloud.core.message.data;

import org.apache.thrift.TBase;

public class ThriftMessage {
    private TBase message;

    public ThriftMessage(TBase message) {
        this.message = message;
    }

    public TBase getMessage() {
        return message;
    }
}
