package cgl.iotcloud.samples.arducopter.client;

import cgl.iotcloud.samples.arducopter.client.control.Controller;
import cgl.iotcloud.samples.arducopter.client.control.EventHandler;
import cgl.iotcloud.samples.arducopter.client.control.KeyControlListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;


public class CopterUI extends CopterUIGen {
    private CopterUIDataModel dataModel;

    private KeyControlListener keyControlListener;

    private Controller controller;

    /**
     * Creates new form CopterUI
     */
    public CopterUI(Controller controller) {
        super();
        keyControlListener = new KeyControlListener(controller);
        this.controller = controller;
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        dataModel = new CopterUIDataModel();

        jTable1.setModel(dataModel);
        jTable1.setEnabled(false);

        jScrollPane1.addKeyListener(keyControlListener);
        jTable1.addKeyListener(keyControlListener);

        keyControlListener.setEventHandler(new EventHandler() {

            @Override
            public void handleEvent(AWTEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Updating the progress");
                        thrustpb.setValue(controller.getLeft().getY());
                        yawpb.setValue(controller.getLeft().getX());
                        pitchpb.setValue(controller.getRight().getY());
                        rollpb.setValue(controller.getRight().getX());
                    }
                });

            }
        });

        thrustpb.addKeyListener(keyControlListener);
        rollpb.addKeyListener(keyControlListener);
        pitchpb.addKeyListener(keyControlListener);
        yawpb.addKeyListener(keyControlListener);

        thrustpb.setMaximum(controller.getLeft().getYrange()[1]);
        thrustpb.setMinimum(controller.getLeft().getYrange()[0]);
        thrustpb.setValue(controller.getLeft().getY());

        yawpb.setMaximum(controller.getLeft().getXrange()[1]);
        yawpb.setMinimum(controller.getLeft().getXrange()[0]);
        yawpb.setValue(controller.getLeft().getX());

        pitchpb.setMaximum(controller.getRight().getYrange()[1]);
        pitchpb.setMinimum(controller.getRight().getYrange()[0]);
        pitchpb.setValue(controller.getRight().getY());

        rollpb.setMaximum(controller.getRight().getXrange()[1]);
        rollpb.setMinimum(controller.getRight().getXrange()[0]);
        rollpb.setValue(controller.getRight().getX());

        enableBttn.addKeyListener(keyControlListener);

        addKeyListener(keyControlListener);

        enableBttn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller.isActive()) {
                    controller.setActive(false);
                    enableBttn.setText("Enable");
                } else {
                    controller.setActive(true);
                    enableBttn.setText("Disable");
                }
            }
        });

        resetBttn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.reset();
                thrustpb.setValue(controller.getLeft().getY());
                yawpb.setValue(controller.getLeft().getX());
                pitchpb.setValue(controller.getRight().getY());
                rollpb.setValue(controller.getRight().getX());
            }
        });
    }

    public CopterUIDataModel getDataModel() {
        return dataModel;
    }

    public KeyControlListener getKeyControlListener() {
        return keyControlListener;
    }

}

