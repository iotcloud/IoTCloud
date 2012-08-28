package cgl.iotcloud.core.client;

import cgl.iotcloud.core.Endpoint;

/**
 * A Client is an entity that listens to a specific sensor.
 */
public class SCClient {
    /** Type of client */
    private String type;
    /** Id of the client */
    private String id;
    /** Data endpoint which is listens to */
    private Endpoint dataEndpoint;
    /** Control endpoint it uses */
    private Endpoint controlEndpoint;
    /** Update endpoint it uses */
    private Endpoint updateEndpoint;
    
    private Endpoint publicEndpoint;

    public SCClient(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
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

    public Endpoint getControlEndpoint() {
        return controlEndpoint;
    }

    public Endpoint getUpdateEndpoint() {
        return updateEndpoint;
    }

    public void setDataEndpoint(Endpoint dataEndpoint) {
        this.dataEndpoint = dataEndpoint;
    }

    public void setControlEndpoint(Endpoint controlEndpoint) {
        this.controlEndpoint = controlEndpoint;
    }

    public void setUpdateEndpoint(Endpoint updateEndpoint) {
        this.updateEndpoint = updateEndpoint;
    }
    
    public Endpoint getPublicEndpoint() {
		return publicEndpoint;
	}

	public void setPublicEndpoint(Endpoint publicEndpoint) {
		this.publicEndpoint = publicEndpoint;
	}
}
