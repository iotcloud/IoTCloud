package cgl.iotcloud.client.robot;

import javax.swing.*;

public class SensorTitlePanel extends JPanel implements RobotUIPanelBuilder {
    //    private static SensorTitlePanel senTitlePanel;
    private JLabel senLabel = new JLabel("Sensors");

//    public static SensorTitlePanel getInstance(){
//        if(senTitlePanel == null)
//            senTitlePanel = new SensorTitlePanel();
//        return senTitlePanel;
//    }

    public SensorTitlePanel() {
        this.setBackground(new java.awt.Color(0, 0, 0));
        senLabel.setForeground(new java.awt.Color(255, 255, 255));
        this.addComponents();
    }

    @Override
    public void addComponents() {
        GroupLayout senTitlePanelLayout = new GroupLayout(this);
        this.setLayout(senTitlePanelLayout);
        senTitlePanelLayout.setHorizontalGroup(
                senTitlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(senTitlePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(senLabel)
                                .addContainerGap(120, Short.MAX_VALUE))
        );
        senTitlePanelLayout.setVerticalGroup(
                senTitlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(senTitlePanelLayout.createSequentialGroup()
                                .addComponent(senLabel)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    @Override
    public void removeComponents() {
        GroupLayout senTitleLayout = (GroupLayout) this.getLayout();
        senTitleLayout.removeLayoutComponent(senLabel);
    }
}