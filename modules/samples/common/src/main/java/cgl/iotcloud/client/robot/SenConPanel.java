package cgl.iotcloud.client.robot;

import javax.swing.*;

public class SenConPanel extends JPanel implements RobotUIPanelBuilder {
    private SensorDataContainerPanel sensorDataContainerPanel;
    private ControlContainerPanel controlContainerPanel;

    public SenConPanel(RootFrame rootFrame) {
    	sensorDataContainerPanel = new SensorDataContainerPanel();
    	controlContainerPanel = new ControlContainerPanel(rootFrame);
        this.addComponents();
    }

    @Override
    public void addComponents() {
        GroupLayout senDataConPanelLayout = new GroupLayout(this);
        this.setLayout(senDataConPanelLayout);
        senDataConPanelLayout.setHorizontalGroup(
                senDataConPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(sensorDataContainerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(controlContainerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        senDataConPanelLayout.setVerticalGroup(
                senDataConPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(senDataConPanelLayout.createSequentialGroup()
                                .addComponent(sensorDataContainerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(controlContainerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }

    @Override
    public void removeComponents() {
        GroupLayout senDataTitleLayout = (GroupLayout) getLayout();
        senDataTitleLayout.removeLayoutComponent(sensorDataContainerPanel);
        senDataTitleLayout.removeLayoutComponent(controlContainerPanel);
    }

    public SensorDataContainerPanel getSensorDataContainerPanel() {
        return sensorDataContainerPanel;
    }

    public ControlContainerPanel getControlContainerPanel() {
        return controlContainerPanel;
    }
}
