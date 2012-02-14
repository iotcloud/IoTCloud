package cgl.iotcloud.core.message.control;

import cgl.iotcloud.core.message.ControlMessage;

import java.util.HashMap;
import java.util.Map;

public class DefaultControlMessage implements ControlMessage {
    private String id = null;

    private Map<String, String> controls = new HashMap<String, String>();

    public DefaultControlMessage(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addControl(String key, String value) {
        controls.put(key, value);
    }

    public Map<String, String> getControls() {
        return controls;
    }
}
