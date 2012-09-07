package cgl.iotcloud.core;

import cgl.iotcloud.core.sensor.NodeInformation;
import cgl.iotcloud.core.sensor.NodeName;

import java.util.ArrayList;
import java.util.List;

public class NodeCatalog {
    private List<NodeInformation> nodes = new ArrayList<NodeInformation>();

    public List<NodeInformation> getNodes() {
        return nodes;
    }

    public NodeInformation getNode(NodeName id) {
        for (NodeInformation s : nodes) {
            if (s.getName().equals(id)) {
                return s;
            }
        }
        return null;
    }

    public void addNode(NodeInformation node) {
        if (node.getName() == null) {
            throw new IllegalArgumentException("The sensor should have an ID");
        }

        nodes.add(node);
    }

    public boolean removeNode(NodeInformation node) {
        return nodes.remove(node);
    }

    public boolean removeNode(NodeName id) {
        for (NodeInformation s : nodes) {
            if (s.getName().equals(id)) {
                return nodes.remove(s);
            }
        }
        return false;
    }

    public boolean hasNode(NodeName id) {
        for (NodeInformation s : nodes) {
            if (s.getName().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
