package cgl.iotcloud.client.robot;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

public class ControlPanel extends JPanel implements RobotUIPanelBuilder {
	private RootFrame rootFrame;
	private JButton starightButton = new JButton("UP");
	private JButton leftButton = new JButton("LEFT");
	private JButton backButton = new JButton("BACK");
	private JButton rightButton = new JButton("RIGHT");

	public ControlPanel(RootFrame rootFrame){
		this.rootFrame = rootFrame;
		addButtonListeners();
		this.setBackground(new java.awt.Color(225, 222, 222));
		this.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.darkGray, java.awt.Color.gray));
		this.addComponents();
	}

	private void addButtonListeners() {
		starightButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				rootFrame.getActionController().stop();
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				rootFrame.getActionController().start();
				rootFrame.getActionController().up();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {

			}
		});

		backButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				rootFrame.getActionController().stop();
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				rootFrame.getActionController().start();
				rootFrame.getActionController().down();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {

			}
		});

		rightButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				rootFrame.getActionController().stop();
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				rootFrame.getActionController().start();
				rootFrame.getActionController().right();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				rootFrame.getActionController().start();
				rootFrame.getActionController().right();
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {

			}
		});
		leftButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				rootFrame.getActionController().stop();
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				rootFrame.getActionController().start();
				rootFrame.getActionController().left();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

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
