package cgl.iotcloud.client.robot;

import javax.swing.*;

public class ControlTitlePanel extends JPanel implements RobotUIPanelBuilder {
	private RootFrame rootFrame;
    private JLabel conTitleLabel = new JLabel("Control");


    public ControlTitlePanel(RootFrame rootFrame) {
    	this.rootFrame = rootFrame;
    	this.setBackground(new java.awt.Color(0, 0, 0));
        conTitleLabel.setForeground(new java.awt.Color(255, 255, 255));
        this.addComponents();
    }

    @Override
    public void addComponents() {
        GroupLayout conTitlePanelLayout = new GroupLayout(this);
        this.setLayout(conTitlePanelLayout);
        conTitlePanelLayout.setHorizontalGroup(
                conTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(conTitlePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(conTitleLabel)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        conTitlePanelLayout.setVerticalGroup(
                conTitlePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(conTitlePanelLayout.createSequentialGroup()
                                .addComponent(conTitleLabel)
                                .addGap(0, 6, Short.MAX_VALUE))
        );
    }

    @Override
    public void removeComponents() {
        GroupLayout conTitlePanelLayout = (GroupLayout) this.getLayout();
        conTitlePanelLayout.removeLayoutComponent(conTitleLabel);
    }
}
