package cgl.iotcloud.core.broker;

import cgl.iotcloud.core.Endpoint;
import cgl.iotcloud.core.message.jms.JMSControlMessageFactory;

public class SenderFactory {
    /**
     * Create a Sender using the Endpoint
     * @param endpoint endpoint to be used
     * @return a sender
     */
    public Sender create(Endpoint endpoint) {
        ConnectionsFactory fac = new ConnectionsFactory();
        Connections connections = fac.create(endpoint.getName(), endpoint.getProperties());

        return new Sender(connections, endpoint.getAddress());
    }

    /**
     * Create a Sender using the Endpoint
     * @param endpoint endpoint to be used
     * @return a sender
     */
    public Sender createControlSender(Endpoint endpoint) {
        ConnectionsFactory fac = new ConnectionsFactory();
        Connections connections = fac.create(endpoint.getName(), endpoint.getProperties());

        Sender sender = new Sender(connections, endpoint.getAddress());
        sender.setMessageFactory(new JMSControlMessageFactory());
        return sender;
    }
}
