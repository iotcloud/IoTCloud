package cgl.iotcloud.samples.turtlebot.client;

import cgl.iotcloud.client.robot.*;
import cgl.iotcloud.core.IOTRuntimeException;
import cgl.iotcloud.samples.turtlebot.sensor.Velocity;

import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class TurtleUI {
    private TurtleClient client;
    private static TurtleUI ui;
    private RootFrame rootFrame;
    private TurtleBotDataPanel dataPanel;
    
    public void start() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                client = new TurtleClient(ui);
                try {
                    client.start();
                } catch (IOTRuntimeException e) {
                    e.printStackTrace();

                }
            }
        });

        t.start();
        
        rootFrame = new RootFrame();
        rootFrame.setRobot("turtlebot");
        
        RootPanel rootPanel = rootFrame.getRootPanel();
        SenConPanel senConPanel = rootPanel.getSenConPanel();

        JPanel dataContainerPanel = senConPanel.getSensorDataContainerPanel().getDataPanel();

        // GroupLayout senDataMainPanelLayout = (GroupLayout) dataContainerPanel.getLayout();
        dataPanel = new TurtleBotDataPanel();

        GroupLayout senDataMainPanelLayout = new GroupLayout(dataContainerPanel);
        dataContainerPanel.setLayout(senDataMainPanelLayout);
        senDataMainPanelLayout.setHorizontalGroup(
                senDataMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(dataPanel, javax.swing.GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        senDataMainPanelLayout.setVerticalGroup(
                senDataMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(senDataMainPanelLayout.createSequentialGroup()
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dataPanel, GroupLayout.PREFERRED_SIZE, 419, Short.MAX_VALUE))
        );

        ControlContainerPanel controlContainerPanel = senConPanel.getControlContainerPanel();

        ControlPanel controlPanel = controlContainerPanel.getControlPanel();

        controlPanel.getStarightButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                forward();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                stop();
            }
        });

        controlPanel.getLeftButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                left();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                stop();
            }
        });

        controlPanel.getRightButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                right();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                stop();
            }
        });

        controlPanel.getBackButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                back();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                stop();
            }
        });
        rootFrame.setRobot("turtlebot");
        rootFrame.setVisible(true);
    }

    private void forward() {
        client.setVelocity(new Velocity(.1, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
    }

    private void back() {
        client.setVelocity(new Velocity(-.1, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
    }

    private void left() {
        client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0, 0.0, -.5));
    }

    private void right() {
        client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0.0, 0.0, .5));
    }

    private void stop() {
        client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
    }

    public static void main(String[] args) {
        ui = new TurtleUI();
        ui.start();
    }

    public void update(BufferedImage data){
    	dataPanel.setImage(data);
        dataPanel.repaint();
    }


}
