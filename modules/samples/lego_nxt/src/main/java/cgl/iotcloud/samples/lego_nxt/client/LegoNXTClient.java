package cgl.iotcloud.samples.lego_nxt.client;

import cgl.iotcloud.clients.SensorClient;
import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.message.ControlMessage;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.control.DefaultControlMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.message.data.MapDataMessage;
import cgl.iotcloud.core.message.update.UpdateMessage;
import cgl.iotcloud.samples.lego_nxt.sensor.Velocity;

public class LegoNXTClient {
    private SensorClient sensorClient;

    public void start() {
        sensorClient = new SensorClient("http://localhost:8080/");

        sensorClient.fixOnSensorWithName("lego-nxt-sensor");

        sensorClient.setUpdateHandler(new MessageHandler() {
            public void onMessage(SensorMessage message) {
                if (message instanceof UpdateMessage) {
                    UpdateMessage updateMessage = (UpdateMessage) message;
                    if (updateMessage.getUpdate(Constants.Updates.STATUS) != null &&
                            updateMessage.getUpdate(Constants.Updates.STATUS).equals(Constants.Updates.REMOVED)) {

                    }
                }
            }
        });

        sensorClient.listen(new MessageHandler() {
            public void onMessage(SensorMessage message) {
                if (message instanceof TextDataMessage) {
                    System.out.println("Message Received: " + ((TextDataMessage) message).getText());
                }
                
                if(message instanceof MapDataMessage){
                	System.out.println("touch sensor contact "+((MapDataMessage)message).get("contact"));
                	LegoNXTUI.getInstance().updateUI("contact :"+((MapDataMessage)message).get("contact"));
                }
            }
        }, new MessageHandler() {
                                @Override
                                public void onMessage(SensorMessage message) {

                                }
                            });
    }

    public void setVelocity(Velocity linear, Velocity angular) {
        DefaultControlMessage controlMessage = new DefaultControlMessage();

        controlMessage.addControl("l.x", linear.getX());
        controlMessage.addControl("l.y", linear.getY());
        controlMessage.addControl("l.z", linear.getZ());

        controlMessage.addControl("a.x", angular.getX());
        controlMessage.addControl("a.y", angular.getY());
        controlMessage.addControl("a.z", angular.getZ());

        sensorClient.sendControlMessage(controlMessage);
    }
}