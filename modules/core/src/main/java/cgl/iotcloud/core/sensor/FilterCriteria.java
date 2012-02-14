package cgl.iotcloud.core.sensor;

import java.util.HashMap;
import java.util.Map;

public class FilterCriteria {
    private Map<String, String> properties = new HashMap<String, String>();

    public void addProperty(String name, String value) {
        properties.put(name, value);
    }

    public void addProperties(Map<String, String> properties) {
        this.properties.putAll(properties);
    }

    public String get(String name) {
        return properties.get(name);
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
