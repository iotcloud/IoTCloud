package cgl.iotcloud.samples.turtlebot.client;

import javax.swing.*;
import java.awt.*;

public class TurtleUI extends JFrame {

    public TurtleUI(String title) throws HeadlessException {
        super(title);
    }

    public static void main(String[] args) {

        TurtleUI frame = new TurtleUI("Turtlebot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add the ubiquitous "Hello World" label.
        JLabel label = new JLabel("Hello World");
        frame.getContentPane().add(label);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
        frame.setSize(400, 400);
    }

}
