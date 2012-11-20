package cgl.iotcloud.client.robot;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class SensorDataContainerPanel extends JPanel implements RobotUIPanelBuilder {
    private SensorDataTitlePanel sensorDataTitlePanel;

    private JTextArea senDataTextArea = new JTextArea();
    private JPanel dataPanel = new JPanel();

    public SensorDataContainerPanel() {
    	sensorDataTitlePanel = new SensorDataTitlePanel();
        this.setBackground(new Color(255, 255, 255));
        this.setBorder(new LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        this.addComponents();
    }

    //Refer turtlebot and lego nxt for addition of panels.
    //following code is overriden in sensor code... due to diffrence the data panels among robots.
    @Override
    public void addComponents() {
        GroupLayout senDataMainPanelLayout = new GroupLayout(this);

        senDataTextArea.setEditable(false);
        senDataTextArea.setVisible(true);

        setLayout(senDataMainPanelLayout);
        // by default Text Panel, turtlebot image panel.
        JScrollPane scrollPane;
        if (dataPanel != null) {

            senDataMainPanelLayout.setHorizontalGroup(
                    senDataMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(sensorDataTitlePanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dataPanel, javax.swing.GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );
            senDataMainPanelLayout.setVerticalGroup(
                    senDataMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(senDataMainPanelLayout.createSequentialGroup()
                                    .addComponent(sensorDataTitlePanel, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(dataPanel, GroupLayout.PREFERRED_SIZE, 419, Short.MAX_VALUE))
            );
        } else {
        	//System.out.println("===called else ==");
            scrollPane = new JScrollPane(senDataTextArea,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

            senDataMainPanelLayout.setHorizontalGroup(
                    senDataMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(sensorDataTitlePanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(scrollPane, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
            );
            senDataMainPanelLayout.setVerticalGroup(
                    senDataMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(senDataMainPanelLayout.createSequentialGroup()
                                    .addComponent(sensorDataTitlePanel, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 419, Short.MAX_VALUE))
            );
        }
    }

    public JPanel getDataPanel() {
        return dataPanel;
    }
    

    @Override
    public void removeComponents() {

    }
    
    public SensorDataTitlePanel getSensorDataTitlePanel(){
    	return sensorDataTitlePanel;
    }

    //not used... sensor code expected to handle update.
    public void updateData(String data) {
        String currentData = senDataTextArea.getText();
        senDataTextArea.setText(currentData + "\n" + data);
    }
}
