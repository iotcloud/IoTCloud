package cgl.iotcloud.core.message.control;

import cgl.iotcloud.core.message.ControlMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DefaultControlMessage implements ControlMessage {
    private String id = UUID.randomUUID().toString();

    private Map<String, Object> controls = new HashMap<String, Object>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addControl(String key, Object value) {
        controls.put(key, value);
    }

    public Map<String, Object> getControls() {
        return controls;
    }

    public Object getControl(String control) {
        return controls.get(control);
    }
}
