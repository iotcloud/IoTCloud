package cgl.iotcloud.samples.lego_nxt.client;


import cgl.iotcloud.client.robot.ActionController;
import cgl.iotcloud.client.robot.RootFrame;
import cgl.iotcloud.client.robot.DataController;
import cgl.iotcloud.samples.lego_nxt.common.LegoNXTSensorTypes;
import cgl.iotcloud.samples.lego_nxt.sensor.Velocity;


public class LegoNXTUI {
	
	private RootFrame rootFrame;
    private LegoNXTClient client;
    private static LegoNXTUI legoNXTUI;
    
    private boolean touchSensorEnabled;
    private boolean ultrasonicSensorEnabled;
    private boolean gyroSensorEnabled;

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

    public void start() {
    	rootFrame = new RootFrame();
        rootFrame.setRobot("lego_ nxt");
        rootFrame.addSensor(LegoNXTSensorTypes.TOUCH_SENSOR);
        rootFrame.addSensor(LegoNXTSensorTypes.ULTRASONIC_SENSOR);
        rootFrame.addSensor(LegoNXTSensorTypes.GYRO_SENSOR);
        rootFrame.addActionController(actController);
        rootFrame.addSensorDataController(dataController);
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
    	rootFrame.update(msg);
    }
}
