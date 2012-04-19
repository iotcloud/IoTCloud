package cgl.iotcloud.core;

import cgl.iotcloud.core.client.SCClient;

import java.util.ArrayList;
import java.util.List;

public class ClientCatalog {
    private List<SCClient> clients = new ArrayList<SCClient>();

    public List<SCClient> getClients() {
        return clients;
    }

    public SCClient getClient(String id) {
        for (SCClient s : clients) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    public void addClient(SCClient sensor) {
        if (sensor.getId() == null) {
            throw new IllegalArgumentException("The sensor should have an ID");
        }

        clients.add(sensor);
    }

    public boolean removeClient(SCClient sensor) {
        return clients.remove(sensor);
    }

    public boolean removeClient(String id) {
        for (SCClient s : clients) {
            if (s.getId().equals(id)) {
                return clients.remove(s);
            }
        }
        return false;
    }

    public boolean hasClient(String id) {
        for (SCClient s : clients) {
            if (s.getId().endsWith(id)) {
                return true;
            }
        }
        return false;
    }
}
