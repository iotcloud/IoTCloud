package cgl.iotcloud.client.robot;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TurtleBotDataPanel extends JPanel implements RobotUIPanelBuilder{

    private static TurtleBotDataPanel turtleBotDataPanel;
    private BufferedImage image = null;

    public static TurtleBotDataPanel getInstance(){
        if(turtleBotDataPanel == null)
            turtleBotDataPanel = new TurtleBotDataPanel();
        return turtleBotDataPanel;
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

    public void setImage(BufferedImage _image){
        image = _image;
    }
}