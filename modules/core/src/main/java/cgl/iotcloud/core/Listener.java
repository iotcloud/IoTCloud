package cgl.iotcloud.core;

import cgl.iotcloud.core.message.MessageHandler;

/**
 * Listens for incoming messages and notifies an event handler when messages arrives
 */
public interface Listener extends Control, ManagedLifeCycle {
    /**
     * Set the message handler to listen to incoming messages
     *
     * @param handler handler to listen to incoming messages
     */
    public void setMessageHandler(MessageHandler handler);
}
