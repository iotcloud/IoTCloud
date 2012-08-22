package cgl.iotcloud.samples.lego_nxt.client;


import cgl.iotcloud.client.robot.ActionController;
import cgl.iotcloud.client.robot.RootFrame;
import cgl.iotcloud.samples.lego_nxt.sensor.Velocity;


public class LegoNXTUI {

    private LegoNXTClient client;

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

    public void start() {
        client = new LegoNXTClient();

        client.start();

        RootFrame rootFrame = RootFrame.getInstance();
        rootFrame.addControl(actController);

        rootFrame.setVisible(true);
    }

    public static void main(String[] args) {
        LegoNXTUI ui = new LegoNXTUI();

        ui.start();
    }
}
