/*
THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. NEITHER RECIPIENT NOR
ANY CONTRIBUTORS SHALL HAVE ANY LIABILITY FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING WITHOUT
LIMITATION LOST PROFITS), HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OR DISTRIBUTION OF THE PROGRAM
OR THE EXERCISE OF ANY RIGHTS GRANTED HEREUNDER, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGES.

The name of the Copyright Holder may not be used to endorse or promote
products derived from this software without specific prior written permission.

Copyright 2000 George Rhoten and others.

*/

package com.centralnexus.test;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import com.centralnexus.input.*;

public class WindowTest
 extends Frame
 implements Runnable, JoystickListener, ActionListener
{

    Joystick joy;

    Joystick joy2;

    /** polling interval for this joystick */
    private int interval = 50;

    Thread thread = new Thread(this);

    Label buttonLabel = new Label(),
          button2Label = new Label(),
          deadZoneLabel = new Label(),
          xLabel = new Label(),
          yLabel = new Label(),
          zLabel = new Label(),
          rLabel = new Label(),
          uLabel = new Label(),
          vLabel = new Label(),
          povLabel = new Label();
    Label xyLabel = new Label();
    Label intervalLabel = new Label();

    Button addButton = new Button("Add Listener");
    Button removeButton = new Button("Remove Listener");

    WindowTest() throws IOException {
        super();

        joy = Joystick.createInstance();
        for (int idx = joy.getID() + 1; idx < Joystick.getNumDevices(); idx++) {
            if (Joystick.isPluggedIn(idx)) {
                joy2 = Joystick.createInstance(idx);
            }
        }
        if (joy2 == null) {
            joy2 = joy;
        }
        doWindowLayout();
    }

    WindowTest(int joystickID, int joyID2) throws IOException {
        super();

        joy = Joystick.createInstance(joystickID);
        joy2 = Joystick.createInstance(joyID2);
        doWindowLayout();
    }

    private void doWindowLayout() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
        setTitle("Joystick Test");

        setLayout(new GridLayout(20, 2));
        add(new Label("Number Of Devices: ", Label.RIGHT));
        add(new Label(Integer.toString(Joystick.getNumDevices())));
        add(new Label("Joystick ID: ", Label.RIGHT));
        add(new Label("joy(" + Integer.toString(joy.getID()) + "), joy#2(" + Integer.toString(joy2.getID()) + ")"));
        add(new Label("Description joy#1: ", Label.RIGHT));
        add(new Label(joy.toString()));
        add(new Label("Description joy#2: ", Label.RIGHT));
        add(new Label(joy2.toString()));
        add(new Label("Capabilities:", Label.RIGHT));
        add(new Label("joy(0x" + Integer.toHexString(joy.getCapabilities()) + "), joyEx(0x" + Integer.toHexString(joy2.getCapabilities()) + ")"));
        add(new Label("Axes: ", Label.RIGHT));
        add(new Label("joy(" + Integer.toString(joy.getNumAxes()) + "), joy#2(" + Integer.toString(joy2.getNumAxes()) + ")"));
        add(new Label("Buttons: ", Label.RIGHT));
        add(new Label("joy(" + Integer.toString(joy.getNumButtons()) + "), joy#2(" + Integer.toString(joy2.getNumButtons()) + ")"));
        add(new Label("Dead Zone Size: ", Label.RIGHT));
        add(deadZoneLabel);
        add(new Label("Buttons Pressed: 0x", Label.RIGHT));
        add(buttonLabel);
        add(new Label("X: ", Label.RIGHT));
        add(xLabel);
        add(new Label("Y: ", Label.RIGHT));
        add(yLabel);
        add(new Label("Z: ", Label.RIGHT));
        add(zLabel);
        add(new Label("R: ", Label.RIGHT));
        add(rLabel);
        add(new Label("U: ", Label.RIGHT));
        add(uLabel);
        add(new Label("V: ", Label.RIGHT));
        add(vLabel);
        add(new Label("POV: ", Label.RIGHT));
        add(povLabel);
        add(new Label("Joystick#2(x, y): ", Label.RIGHT));
        add(xyLabel);
        add(new Label("Buttons Pressed: 0x", Label.RIGHT));
        add(button2Label);
        add(new Label("Polling interval: ", Label.RIGHT));
        add(intervalLabel);
        addButton.addActionListener(this);
        removeButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
//        System.out.println(e);
        if (e.getSource() == addButton) {
            joy.addJoystickListener(this);
            joy2.addJoystickListener(this);
        }
        else {
            joy.removeJoystickListener(this);
            joy2.removeJoystickListener(this);
        }
    }

    /**
     * This is used by the internal thread.  It creates a lot of String
     * objects, so it uses the garbage collector a lot.  Since this is
     * for testing only, this is not a problem for speed.
     */
    public void run() {
        for (;;) {
            joy.poll();
            joy2.poll();

            updateFieldsEx(joy);
            updateFields(joy2);
            try {
                Thread.sleep(interval);
            } catch(InterruptedException e) {
                break;
            }
        }
    }

    public void joystickAxisChanged(Joystick j) {
//        System.out.println(j.toString());
        if (j == joy) {
            updateFieldsEx(j);
        }
        else {
            updateFields(j);
        }
    }

    public void joystickButtonChanged(Joystick j) {
//        System.out.println(j.toString());
        if (j == joy) {
            updateFieldsEx(j);
        }
        else {
            updateFields(j);
        }
    }

    public void setPollInterval(int pollMillis) {
        interval = pollMillis;
        joy.setPollInterval(pollMillis);
        joy2.setPollInterval(pollMillis);
        intervalLabel.setText(Integer.toString(interval));
    }

    public void updateFields(Joystick joystick) {
        button2Label.setText(Integer.toHexString(joystick.getButtons()));
        xyLabel.setText(joystick.getX() + ", " + joystick.getY());
    }

    public void updateFieldsEx(Joystick joystick) {
        buttonLabel.setText(Integer.toHexString(joystick.getButtons()));
        xLabel.setText(Double.toString(joystick.getX()));
        yLabel.setText(Double.toString(joystick.getY()));
        zLabel.setText(Double.toString(joystick.getZ()));
        rLabel.setText(Double.toString(joystick.getR()));
        uLabel.setText(Double.toString(joystick.getU()));
        vLabel.setText(Double.toString(joystick.getV()));
        povLabel.setText(Double.toString(joystick.getPOV()));
    }

    public void startPolling() {
        thread.start();
    }

    public void addListeners() {
        add(addButton);
        add(removeButton);
        joy.addJoystickListener(this);
        joy2.addJoystickListener(this);
    }

    public void setDeadZone(double deadZone) {
        joy.setDeadZone(deadZone);
        updateDeadZone();
    }

    public void setDeadZoneEx(double deadZone) {
        joy2.setDeadZone(deadZone);
        updateDeadZone();
    }

    public void updateDeadZone() {
        deadZoneLabel.setText("joy(" + joy.getDeadZone() + "), joy#2("
                              + joy2.getDeadZone() + ")");
    }

    private static void help() {
        System.out.println("Help:");
        System.out.println(" -h This help screen info");
        System.out.println(" -v Verbose Joystick debug information");
        System.out.println(" -j:n Set the Joystick ID to test (n is an integer)");
        System.out.println(" -j2:n Set the second joystick ID to test (n is an integer)");
        System.out.println(" -d:n Set the dead zone size of the Joystick (n is a real number)");
        System.out.println(" -d2:n Set the dead zone size of the second Joystick (n is a real number)");
    }

    public static void main(String args[]) {
        // This first and last one are never there, but this is for internal testing.
        // They should ALWAYS be false.
        try {
            WindowTest mainFrame;
            WindowTest listenerFrame;
            int joystickNum = -1, joystickNumEx = -1;
            double deadZone = -1.0, deadZoneEx = -1.0;
            int interval = 50;

            for (int idx = 0; idx < args.length; idx++) {
                if (args[idx].startsWith("-d2:")) {
                    deadZoneEx = 
                        Double.valueOf(args[idx].substring(4, args[idx].length()))
                        .doubleValue();
                }
                else if (args[idx].startsWith("-d:")) {
                    deadZone = 
                        Double.valueOf(args[idx].substring(3, args[idx].length()))
                        .doubleValue();
                }
                else if (args[idx].startsWith("-i:")) {
                    interval = 
                        Integer.valueOf(args[idx].substring(3, args[idx].length()))
                        .intValue();
                }
                else if (args[idx].startsWith("-j:")) {
                    joystickNum = 
                        Integer.valueOf(args[idx].substring(3, args[idx].length()))
                        .intValue();
                }
                else if (args[idx].startsWith("-j2:")) {
                    joystickNumEx = 
                        Integer.valueOf(args[idx].substring(4, args[idx].length()))
                        .intValue();
                }
                else if (args[idx].startsWith("-v")) {
                    for (int id = -1; id <= Joystick.getNumDevices(); id++) {
                        System.out.println("Joystick " + id + ": " + Joystick.isPluggedIn(id));
                    }
                }
                else if (args[idx].startsWith("-h")) {
                    help();
                }
                else {
                    System.out.println("Unknown option: " + args[idx]);
                    help();
                }
            }
            if (joystickNum >= 0) {
                if (joystickNumEx < 0) {
                    joystickNumEx = joystickNum;
                }
                mainFrame = new WindowTest(joystickNum, joystickNumEx);
                listenerFrame = new WindowTest(joystickNum, joystickNumEx);
            }
            else {
                mainFrame = new WindowTest();
                listenerFrame = new WindowTest();
            }
            if (deadZone >= 0.0) {
                mainFrame.setDeadZone(deadZone);
                listenerFrame.setDeadZone(deadZone);
            }
            if (deadZoneEx >= 0.0) {
                mainFrame.setDeadZoneEx(deadZoneEx);
                listenerFrame.setDeadZoneEx(deadZoneEx);
            }
            mainFrame.setPollInterval(interval);
            mainFrame.updateDeadZone();
            mainFrame.pack();
            mainFrame.setTitle("Polling Joystick");
            //mainFrame.show();
            mainFrame.setVisible(true);
            mainFrame.startPolling();

            listenerFrame.setPollInterval(interval);
            listenerFrame.updateDeadZone();
            listenerFrame.addListeners();
            listenerFrame.pack();
            listenerFrame.setTitle("Listener Joystick");
            Point pt = mainFrame.getLocation();
            pt.x += listenerFrame.getWidth();
            listenerFrame.setLocation(pt);
            //listenerFrame.show();
            listenerFrame.setVisible(true);
        } catch (IOException e) {
            System.err.println("");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
