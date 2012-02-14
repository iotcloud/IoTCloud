package cgl.iotcloud.core.message.data;

import cgl.iotcloud.core.message.DataMessage;

import java.util.UUID;

/**
 * A simple data message with a fixed length string
 */
public class TextDataMessage implements DataMessage {
    private String text = null;

    private String id = UUID.randomUUID().toString();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
