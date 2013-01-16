package cgl.iotcloud.samples.arducopter.client;

import cgl.iotcloud.samples.arducopter.client.control.Controller;

import javax.swing.*;
import java.awt.*;

public class CopterFrame extends JFrame {
    private CopterUI copterUI;

    private ArduClient client;

    public CopterFrame() throws HeadlessException {
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
        copterFrame.setVisible(true);
        copterFrame.start();
    }
}
