package cgl.iotcloud.core.sensor;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.Endpoint;
import cgl.iotcloud.core.ManagedLifeCycle;
import cgl.iotcloud.core.broker.Sender;
import cgl.iotcloud.core.broker.SenderFactory;
import cgl.iotcloud.core.message.SensorMessage;

/**
 * This is a dummy sensor to represent actual sensors in the SC.
 * We will use this class to represent an actual sensor in the IoTCloud.
 */
public class SCSensor implements Sensor {
    private String name = null;

    private String id = null;

    private String type = Constants.MESSAGE_TYPE_TEXT;

    private Endpoint dataEndpoint;

    private Endpoint controlEndpoint;

    private Endpoint updateListeningEndpoint;

    private Endpoint updateSendingEndpoint;

    public SCSensor(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Endpoint getDataEndpoint() {
        return dataEndpoint;
    }

    public void setDataEndpoint(Endpoint endpoint) {
        this.dataEndpoint = endpoint;
    }

    public Endpoint getControlEndpoint() {
        return controlEndpoint;
    }

    public void setControlEndpoint(Endpoint endpoint) {
        this.controlEndpoint = endpoint;
    }

    public Endpoint getUpdateEndpoint() {
        return updateListeningEndpoint;
    }

    public void setUpdateEndpoint(Endpoint endpoint) {
        this.updateListeningEndpoint = endpoint;
    }
}
