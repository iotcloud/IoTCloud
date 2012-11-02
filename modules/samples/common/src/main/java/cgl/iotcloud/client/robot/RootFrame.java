package cgl.iotcloud.client.robot;

import java.lang.Exception;

import javax.swing.*;

/**
 * @RootFrame : Swing UI to control robot and collect sensor data
 */
public class RootFrame extends JFrame {
    private RootPanel rootPanel = new RootPanel();

    /**
     * Creates new form SwingClient
     */
    public RootFrame() {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            GroupLayout layout = new GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(rootPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rootPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
            pack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RootPanel getRootPanel() {
        return rootPanel;
    }
}