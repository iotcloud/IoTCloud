package cgl.iotcloud.client.robot;

import javax.swing.*;

public class ControlContainerPanel extends JPanel implements RobotUIPanelBuilder {
    private ControlPanel controlPanel = new ControlPanel();
    private ControlSessionPanel controlSessionPanel = new ControlSessionPanel();
    private ControlTitlePanel controlTitlePanel = new ControlTitlePanel();

    public ControlContainerPanel() {
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
                                .addComponent(controlSessionPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                        .addComponent(controlTitlePanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
        );
        conContainerPanelLayout.setVerticalGroup(
                conContainerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(conContainerPanelLayout.createSequentialGroup()
                                .addComponent(controlTitlePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(conContainerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(controlSessionPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(controlPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(19, 19, 19))
        );
    }

    @Override
    public void removeComponents() {
        GroupLayout conContainerPanelLayout = (GroupLayout) getLayout();
        conContainerPanelLayout.removeLayoutComponent(controlPanel);
        conContainerPanelLayout.removeLayoutComponent(controlTitlePanel);
        conContainerPanelLayout.removeLayoutComponent(controlSessionPanel);
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public ControlSessionPanel getControlSessionPanel() {
        return controlSessionPanel;
    }
}
