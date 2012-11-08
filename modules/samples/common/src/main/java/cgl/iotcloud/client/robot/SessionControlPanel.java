package cgl.iotcloud.client.robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SessionControlPanel extends JPanel implements RobotUIPanelBuilder {
	private RootFrame rootFrame;
    private JButton startButton = new JButton("Start");
    private JButton stopButton = new JButton("Stop");
    private Thread run;

    public SessionControlPanel(RootFrame rootFrame) {
    	this.rootFrame = rootFrame;
    	addButtonListners();
        this.setBackground(new Color(255, 255, 255));
        this.addComponents();
    }

    private void addButtonListners() {
    	startButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				rootFrame.getDataController().start(rootFrame.getSensorSelected());
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
    	
	stopButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				rootFrame.getDataController().stop(rootFrame.getSensorSelected());
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
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