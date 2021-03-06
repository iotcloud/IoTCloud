package cgl.iotcloud.samples.arducopter.client;

import cgl.iotcloud.samples.arducopter.client.control.Controller;
import com.sun.java.swing.plaf.gtk.GTKLookAndFeel;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;

public class CopterFrame extends JFrame {
    private CopterUI copterUI;

    private ArduClient client;

    public CopterFrame() throws HeadlessException {
        super("Hexacopter");
        client = new ArduClient();

        Controller controller = new Controller(client);

        copterUI = new CopterUI(controller);
        client.setUpdater(copterUI.getDataModel());

        initComponents();

        start();
    }

    public void start() {
        client.start();
    }

    public void initComponents() {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(copterUI, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(copterUI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        addKeyListener(copterUI.getKeyControlListener());

        pack();
    }


    public static void main(String[] args) {
        CopterFrame copterFrame = new CopterFrame();
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(GTKLookAndFeel.class.getCanonicalName());
        }
        catch (UnsupportedLookAndFeelException e) {
            // handle exception
        }
        catch (ClassNotFoundException e) {
            // handle exception
        }
        catch (InstantiationException e) {
            // handle exception
        }
        catch (IllegalAccessException e) {
            // handle exception
        }
        copterFrame.setVisible(true);
        copterFrame.start();
    }
}
