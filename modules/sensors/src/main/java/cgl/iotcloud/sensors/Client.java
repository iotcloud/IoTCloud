package cgl.iotcloud.sensors;

import cgl.iotcloud.core.Endpoint;
import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.sensor.NodeInformation;
import cgl.iotcloud.core.sensor.NodeName;

import java.util.List;

public interface Client {
    public boolean registerNode(NodeName nodeInfo) throws IOTException;

    public boolean unRegisterNode(NodeName nodeInfo) throws IOTException ;

    public Endpoint registerConsumer(NodeName nodeInfo, String name,
                                                       String type, String path) throws IOTException;

    public Endpoint registerProducer(NodeName nodeInfo, String name,
                                     String type, String path) throws IOTException;

    public boolean unRegisterProducer(NodeName nodeInfo, String name,
                                      String type, String path) throws IOTException;

    public boolean unRegisterConsumer(NodeName nodeInfo, String name,
                                      String type, String path) throws IOTException;
    public List<NodeName> getNodeList() throws IOTException;

    public NodeInformation getNode(NodeName name) throws IOTException;
}
