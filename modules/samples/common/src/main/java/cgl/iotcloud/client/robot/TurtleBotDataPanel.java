package cgl.iotcloud.client.robot;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TurtleBotDataPanel extends JPanel implements RobotUIPanelBuilder {
    private BufferedImage image = null;

    public TurtleBotDataPanel() {
    }

    @Override
    public void addComponents() {
    }

    @Override
    public void removeComponents() {
    }

    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, null);
        repaint();
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}