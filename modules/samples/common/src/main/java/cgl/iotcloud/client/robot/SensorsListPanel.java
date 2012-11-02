package cgl.iotcloud.client.robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SensorsListPanel extends JPanel implements RobotUIPanelBuilder {
    // private SensorsListPanel sensorsListPanel;
    private Map<String, JButton> sensorNameToButtonMap = new HashMap<String, JButton>();
    private String sensorSelected = null;
    private GroupLayout.ParallelGroup parallelGrp;
    private GroupLayout.SequentialGroup sequentialGrp;
    private GroupLayout senPanelLayout = new GroupLayout(this);

    public void removeSensor(String sensorName) {
        JButton sensorButton = null;
        if (sensorNameToButtonMap != null && sensorNameToButtonMap.containsKey(sensorName))
            sensorButton = sensorNameToButtonMap.remove(sensorName);
        removeSensorFromPanel(sensorButton);
    }

    private void removeSensorFromPanel(JButton sensorButton) {
        parallelGrp = senPanelLayout.createParallelGroup();
        sequentialGrp = senPanelLayout.createSequentialGroup();

        Collection<JButton> sensorButtons = sensorNameToButtonMap.values();
        for (JButton _sensorButton : sensorButtons) {
            parallelGrp.addComponent(_sensorButton, 20, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE);
            sequentialGrp.addComponent(_sensorButton);
            sequentialGrp.addGap(20);

            senPanelLayout.setHorizontalGroup(senPanelLayout.createSequentialGroup().addGap(40).addGroup(parallelGrp).addGap(40));
            senPanelLayout.setVerticalGroup(sequentialGrp);
        }
    }

    public void addSensor(String sensorName) {
        JButton sensorButton = new JButton(sensorName);
        if (sensorNameToButtonMap != null)
            sensorNameToButtonMap.put(sensorName, sensorButton);
        sensorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sensorName = ((JButton) e.getSource()).getText();
                sensorSelected = sensorName;
            }
        });
        addSensorToPanel(sensorButton);
    }

    private void addSensorToPanel(JButton sensorButton) {
        parallelGrp.addComponent(sensorButton, 20, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE);
        sequentialGrp.addComponent(sensorButton);
        sequentialGrp.addGap(20);

        senPanelLayout.setHorizontalGroup(senPanelLayout.createSequentialGroup().addGap(40).addGroup(parallelGrp).addGap(40));
        senPanelLayout.setVerticalGroup(sequentialGrp);
    }

    public String getSensorSelected() {
        return sensorSelected;
    }

    public SensorsListPanel() {
        this.setBackground(new Color(255, 255, 255));
        this.addComponents();
    }

    @Override
    public void addComponents() {
        this.setLayout(senPanelLayout);

        parallelGrp = senPanelLayout.createParallelGroup();
        sequentialGrp = senPanelLayout.createSequentialGroup();

        senPanelLayout.setHorizontalGroup(senPanelLayout.createSequentialGroup().addGap(40).addGroup(parallelGrp).addGap(40));
        senPanelLayout.setVerticalGroup(sequentialGrp);
    }

    @Override
    public void removeComponents() {

    }
}
