package cgl.iotcloud.core.message;

import cgl.iotcloud.core.Constants;

public class MessageContext {
    private SensorMessage message = null;

    private String messageType = Constants.MESSAGE_TYPE_TEXT;

    public SensorMessage getMessage() {
        return message;
    }

    public void setMessage(SensorMessage message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
