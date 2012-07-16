package cgl.iotcloud.core.message.update;

import cgl.iotcloud.core.message.SensorMessage;

import java.util.HashMap;
import java.util.Map;

public class UpdateMessage implements SensorMessage {
    private String sensorId;

    private Map<String, String> updates = new HashMap<String, String>();

    public UpdateMessage(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getId() {
        return sensorId;
    }

    public void addUpdate(String name, String value) {
        updates.put(name, value);
    }

    public void addUpdates(Map<String, String> updates) {
        this.updates.putAll(updates);
    }

    public String getUpdate(String name) {
        return updates.get(name);
    }

    public Map<String, String> getAllUpdates() {
        return updates;
    }

    @Override
    public void setId(String id) {
        this.sensorId = id;
    }
}
