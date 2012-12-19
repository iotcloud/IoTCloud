package cgl.iotcloud.samples.lego_nxt.client;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import cgl.iotcloud.client.robot.ControlContainerPanel;
import cgl.iotcloud.client.robot.ControlPanel;
import cgl.iotcloud.client.robot.RootFrame;
import cgl.iotcloud.client.robot.DataController;
import cgl.iotcloud.client.robot.RootPanel;
import cgl.iotcloud.client.robot.SenConPanel;
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
	private NXTSensorsListPanel sensorsListPanel;


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
		rootFrame = new RootFrame("Lego NXT");
		rootFrame.addSensor(LegoNXTSensorTypes.TOUCH_SENSOR);
		rootFrame.addSensor(LegoNXTSensorTypes.ULTRASONIC_SENSOR);
		rootFrame.addSensor(LegoNXTSensorTypes.GYRO_SENSOR);
		//rootFrame.setActionController(actController);
		rootFrame.setDataController(dataController);

		RootPanel rootPanel = rootFrame.getRootPanel();
		SenConPanel senConPanel = rootPanel.getSenConPanel();

		JPanel dataContainerPanel  = senConPanel.getSensorDataContainerPanel().getDataPanel();


		sensorsListPanel = new NXTSensorsListPanel();

		GroupLayout senDataMainPanelLayout = new GroupLayout(dataContainerPanel);
		dataContainerPanel.setLayout(senDataMainPanelLayout);
		senDataMainPanelLayout.setHorizontalGroup(
				senDataMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(sensorsListPanel, javax.swing.GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				);
		senDataMainPanelLayout.setVerticalGroup(
				senDataMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(senDataMainPanelLayout.createSequentialGroup()
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(sensorsListPanel, GroupLayout.PREFERRED_SIZE, 419, Short.MAX_VALUE))
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
		String displayMsg;
		System.out.println("==== msg receieved from robot === : "+msg);
		if(msg.indexOf(LegoNXTSensorTypes.TOUCH_SENSOR) !=-1){
			displayMsg = msg.substring(msg.indexOf(":")+1);
			sensorsListPanel.updateTouchData(displayMsg);
		}else if(msg.indexOf(LegoNXTSensorTypes.GYRO_SENSOR) !=-1){
			displayMsg = msg.substring(msg.indexOf(":")+1);
			sensorsListPanel.updateGyroData(displayMsg);
		}else{
			displayMsg = msg.substring(msg.indexOf(":")+1);
			sensorsListPanel.updateUltrasonicData(displayMsg);
		}
	}


	public class NXTSensorsListPanel extends javax.swing.JPanel {
		public NXTSensorsListPanel() {
			initComponents();
		}

		private void initComponents() {
			sensorsPanel = new javax.swing.JPanel();
			gyroLabel = new javax.swing.JLabel();
			gyroTextField = new javax.swing.JTextField();

			ultrasonicTextField = new javax.swing.JTextField();
			utrasonicLabel = new javax.swing.JLabel();
			
			touchLabel = new javax.swing.JLabel();
			touchTextField = new javax.swing.JTextField();
			
			

			sensorsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.lightGray, java.awt.Color.black));

			gyroLabel.setFont(new java.awt.Font("Trebuchet MS", 0, 13)); // NOI18N
			gyroLabel.setText("Gyro");

			ultrasonicTextField.setText(" ");

			touchLabel.setFont(new java.awt.Font("Trebuchet MS", 0, 13)); // NOI18N
			touchLabel.setText("Touch");

			touchTextField.setText(" ");

			utrasonicLabel.setFont(new java.awt.Font("Trebuchet MS", 0, 13)); // NOI18N
			utrasonicLabel.setText("Ultrasonic");

			gyroTextField.setText(" ");

			javax.swing.GroupLayout sensorsPanelLayout = new javax.swing.GroupLayout(sensorsPanel);
			sensorsPanel.setLayout(sensorsPanelLayout);
			sensorsPanelLayout.setHorizontalGroup(
					sensorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addGroup(sensorsPanelLayout.createSequentialGroup()
							.addGap(41, 41, 41)
							.addComponent(utrasonicLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addGap(18, 18, 18)
							.addComponent(ultrasonicTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
							.addGap(26, 26, 26))
							.addGroup(sensorsPanelLayout.createSequentialGroup()
									.addGap(38, 38, 38)
									.addComponent(gyroLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
									.addGap(18, 18, 18)
									.addComponent(gyroTextField)
									.addGap(29, 29, 29))
									.addGroup(sensorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
											.addGroup(sensorsPanelLayout.createSequentialGroup()
													.addGap(44, 44, 44)
													.addComponent(touchLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
													.addContainerGap(242, Short.MAX_VALUE)))
													.addGroup(sensorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
															.addGroup(sensorsPanelLayout.createSequentialGroup()
																	.addGap(149, 149, 149)
																	.addComponent(touchTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
																	.addGap(22, 22, 22)))
					);
			sensorsPanelLayout.setVerticalGroup(
					sensorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addGroup(sensorsPanelLayout.createSequentialGroup()
							.addGap(117, 117, 117)
							.addGroup(sensorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
									.addComponent(ultrasonicTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
									.addComponent(utrasonicLabel))
									.addGap(28, 28, 28)
									.addGroup(sensorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
											.addComponent(gyroLabel)
											.addComponent(gyroTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
											.addContainerGap(73, Short.MAX_VALUE))
											.addGroup(sensorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
													.addGroup(sensorsPanelLayout.createSequentialGroup()
															.addGap(67, 67, 67)
															.addComponent(touchLabel)
															.addContainerGap(188, Short.MAX_VALUE)))
															.addGroup(sensorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																	.addGroup(sensorsPanelLayout.createSequentialGroup()
																			.addGap(62, 62, 62)
																			.addComponent(touchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
																			.addContainerGap(183, Short.MAX_VALUE)))
					);

			javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
			this.setLayout(layout);
			layout.setHorizontalGroup(
					layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
							.addContainerGap()
							.addComponent(sensorsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addContainerGap())
					);
			layout.setVerticalGroup(
					layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
							.addContainerGap()
							.addComponent(sensorsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addContainerGap())
					);

			//pack();
		}

		public void updateGyroData(String data){
			gyroTextField.setText(data);
		}

		public void updateUltrasonicData(String data){
			ultrasonicTextField.setText(data);
		}

		public void updateTouchData(String data){
			touchTextField.setText(data);
		}


		private javax.swing.JLabel gyroLabel;
		private javax.swing.JTextField gyroTextField;
		private javax.swing.JPanel sensorsPanel;
		private javax.swing.JLabel touchLabel;
		private javax.swing.JTextField touchTextField;
		private javax.swing.JTextField ultrasonicTextField;
		private javax.swing.JLabel utrasonicLabel;
		// End of variables declaration
	}

}
