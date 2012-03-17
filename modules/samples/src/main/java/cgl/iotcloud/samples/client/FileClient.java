package cgl.iotcloud.samples.client;

import cgl.iotcloud.clients.SensorClient;
import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.control.DefaultControlMessage;
import cgl.iotcloud.core.message.data.StreamDataMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.message.update.UpdateMessage;
import cgl.iotcloud.core.message.update.UpdateMessageHandler;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileClient {
    public static void main(String[] args) {
        SensorClient sensorClient = new SensorClient("http://localhost:8081/");
        InputStreamReader cin = new InputStreamReader(System.in);
        final BufferedReader reader = new BufferedReader(cin);

        final boolean[] quit = {false};
        sensorClient.registerWithName("file-sensor");

        sensorClient.setUpdateListener(new UpdateMessageHandler() {
            public void onUpdate(UpdateMessage message) {
                if (message.getUpdate(Constants.Updates.STATUS) != null &&
                        message.getUpdate(Constants.Updates.STATUS).equals(Constants.Updates.REMOVED)) {
                    quit[0] = true;
                    try {
                        reader.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        });

        sensorClient.listen(new MessageHandler() {
            public void onMessage(SensorMessage message) {
                if (message instanceof StreamDataMessage) {
                    StreamDataMessage dataMessage = (StreamDataMessage) message;
                    InputStream in = dataMessage.getInputStream();
                    File f = new File("Output.txt");
                    try {
                        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(f));
                        int i = in.read();
                        while (i != -1) {
                            outputStream.write(i);
                            i = in.read();
                        }
                        outputStream.close();
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        String in;
        try {
            in = reader.readLine();
            while (!"quit".equals(in) && !quit[0]) {
                DefaultControlMessage controlMessage = new DefaultControlMessage();
                controlMessage.addControl("action", in);
                sensorClient.sendControlMessage(controlMessage);

                in = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("quit client.......");
    }
}
