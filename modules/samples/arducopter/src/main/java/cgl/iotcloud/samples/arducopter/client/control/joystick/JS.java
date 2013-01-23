package cgl.iotcloud.samples.arducopter.client.control.joystick;

import cgl.iotcloud.samples.arducopter.client.control.Controller;
import com.centralnexus.input.Joystick;
import com.centralnexus.input.JoystickListener;

import java.io.IOException;

public class JS {
    private Joystick joystick;

    private final Controller controller;

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
            controller.axisChanged(joystick.getX(), joystick.getY(), joystick.getZ(), joystick.getR());
        }

        @Override
        public void joystickButtonChanged(Joystick joystick) {
            controller.buttonChanged(joystick.getButtons());
        }
    }
}
