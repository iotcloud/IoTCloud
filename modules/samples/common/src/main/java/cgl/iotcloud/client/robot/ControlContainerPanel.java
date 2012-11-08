package cgl.iotcloud.client.robot;

import javax.swing.*;

public class ControlContainerPanel extends JPanel implements RobotUIPanelBuilder {
    private RootFrame rootFrame;
	private ControlPanel controlPanel; 
    private SessionControlPanel sessionControlPanel; 
    private ControlTitlePanel controlTitlePanel; 

    public ControlContainerPanel(RootFrame rootFrame) {
    	this.rootFrame = rootFrame;
    	controlPanel = new ControlPanel(rootFrame);
    	sessionControlPanel = new SessionControlPanel(rootFrame);
    	controlTitlePanel = new ControlTitlePanel(rootFrame);
        this.setBackground(new java.awt.Color(255, 255, 255));
        this.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        addComponents();
    }

    @Override
    public void addComponents() {
        GroupLayout conContainerPanelLayout = new GroupLayout(this);
        this.setLayout(conContainerPanelLayout);
        conContainerPanelLayout.setHorizontalGroup(
                conContainerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(conContainerPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(controlPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                                .addComponent(sessionControlPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                        .addComponent(controlTitlePanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
        );
        conContainerPanelLayout.setVerticalGroup(
                conContainerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(conContainerPanelLayout.createSequentialGroup()
                                .addComponent(controlTitlePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(conContainerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(sessionControlPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(controlPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(19, 19, 19))
        );
    }

    @Override
    public void removeComponents() {
        GroupLayout conContainerPanelLayout = (GroupLayout) getLayout();
        conContainerPanelLayout.removeLayoutComponent(controlPanel);
        conContainerPanelLayout.removeLayoutComponent(controlTitlePanel);
        conContainerPanelLayout.removeLayoutComponent(sessionControlPanel);
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public SessionControlPanel getSessionControlPanel() {
        return sessionControlPanel;
    }
}
