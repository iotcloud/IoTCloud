package cgl.iotcloud.samples.arducopter.client;

import java.util.HashMap;
import java.util.Map;

public class CopterUIDataModel extends javax.swing.table.DefaultTableModel implements Updater {
    private Class[] types = new Class [] {
            String.class, Object.class};

    private final String[] names = new String[]{"Pitch",
            "Pitch Speed",
            "Roll",
            "Roll Speed",
            "Yaw",
            "Yaw Speed",
            "Pitch Control",
            "Roll Control",
            "Yaw Control",
            "Thrust",
            "xacc",
            "xgyro",
            "xmag",
            "yacc",
            "ygyro",
            "ymag",
            "zacc",
            "zgyro",
            "zmag",
            "Time Usec",
            "mode",
            "armed",
            "guided",
            "Air Speed",
            "Altitude",
            "Climb",
            "GRNDSpeed",
            "Heading",
            "Throttle"};

    private Object data[][];

    private Object columnNames[] = new String [] {
            "Attribute", "Value"};

    private Map<String, Integer> index = new HashMap<String, Integer>();

    public CopterUIDataModel() {
        data = new Object[names.length][2];
        for (int i = 0; i < names.length; i++) {
            data[i][0] = names[i];
            data[i][1] = null;
            index.put(names[i], i);
        }

        setColumnIdentifiers(columnIdentifiers);
        setDataVector(data, columnNames);
    }

    public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void updateAttitudeData(float pitch, float pitchSpeed, float roll, float rollSpeed, float yaw, float yawSpeed) {
        setValueAt(pitch, 0, 1);
        setValueAt(pitchSpeed, 1, 1);
        setValueAt(roll, 2, 1);
        setValueAt(rollSpeed, 3, 1);
        setValueAt(yaw, 4, 1);
        setValueAt(yawSpeed, 5, 1);
    }

    public void updateControlData(float pitch, float roll, float thrust, float yaw) {
        setValueAt(pitch, 6, 1);
        setValueAt(roll, 7, 1);
        setValueAt(yaw, 8, 1);
        setValueAt(thrust, 9, 1);

    }

    public void updateMRIData(long timeUsec, int xacc, int xgyro, int xmag,
                              int yacc, int ygyro, int ymag, int zacc, int zgyro, int zmag) {
        setValueAt(xacc, 10, 1);
        setValueAt(xgyro, 11, 1);
        setValueAt(xmag, 12, 1);

        setValueAt(yacc, 13, 1);
        setValueAt(ygyro, 14, 1);
        setValueAt(ymag, 15, 1);

        setValueAt(zacc, 16, 1);
        setValueAt(zgyro, 17, 1);
        setValueAt(zmag, 18, 1);

        setValueAt(timeUsec, 19, 1);
    }

    public void updateStateData(String mode, boolean armed, boolean guided) {
        setValueAt(mode, 20, 1);
        setValueAt(armed, 21, 1);
        setValueAt(guided, 22, 1);
    }

    public void updateVHData(float airSpeed, float alt, float climb,
                             float groundSpeed, short heading, short throttle) {
        setValueAt(airSpeed, 23, 1);
        setValueAt(alt, 24, 1);
        setValueAt(climb, 25, 1);
        setValueAt(groundSpeed, 26, 1);
        setValueAt(heading, 27, 1);
        setValueAt(throttle, 28, 1);
    }
}
