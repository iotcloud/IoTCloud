package cgl.iotcloud.client.robot;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TurtleBotDataPanel extends JPanel implements RobotUIPanelBuilder{
	
	private static RootFrame rootFrame;
    private static TurtleBotDataPanel turtleBotDataPanel;
    private BufferedImage image = null;

    public TurtleBotDataPanel (RootFrame rootFrame){
    	this.rootFrame = rootFrame;
    }

    @Override
    public void addComponents() {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeComponents() {
        // TODO Auto-generated method stub

    }

    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, null);
        repaint();
    }

    public void setImage(BufferedImage image){
        this.image = image;
    }
}