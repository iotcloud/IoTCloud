package cgl.iotcloud.client.robot;

import javax.swing.*;

public class RootPanel extends JPanel implements RobotUIPanelBuilder {
    private SensorContainerPanel sensorContainerPanel;
    private RootFrame rootFrame;
    private SenConPanel senConPanel;
    
  /*  public static void main(String args[]){
    	RootPanel panel = new RootPanel(rootFrame);
        panel.setVisible(true);
    }*/

    public RootPanel(RootFrame rootFrame) {
    	this.rootFrame = rootFrame;
    	sensorContainerPanel = new SensorContainerPanel(rootFrame);
    	senConPanel = new SenConPanel(rootFrame);
        try {
            this.addComponents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addComponents() {
        GroupLayout rootPanelLayout = new GroupLayout(this);
        this.setLayout(rootPanelLayout);
        rootPanelLayout.setHorizontalGroup(
                rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, rootPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(senConPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sensorContainerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        rootPanelLayout.setVerticalGroup(
                rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rootPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(senConPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(sensorContainerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
    }

    @Override
    public void removeComponents() {
        GroupLayout rootPanelLayout = (GroupLayout) this.getLayout();
        rootPanelLayout.removeLayoutComponent(senConPanel);
        rootPanelLayout.removeLayoutComponent(sensorContainerPanel);
    }

    public SensorContainerPanel getSensorContainerPanel() {
        return sensorContainerPanel;
    }

    public SenConPanel getSenConPanel() {
        return senConPanel;
    }
}
