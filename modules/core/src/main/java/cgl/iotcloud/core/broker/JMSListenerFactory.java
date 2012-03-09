package cgl.iotcloud.core.broker;

import cgl.iotcloud.core.Endpoint;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.jms.JMSControlMessageFactory;

public class JMSListenerFactory {
    /**
     * Create a Listener using the given endpoint
     *
     * @param endpoint endpoint to be used for creating the listener
     * @param handler the message handler
     * @return a Listener
     */
    public JMSListener create(Endpoint endpoint, MessageHandler handler) {
        ConnectionsFactory fac = new ConnectionsFactory();
        Connections connections = fac.create(endpoint.getName(), endpoint.getProperties());

        return new JMSListener(connections, handler, endpoint.getAddress());
    }

    public JMSListener createControlListener(Endpoint endpoint, MessageHandler handler) {
        ConnectionsFactory fac = new ConnectionsFactory();
        Connections connections = fac.create(endpoint.getName(), endpoint.getProperties());

        JMSListener listener = new JMSListener(connections, handler, endpoint.getAddress());
        listener.setMessageFactory(new JMSControlMessageFactory());
        return listener;
    }
}
