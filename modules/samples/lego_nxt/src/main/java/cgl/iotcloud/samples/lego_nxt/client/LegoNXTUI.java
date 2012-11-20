package cgl.iotcloud.samples.lego_nxt.client;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;

import cgl.iotcloud.client.robot.ControlContainerPanel;
import cgl.iotcloud.client.robot.ControlPanel;
import cgl.iotcloud.client.robot.RootFrame;
import cgl.iotcloud.client.robot.DataController;
import cgl.iotcloud.client.robot.RootPanel;
import cgl.iotcloud.client.robot.SenConPanel;
import cgl.iotcloud.client.robot.SensorDataContainerPanel;
import cgl.iotcloud.core.IOTRuntimeException;
import cgl.iotcloud.samples.lego_nxt.common.LegoNXTSensorTypes;
import cgl.iotcloud.samples.lego_nxt.sensor.Velocity;


public class LegoNXTUI {

	private RootFrame rootFrame;
	private LegoNXTClient client;
	private static LegoNXTUI legoNXTUI;

	private boolean touchSensorEnabled;
	private boolean ultrasonicSensorEnabled;
	private boolean gyroSensorEnabled;
	private JTextArea senDataTextArea;

	//old code...
	/*  private ActionController actController = new ActionController() {
    	Thread worker;
    	boolean started;

        @Override
        public void up() {
        	worker = new Thread(){
				public void run(){

					while(isStarted()){
						System.out.println("=== isStarted ===");
						client.setVelocity(new Velocity(.1, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}	
				}
			};
			worker.start();
            //client.setVelocity(new Velocity(.1, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
        }

        @Override
        public void down() {
        	worker = new Thread(){
				public void run(){
					while(isStarted()){
						client.setVelocity(new Velocity(-.1, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}	
				}
			};
			worker.start();
            //client.setVelocity(new Velocity(-.1, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
        }

        @Override
        public void left() {
        	worker = new Thread(){
				public void run(){
					while(isStarted()){
						client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0, 0.0, -.5));
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}	
				}
			};
			worker.start();
            //client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0, 0.0, -.5));
        }

        @Override
        public void right() {
        	worker = new Thread(){
				public void run(){
					while(isStarted()){
						client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0.0, 0.0, .5));
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}	
				}
			};
			worker.start();
            //client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0.0, 0.0, .5));
        }

        @Override
        public void stop() {
            client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
        	setStarted(false);
        	worker = null;
        }

        public void start(){
        	setStarted(true); 
        }

        public boolean isStarted(){
        	return started;
        }

        public void setStarted(boolean value){
        	started = value;
        }
    };*/

	private DataController  dataController = new DataController() {

		@Override
		public void stop(String sensorName) {
			if(sensorName.equalsIgnoreCase(LegoNXTSensorTypes.TOUCH_SENSOR))
				touchSensorEnabled = false;
			if(sensorName.equalsIgnoreCase(LegoNXTSensorTypes.ULTRASONIC_SENSOR))
				ultrasonicSensorEnabled = false;
			if(sensorName.equalsIgnoreCase(LegoNXTSensorTypes.GYRO_SENSOR))
				gyroSensorEnabled = false;
		}

		@Override
		public void start(String sensorName) {
			if(sensorName.equalsIgnoreCase(LegoNXTSensorTypes.TOUCH_SENSOR))
				touchSensorEnabled = true;
			if(sensorName.equalsIgnoreCase(LegoNXTSensorTypes.ULTRASONIC_SENSOR))
				ultrasonicSensorEnabled = true;
			if(sensorName.equalsIgnoreCase(LegoNXTSensorTypes.GYRO_SENSOR))
				gyroSensorEnabled = true;
		}
	}; 

	boolean isTouchSensorEnabled(){
		return touchSensorEnabled;
	}

	boolean isGyroSensorEnabled(){
		return gyroSensorEnabled;
	}

	boolean isUltrasonicSensorEnabled(){
		return ultrasonicSensorEnabled;
	}

	private void forward() {
		client.setVelocity(new Velocity(.2, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
	}

	private void back() {
		client.setVelocity(new Velocity(-.1, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
	}

	private void left() {
		client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0, 0.0, -.5));
	}

	private void right() {
		client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0.0, 0.0, .5));
	}

	private void stop() {
		client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
	}

	public void start() {
		rootFrame = new RootFrame("lego_ nxt");
		rootFrame.addSensor(LegoNXTSensorTypes.TOUCH_SENSOR);
		rootFrame.addSensor(LegoNXTSensorTypes.ULTRASONIC_SENSOR);
		rootFrame.addSensor(LegoNXTSensorTypes.GYRO_SENSOR);
		//rootFrame.setActionController(actController);
		rootFrame.setDataController(dataController);

		RootPanel rootPanel = rootFrame.getRootPanel();
		SenConPanel senConPanel = rootPanel.getSenConPanel();

		SensorDataContainerPanel dataContainerPanel = senConPanel.getSensorDataContainerPanel();

		senDataTextArea = new JTextArea();
		senDataTextArea.setEditable(false);
		senDataTextArea.setVisible(true);

		JScrollPane scrollPane = new JScrollPane(senDataTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);


		GroupLayout senDataMainPanelLayout = new GroupLayout(dataContainerPanel);
		dataContainerPanel.setLayout(senDataMainPanelLayout);

		scrollPane = new JScrollPane(senDataTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		senDataMainPanelLayout.setHorizontalGroup(
				senDataMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(dataContainerPanel.getSensorDataTitlePanel(), GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(scrollPane, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				);
		senDataMainPanelLayout.setVerticalGroup(
				senDataMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(senDataMainPanelLayout.createSequentialGroup()
						.addComponent(dataContainerPanel.getSensorDataTitlePanel(), GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 419, Short.MAX_VALUE))
				);


		ControlContainerPanel controlContainerPanel = senConPanel.getControlContainerPanel();

		ControlPanel controlPanel = controlContainerPanel.getControlPanel();

		controlPanel.getStarightButton().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				forward();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				stop();
			}
		});

		controlPanel.getLeftButton().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				left();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				stop();
			}
		});

		controlPanel.getRightButton().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				right();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				stop();
			}
		});

		controlPanel.getBackButton().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				back();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				stop();
			}
		});



		rootFrame.setVisible(true);

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				client = new LegoNXTClient(legoNXTUI);
				try {
					client.start();
				} catch (IOTRuntimeException e) {
					e.printStackTrace();

				}
			}
		});
		t.start();
	}

	public static void main(String[] args) {
		if(legoNXTUI == null)
			legoNXTUI = new LegoNXTUI();

		legoNXTUI.start();
	}

	public static LegoNXTUI getInstance(){
		return legoNXTUI;
	}

	public void update(String msg){
		String currentData = senDataTextArea.getText();
		senDataTextArea.setText(currentData + "\n" + msg);
	}
}
