package cgl.iotcloud.client.robot;

import javax.swing.*;
import java.awt.*;

public class SensorDataTitlePanel extends JPanel implements RobotUIPanelBuilder {
	private RootFrame rootFrame;
    private JLabel senDataTitleLabel = new JLabel("Sensor Data");

    public SensorDataTitlePanel(RootFrame rootFrame) {
    	this.rootFrame = rootFrame;
        this.setBackground(new java.awt.Color(0, 0, 0));
        senDataTitleLabel.setForeground(new Color(255, 255, 255));
        this.addComponents();
    }

    @Override
    public void addComponents() {
        GroupLayout senDataTitlePanelLayout = new GroupLayout(this);
        this.setLayout(senDataTitlePanelLayout);
        senDataTitlePanelLayout.setHorizontalGroup(
                senDataTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(senDataTitlePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(senDataTitleLabel)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        senDataTitlePanelLayout.setVerticalGroup(
                senDataTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(senDataTitlePanelLayout.createSequentialGroup()
                                .addComponent(senDataTitleLabel)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    @Override
    public void removeComponents() {
        GroupLayout senDataTitleLayout = (GroupLayout) getLayout();
        senDataTitleLayout.removeLayoutComponent(senDataTitleLabel);
    }
}