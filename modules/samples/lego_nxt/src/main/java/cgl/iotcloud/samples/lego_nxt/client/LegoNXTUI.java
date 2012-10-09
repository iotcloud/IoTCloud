package cgl.iotcloud.samples.lego_nxt.client;


import cgl.iotcloud.client.robot.ActionController;
import cgl.iotcloud.client.robot.RootFrame;
import cgl.iotcloud.client.robot.SensorDataController;
import cgl.iotcloud.samples.lego_nxt.common.LegoNXTSensorTypes;
import cgl.iotcloud.samples.lego_nxt.sensor.Velocity;


public class LegoNXTUI {

    private static LegoNXTClient client;
    private static LegoNXTUI legoNXTUI;
    
    private boolean touchSensorEnabled;

    private ActionController actController = new ActionController() {
        @Override
        public void up() {
            client.setVelocity(new Velocity(.1, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
        }

        @Override
        public void down() {
            client.setVelocity(new Velocity(-.1, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
        }

        @Override
        public void left() {
            client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0, 0.0, -.5));
        }

        @Override
        public void right() {
            client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0.0, 0.0, .5));
        }

        @Override
        public void stop() {
            client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0.0, 0.0, 0));
        }
    };
    
    private SensorDataController  senDataController = new SensorDataController() {
		
		@Override
		public void stop(String sensorName) {
			if(sensorName.equalsIgnoreCase(LegoNXTSensorTypes.TOUCH_SENSOR))
				touchSensorEnabled = false;
		}
		
		@Override
		public void start(String sensorName) {
			if(sensorName.equalsIgnoreCase(LegoNXTSensorTypes.TOUCH_SENSOR))
				touchSensorEnabled = true;
		}
	}; 
	
	boolean isTouchSensorEnabled(){
		return touchSensorEnabled;
	}

    public void start() {
        client = new LegoNXTClient();

        client.start();

        RootFrame rootFrame = RootFrame.getInstance();
        rootFrame.addSensor(LegoNXTSensorTypes.TOUCH_SENSOR);
        rootFrame.addActionController(actController);
        rootFrame.addSensorDataController(senDataController);

        rootFrame.setVisible(true);
    }

    public static void main(String[] args) {
    	if(legoNXTUI == null)
    		legoNXTUI = new LegoNXTUI();

    	legoNXTUI.start();
    }
    
    public static LegoNXTUI getInstance(){
    	return legoNXTUI;
    }
    
    public void updateUI(String msg){
    	RootFrame rootFrame = RootFrame.getInstance();
    	rootFrame.update(msg);
    }
}
