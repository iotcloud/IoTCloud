package cgl.iotcloud.client.robot;

import javax.swing.*;

public class ControlPanel extends JPanel implements RobotUIPanelBuilder {
    private JButton starightButton = new JButton("UP");
    private JButton leftButton = new JButton("LEFT");
    private JButton backButton = new JButton("BACK");
    private JButton rightButton = new JButton("RIGHT");

    public ControlPanel(){
        this.setBackground(new java.awt.Color(225, 222, 222));
        this.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.darkGray, java.awt.Color.gray));
        this.addComponents();
    }

    @Override
    public void addComponents() {
        GroupLayout conPanelLayout = new GroupLayout(this);
        this.setLayout(conPanelLayout);
        conPanelLayout.setHorizontalGroup(
                conPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(conPanelLayout.createSequentialGroup()
                                .addGap(123, 123, 123)
                                .addGroup(conPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(starightButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(backButton, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(123, Short.MAX_VALUE))
                        .addGroup(conPanelLayout.createSequentialGroup()
                                .addComponent(leftButton, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(rightButton, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE))
        );
        conPanelLayout.setVerticalGroup(
                conPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(conPanelLayout.createSequentialGroup()
                                .addComponent(starightButton)
                                .addGap(2, 2, 2)
                                .addGroup(conPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(leftButton)
                                        .addComponent(rightButton))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(backButton))
        );

    }

    @Override
    public void removeComponents() {
        GroupLayout conPanelLayout = (GroupLayout)getLayout();
        conPanelLayout.removeLayoutComponent(starightButton);
        conPanelLayout.removeLayoutComponent(leftButton);
        conPanelLayout.removeLayoutComponent(rightButton);
        conPanelLayout.removeLayoutComponent(backButton);
    }

    public JButton getStarightButton() {
        return starightButton;
    }

    public JButton getLeftButton() {
        return leftButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getRightButton() {
        return rightButton;
    }
}
