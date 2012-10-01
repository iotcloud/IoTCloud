package cgl.iotcloud.core;

import cgl.iotcloud.core.message.MessageHandler;

public interface Listener extends Control, ManagedLifeCycle {
    public void setMessageHandler(MessageHandler handler);
}
