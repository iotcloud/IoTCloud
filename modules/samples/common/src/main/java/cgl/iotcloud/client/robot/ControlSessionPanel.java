package cgl.iotcloud.client.robot;

import javax.swing.*;
import java.awt.*;

public class ControlSessionPanel extends JPanel implements RobotUIPanelBuilder {
    private JButton startButton = new JButton("Start");
    private JButton stopButton = new JButton("Stop");

    public ControlSessionPanel() {
        this.setBackground(new Color(255, 255, 255));
        this.addComponents();
    }

    @Override
    public void addComponents() {
        GroupLayout conSessionPanelLayout = new GroupLayout(this);
        this.setLayout(conSessionPanelLayout);
        conSessionPanelLayout.setHorizontalGroup(
                conSessionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(conSessionPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(conSessionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(startButton, GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                                        .addComponent(stopButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        conSessionPanelLayout.setVerticalGroup(
                conSessionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(conSessionPanelLayout.createSequentialGroup()
                                .addComponent(startButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(stopButton))
        );
    }

    @Override
    public void removeComponents() {
        GroupLayout conTitlePanelLayout = (GroupLayout) this.getLayout();
        conTitlePanelLayout.removeLayoutComponent(startButton);
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }
}