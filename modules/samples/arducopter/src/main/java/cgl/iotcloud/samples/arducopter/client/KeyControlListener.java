package cgl.iotcloud.samples.arducopter.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyControlListener implements KeyListener {
    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println("Key typed");
        System.out.println(e.getKeyCode());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Key pressed");
        System.out.println(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println("key released");
        System.out.println(e.getKeyCode());
    }
}
