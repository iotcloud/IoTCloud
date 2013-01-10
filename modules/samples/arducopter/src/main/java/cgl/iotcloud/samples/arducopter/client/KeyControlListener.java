package cgl.iotcloud.samples.arducopter.client;

import cgl.iotcloud.core.Control;
import cgl.iotcloud.samples.arducopter.client.control.Controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyControlListener extends KeyAdapter {
    private Controller controller;

    public KeyControlListener(Controller controller) {
        this.controller = controller;
    }

    public void keyTyped(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DOWN:
                controller.move(Controller.StickPos.RIGHT, Controller.Direction.DOWN);
                break;
            case KeyEvent.VK_UP:
                controller.move(Controller.StickPos.RIGHT, Controller.Direction.UP);
                break;
            case KeyEvent.VK_LEFT:
                controller.move(Controller.StickPos.RIGHT, Controller.Direction.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                controller.move(Controller.StickPos.RIGHT, Controller.Direction.RIGHT);
                break;
            case KeyEvent.VK_W:
                controller.move(Controller.StickPos.LEFT, Controller.Direction.UP);
                break;
            case KeyEvent.VK_S:
                controller.move(Controller.StickPos.RIGHT, Controller.Direction.UP);
                break;
            case KeyEvent.VK_D:
                controller.move(Controller.StickPos.RIGHT, Controller.Direction.LEFT);
                break;
            case KeyEvent.VK_A:
                controller.move(Controller.StickPos.RIGHT, Controller.Direction.RIGHT);
                break;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {

        } else if (e.ge)
    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }
}
