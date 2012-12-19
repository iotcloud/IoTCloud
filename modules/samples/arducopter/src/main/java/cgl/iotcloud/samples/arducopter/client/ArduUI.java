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
                client = new ArduClient(ui);
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

    public void updateAttitudeMessage(SensorMessage message)
    {
        AttitudeMessage attitudeMessage = (AttitudeMessage) message;
        dataPanel.updateAttitudeData(attitudeMessage.getPitch(),
                attitudeMessage.getPitchSpeed(),
                attitudeMessage.getRoll(),
                attitudeMessage.getRollSpeed(),
                attitudeMessage.getYaw(),
                attitudeMessage.getYawSpeed());
    }

    public void updateControlMessage(SensorMessage message)
    {
        ControlMessage controlMessage = (ControlMessage) message;
        dataPanel.updateControlData(controlMessage.getPitch(),
                controlMessage.getRoll(),
                controlMessage.getThrust(),
                controlMessage.getYaw());
    }

    public void updateMRIMessage(SensorMessage message)
    {
        MRIMessage mriMessage = (MRIMessage) message;
        dataPanel.updateMRIData(mriMessage.getTimeUsec(),
                mriMessage.getXacc(),
                mriMessage.getXgyro(),
                mriMessage.getXmag(),
                mriMessage.getYacc(),
                mriMessage.getYgyro(),
                mriMessage.getYmag(),
                mriMessage.getZacc(),
                mriMessage.getZgyro(),
                mriMessage.getZmag());
    }

    public void updateStateMessage(SensorMessage message)
    {
        StateMessage stateMessage = (StateMessage) message;
        dataPanel.updateStateData(stateMessage.getMode(),
                stateMessage.isArmed(),
                stateMessage.isGuided());
    }

    public void updateVHMessage(SensorMessage message)
    {
        VHMessage vhMessage = (VHMessage) message;
        dataPanel.updateVHData(vhMessage.getAirSpeed(),
                vhMessage.getAlt(),
                vhMessage.getClimb(),
                vhMessage.getGroundSpeed(),
                vhMessage.getHeading(),
                vhMessage.getThrottle());
    }
}
