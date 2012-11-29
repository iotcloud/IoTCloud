package cgl.iotcloud.samples.arducopter.mssg;

import cgl.iotcloud.core.message.data.ObjectDataMessage;

public class RCMessage extends ObjectDataMessage {

    private int channel[];

    public int[] getChannel() {
        return channel;
    }

    public void setChannel(int[] channel) {
        this.channel = channel;
    }
}
