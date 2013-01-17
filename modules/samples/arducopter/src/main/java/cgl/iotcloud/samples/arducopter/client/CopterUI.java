package cgl.iotcloud.samples.arducopter.client;

import cgl.iotcloud.samples.arducopter.client.control.Controller;
import cgl.iotcloud.samples.arducopter.client.control.KeyControlListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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

        jScrollPane1.addKeyListener(keyControlListener);
        jTable1.addKeyListener(keyControlListener);

        ll.addKeyListener(keyControlListener);
        lr.addKeyListener(keyControlListener);
        lu.addKeyListener(keyControlListener);
        ld.addKeyListener(keyControlListener);
        rl.addKeyListener(keyControlListener);
        rr.addKeyListener(keyControlListener);
        ru.addKeyListener(keyControlListener);
        rd.addKeyListener(keyControlListener);

        jButton1.addKeyListener(keyControlListener);

        addKeyListener(keyControlListener);

        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller.isActive()) {
                    controller.setActive(false);
                    jButton1.setText("Enable");
                } else {
                    controller.setActive(true);
                    jButton1.setText("Disable");
                }
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

