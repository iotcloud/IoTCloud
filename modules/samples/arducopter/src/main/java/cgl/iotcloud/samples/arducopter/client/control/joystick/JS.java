package cgl.iotcloud.samples.arducopter.client.control.joystick;

import cgl.iotcloud.samples.arducopter.client.control.Controller;
import com.centralnexus.input.Joystick;
import com.centralnexus.input.JoystickListener;

import java.io.IOException;

public class JS {
    private Joystick joystick;

    private final Controller controller;

    public JS() {
        this(null);
    }

    public JS(Controller controller) {
        this.controller = controller;
        try {
            joystick = Joystick.createInstance();

            joystick.addJoystickListener(new JoySL());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Please make sure the joystick is connected...");
        }
    }

    private class JoySL implements JoystickListener {

        @Override
        public void joystickAxisChanged(Joystick joystick) {
            if (controller != null) {
                controller.axisChanged(joystick.getX(), joystick.getY(), joystick.getZ(), joystick.getR());
            }
        }

        @Override
        public void joystickButtonChanged(Joystick joystick) {
            if ((joystick.getButtons() & Joystick.BUTTON7) > 0) {
                if (controller != null) {
                    controller.setActive(true);
                }
            }
            if ((joystick.getButtons() & Joystick.BUTTON8) > 0) {
                if (controller != null) {
                    controller.setActive(false);
                }
            }
        }
    }

    public static void main(String[] args) {
        JS js = new JS();
        while(true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
