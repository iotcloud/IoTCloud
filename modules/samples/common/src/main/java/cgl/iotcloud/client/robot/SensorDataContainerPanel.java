package cgl.iotcloud.client.robot;

import java.awt.image.BufferedImage;

import javax.swing.*;

public class SensorDataContainerPanel extends JPanel implements RobotUIPanelBuilder {
    private RootFrame rootFrame;
    private SensorDataTitlePanel sensorDataTitlePanel;

    private JTextArea senDataTextArea = new JTextArea();
    private JPanel dataPanel = null;

    public SensorDataContainerPanel(RootFrame rootFrame) {
    	this.rootFrame = rootFrame;
    	sensorDataTitlePanel = new SensorDataTitlePanel(rootFrame);
        this.setBackground(new java.awt.Color(255, 255, 255));
        this.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        
        if(rootFrame.getRobot().equalsIgnoreCase("turtlebot"))
        	dataPanel = new TurtleBotDataPanel(rootFrame);
        this.addComponents();
    }

    public JPanel getDataPanel() {
        return dataPanel;
    }

    @Override
    public void addComponents() {
        GroupLayout senDataMainPanelLayout = new GroupLayout(this);

        senDataTextArea.setEditable(false);
        senDataTextArea.setVisible(true);

        // by default Text Panel, turtlebot image panel.
        JScrollPane scrollPane;
        if (dataPanel != null) {
            this.setLayout(senDataMainPanelLayout);
            senDataMainPanelLayout.setHorizontalGroup(
                    senDataMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(sensorDataTitlePanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dataPanel, javax.swing.GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );
            senDataMainPanelLayout.setVerticalGroup(
                    senDataMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(senDataMainPanelLayout.createSequentialGroup()
                                    .addComponent(sensorDataTitlePanel, GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(dataPanel, GroupLayout.PREFERRED_SIZE, 419, Short.MAX_VALUE))
            );
        } else {
            scrollPane = new JScrollPane(senDataTextArea,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

            this.setLayout(senDataMainPanelLayout);
            senDataMainPanelLayout.setHorizontalGroup(
                    senDataMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(sensorDataTitlePanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
            );
            senDataMainPanelLayout.setVerticalGroup(
                    senDataMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(senDataMainPanelLayout.createSequentialGroup()
                                    .addComponent(sensorDataTitlePanel, GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 419, Short.MAX_VALUE))
            );
        }
    }

    @Override
    public void removeComponents() {

    }

    public void updateData(String data) {
        String currentData = senDataTextArea.getText();
        senDataTextArea.setText(currentData + "\n" + data);
    }
    
    public void updateData(BufferedImage data) {
    	((TurtleBotDataPanel)dataPanel).setImage(data);
    }
}
