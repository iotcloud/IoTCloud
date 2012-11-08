package cgl.iotcloud.client.robot;

import java.awt.image.BufferedImage;
import java.lang.Exception;

import javax.swing.*;

/**
 * @RootFrame : Swing UI to control robot and collect sensor data
 */
public class RootFrame extends JFrame {
    private RootPanel rootPanel;
    private String robot ;
	private DataController dataController;
	private ActionController actionController;

    public static void main(String args[]){
        new RootFrame().setVisible(true);
    }
    /**
     * Creates new form SwingClient
     */
    public RootFrame() {
    	rootPanel = new RootPanel(this);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            GroupLayout layout = new GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(rootPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rootPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
            pack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RootPanel getRootPanel() {
        return rootPanel;
    }
    
    public String getRobot(){
    	return robot;
    }
    
    public void setRobot(String robot){
    	this.robot = robot;
    	rootPanel.getSensorContainerPanel().getSensorsListPanel().addRobotNode(robot);
    }
    
    public void addSensor(String sensorName){
    	rootPanel.getSensorContainerPanel().getSensorsListPanel().addSensor(sensorName);
    }
    
    public void deleteSensor(String sensorName){
    	rootPanel.getSensorContainerPanel().getSensorsListPanel().deleteSensor(sensorName);
    }
    
    public void update(String data){
    	rootPanel.getSenConPanel().getSensorDataContainerPanel().updateData(data);
    }
    
    public void update(BufferedImage data){
    	rootPanel.getSenConPanel().getSensorDataContainerPanel().updateData(data);
    }
    
    public void setDataController(DataController dataController){
    	this.dataController = dataController;
    }
    
    public DataController getDataController(){
    	return dataController;
    }
    
    public ActionController getActionController(){
    	return this.actionController ;
    }
    
    public void setActionController(ActionController actionController){
    	this.actionController = actionController;
    }
    
    public String getSensorSelected(){
		return rootPanel.getSensorContainerPanel().getSensorsListPanel().getSensorSelected();
    }
}	