package cgl.iotcloud.samples.arducopter.client;

public class CopterUIDataModel extends javax.swing.table.DefaultTableModel {
    private Class[] types = new Class [] {
            java.lang.String.class, java.lang.Object.class};

    Object data[][] = new Object [][] {
            {"Pitch", null},
            {"Pitch Speed", null},
            {"Roll", null},
            {"Roll Speed", null},
            {"Yaw", null},
            {"Yaw Speed", null},
            {"Pitch Control", null},
            {"Roll Control", null},
            {"Yaw Control", null},
            {"Thrust", null},
            {"xacc", null},
            {"xgyro", null},
            {"xmag", null},
            {"yacc", null},
            {"ygyro", null},
            {"ymag", null},
            {"zacc", null},
            {"zgyro", null},
            {"zmag", null},
            {"Time Usec", null},
            {"mode", null},
            {"armed", null},
            {"guided", null},
            {"Air Speed", null},
            {"Altitude", null},
            {"Climb", null},
            {"GRNDSpeed", null},
            {"Heading", null},
            {"Throttle", null},

    };

    Object columnNames[] = new String [] {
            "Title 1", "Title 2"};

    public CopterUIDataModel() {
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
}
