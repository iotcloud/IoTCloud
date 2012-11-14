package cgl.iotcloud.client.robot;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SensorsListPanel extends JPanel implements RobotUIPanelBuilder {
	private RootFrame rootFrame;
	private JTree sensorTree;
	private DefaultMutableTreeNode rootNode;
	private Map<String, DefaultMutableTreeNode> sensorNameToNodeMap = new HashMap<String, DefaultMutableTreeNode>();
	private String sensorSelected = null;
	private GroupLayout.ParallelGroup parallelGrp;
	private GroupLayout.SequentialGroup sequentialGrp;
	private GroupLayout senPanelLayout;

	public void addSensor(String sensorName) {
		DefaultMutableTreeNode sensorNode = new DefaultMutableTreeNode(sensorName);
		if (sensorNameToNodeMap != null)
			sensorNameToNodeMap.put(sensorName, sensorNode);
		updateUITree();
	}

	public void deleteSensor(String sensorName) {
		sensorNameToNodeMap.remove(sensorName);
		updateUITree();
	}

	public void addRobotNode(String robot){
		rootNode = new DefaultMutableTreeNode(robot);
	}

	public void updateUITree(){
		Collection<DefaultMutableTreeNode> sensorNodes = sensorNameToNodeMap.values();

		DefaultTreeModel treeModel = (DefaultTreeModel)sensorTree.getModel();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)treeModel.getRoot();

		for(DefaultMutableTreeNode sensorNode : sensorNodes){
			rootNode.add(sensorNode);
		}

		treeModel.reload(rootNode);
		sensorTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				sensorSelected = sensorTree.getLastSelectedPathComponent().toString();
			}
		});
		sensorTree.setRowHeight(40);
	}

	public String getSensorSelected() {
//		if(sensorSelected.isEmpty()){
//			JOptionPane.showMessageDialog(rootFrame, "Select a sensor.", "WARNING", 1, null);
//			return "";
//		}else
//			return sensorSelected;
        return "";
	}

	public SensorsListPanel(RootFrame rootFrame) {
		this.rootFrame = rootFrame;
		senPanelLayout = new GroupLayout(this);
		this.setBackground(new Color(255, 255, 255));
		this.addComponents();

	}

	@Override
	public void addComponents() {
		this.setLayout(senPanelLayout);

		parallelGrp = senPanelLayout.createParallelGroup();
		sequentialGrp = senPanelLayout.createSequentialGroup();

		rootNode = new DefaultMutableTreeNode("TurtleBot");
		sensorTree = new JTree(rootNode);
		sensorTree.setRowHeight(40);
		parallelGrp.addComponent(sensorTree);

		sequentialGrp.addComponent(sensorTree);
		senPanelLayout.setHorizontalGroup(senPanelLayout.createSequentialGroup().addGap(40).addGroup(parallelGrp).addGap(40));
		senPanelLayout.setVerticalGroup(sequentialGrp);

	}


	@Override
	public void removeComponents() {

	}
}
