package cgl.iotcloud.services;

/**
 * Client information as a bean
 */
public class ClientInformation {
    /** id of the client */
    private String id;
    /** type of the client */
    private String type;
    /** name of the client */
    private String name;
    /** control endpoint */
    private Endpoint controlEndpoint;
    /** data endpoint */
    private Endpoint dataEndpoint;
    /** update endpoint */
    private Endpoint updateEndpoint;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Endpoint getControlEndpoint() {
        return controlEndpoint;
    }

    public Endpoint getDataEndpoint() {
        return dataEndpoint;
    }

    public void setControlEndpoint(Endpoint controlEndpoint) {
        this.controlEndpoint = controlEndpoint;
    }

    public void setDataEndpoint(Endpoint dataEndpoint) {
        this.dataEndpoint = dataEndpoint;
    }

    public Endpoint getUpdateEndpoint() {
        return updateEndpoint;
    }

    public void setUpdateEndpoint(Endpoint updateEndpoint) {
        this.updateEndpoint = updateEndpoint;
    }
}
