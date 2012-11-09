package cgl.iotcloud.samples.turtlebot.client;


import cgl.iotcloud.client.robot.*;
import cgl.iotcloud.core.IOTRuntimeException;
import cgl.iotcloud.samples.turtlebot.common.TurtleSensorTypes;
import cgl.iotcloud.samples.turtlebot.sensor.Velocity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;


public class TurtleUI {
    private TurtleClient client;
    private static TurtleUI ui;
    private RootFrame rootFrame;
    private boolean kinectEnabled;
    
    private ActionController actController = new ActionController() {
    	Thread worker;
    	boolean started;
    	
        @Override
        public void up() {
        	worker = new Thread(){
				public void run(){
					while(isStarted()){
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
    };
    
    private DataController  dataController = new DataController() {
		
		@Override
		public void stop(String sensorName) {
			if(sensorName.equalsIgnoreCase(TurtleSensorTypes.KINECT_SENSOR))
				kinectEnabled = false;
		}
		
		@Override
		public void start(String sensorName) {
			if(sensorName.equalsIgnoreCase(TurtleSensorTypes.KINECT_SENSOR))
				kinectEnabled = true;
		}
	}; 
	
	public boolean isKinectSensorEnabled(){
		return kinectEnabled;
	}
    
    public void start() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                client = new TurtleClient(ui);
                try {
                    client.start();
                } catch (IOTRuntimeException e) {
                    e.printStackTrace();

                }
            }
        });

        t.start();
        
      /*  rootFrame = new RootFrame();
        rootFrame.setRobot("turtlebot");
        
        RootPanel rootPanel = rootFrame.getRootPanel();
        SenConPanel senConPanel = rootPanel.getSenConPanel();

        ControlContainerPanel controlContainerPanel = senConPanel.getControlContainerPanel();

        ControlPanel controlPanel = controlContainerPanel.getControlPanel();
        ControlSessionPanel controlSessionPanel = controlContainerPanel.getControlSessionPanel();

        controlPanel.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.setVelocity(new Velocity(-.1, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
            }
        });

        controlPanel.getStarightButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.setVelocity(new Velocity(.1, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
            }
        });

        controlPanel.getLeftButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0, 0.0, -.5));
            }
        });

        controlPanel.getRightButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0.0, 0.0, .5));
            }
        });

        controlSessionPanel.getStopButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
            }
        });
*/
        
        rootFrame = new RootFrame();
        rootFrame.setRobot("turtlebot");
        rootFrame.addSensor(TurtleSensorTypes.KINECT_SENSOR);
        rootFrame.setActionController(actController);
        rootFrame.setDataController(dataController);
        
        rootFrame.setVisible(true);
        
    }

    public static void main(String[] args) {
        ui = new TurtleUI();
        ui.start();
    }
    
    public void update(BufferedImage data){
    	rootFrame.update(data);
    }
}
