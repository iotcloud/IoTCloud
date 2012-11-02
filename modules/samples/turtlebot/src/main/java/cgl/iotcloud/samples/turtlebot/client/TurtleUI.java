package cgl.iotcloud.samples.turtlebot.client;


import cgl.iotcloud.client.robot.*;
import cgl.iotcloud.core.IOTRuntimeException;
import cgl.iotcloud.samples.turtlebot.sensor.Velocity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class TurtleUI {
    private TurtleClient client;

    public void start() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                client = new TurtleClient();
                try {
                    client.start();
                } catch (IOTRuntimeException e) {
                    e.printStackTrace();

                }
            }
        });

        t.start();
        RootFrame rootFrame = new RootFrame();
        RootPanel rootPanel = rootFrame.getRootPanel();
        SenConPanel senConPanel = rootPanel.getSenConPanel();

        ControlContainerPanel controlContainerPanel = senConPanel.getControlContainerPanel();

        ControlPanel controlPanel = controlContainerPanel.getControlPanel();
        ControlSessionPanel controlSessionPanel = controlContainerPanel.getControlSessionPanel();

        controlPanel.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.setVelocity(new Velocity(-.1, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
            }
        });

        controlPanel.getStarightButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.setVelocity(new Velocity(.1, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
            }
        });

        controlPanel.getLeftButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0, 0.0, -.5));
            }
        });

        controlPanel.getRightButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0.0, 0.0, .5));
            }
        });

        controlSessionPanel.getStopButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
            }
        });

        rootFrame.setVisible(true);
    }

    public static void main(String[] args) {
        TurtleUI ui = new TurtleUI();

        ui.start();
    }
}
