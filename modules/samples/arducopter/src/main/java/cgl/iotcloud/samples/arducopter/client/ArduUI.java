package cgl.iotcloud.samples.arducopter.client;

import cgl.iotcloud.client.robot.*;
import cgl.iotcloud.core.IOTRuntimeException;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.samples.arducopter.mssg.*;

import javax.swing.*;

public class ArduUI {

    private ArduClient client;
    private static ArduUI ui;
    private RootFrame rootFrame;
    private ArduCopterDataPanel dataPanel;

    public void start() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                client = new ArduClient(dataPanel);
                try {
                    client.start();
                } catch (IOTRuntimeException e) {
                    e.printStackTrace();

                }
            }
        });

        t.start();

        rootFrame = new RootFrame("ArduSensor");

        RootPanel rootPanel = rootFrame.getRootPanel();
        SenConPanel senConPanel = rootPanel.getSenConPanel();

        JPanel dataContainerPanel = senConPanel.getSensorDataContainerPanel().getDataPanel();

        dataPanel = new ArduCopterDataPanel();

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

        rootFrame.setVisible(true);

    }

    public static void main(String[] args) {
        ui = new ArduUI();
        ui.start();
    }
}
