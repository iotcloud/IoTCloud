package cgl.iotcloud.client.robot;

import javax.swing.*;

public class SensorContainerPanel extends JPanel implements RobotUIPanelBuilder{
	private RootFrame rootFrame;
    private SensorTitlePanel sensorTitlePanel; 
    private SensorsListPanel sensorsListPanel; 

    public SensorContainerPanel (RootFrame rootFrame){
    	this.rootFrame = rootFrame;
    	sensorTitlePanel = new SensorTitlePanel(rootFrame);
    	sensorsListPanel = new SensorsListPanel(rootFrame);
        this.setBackground(new java.awt.Color(255, 255, 255));
        this.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        this.addComponents();
    }
    @Override
    public void addComponents() {
        GroupLayout senMainPanelLayout = new GroupLayout(this);

        this.setLayout(senMainPanelLayout);
        senMainPanelLayout.setHorizontalGroup(
                senMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(sensorTitlePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(sensorsListPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        senMainPanelLayout.setVerticalGroup(
                senMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(senMainPanelLayout.createSequentialGroup()
                                .addComponent(sensorTitlePanel, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                                .addGap(10)
                                .addComponent(sensorsListPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE))
        );
    }

    @Override
    public void removeComponents() {
        GroupLayout sensorTitlePanelLayout = (GroupLayout)this.getLayout();
        sensorTitlePanelLayout.removeLayoutComponent(sensorTitlePanel);
    }

    public SensorTitlePanel getSensorTitlePanel() {
        return sensorTitlePanel;
    }

    public SensorsListPanel getSensorsListPanel() {
        return sensorsListPanel;
    }
}
