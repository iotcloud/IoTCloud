package cgl.iotcloud.samples.turtlebot.client;


import cgl.iotcloud.client.robot.Mover;
import cgl.iotcloud.client.robot.RootFrame;
import cgl.iotcloud.samples.turtlebot.sensor.Velocity;


public class TurtleUI {

    private TurtleClient client;

    private Mover mover = new Mover() {
        @Override
        public void up() {
            client.setVelocity(new Velocity(.1, 0.0, 0.0), new Velocity(0.0, 0.0, 0.0));
        }

        @Override
        public void down() {
            client.setVelocity(new Velocity(0, 0.0, 0.0), new Velocity(0.0, 0.0, 0.0));
        }

        @Override
        public void left() {
            client.setVelocity(new Velocity(.1, 0.0, 0.0), new Velocity(0.0, 0.0, 0.0));
        }

        @Override
        public void right() {
            client.setVelocity(new Velocity(.1, 0.0, 0.0), new Velocity(0.0, 0.0, 0.0));
        }
    };

    public void start() {
        client = new TurtleClient();

        client.start();

        RootFrame rootFrame = RootFrame.getInstance();
        rootFrame.addControl(mover);

        rootFrame.setVisible(true);
    }

    public static void main(String[] args) {
        TurtleUI ui = new TurtleUI();

        ui.start();
    }
}
