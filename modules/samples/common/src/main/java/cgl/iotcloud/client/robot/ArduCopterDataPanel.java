package cgl.iotcloud.client.robot;

import javax.swing.*;
import java.awt.*;

public class ArduCopterDataPanel extends JScrollPane implements RobotUIPanelBuilder{
    // Attitude Data Label
    private JLabel pitchDataLabel;
    private JLabel pitchSpeedDataLabel;
    private JLabel rollDataLabel;
    private JLabel rollSpeedDataLabel;
    private JLabel yawDataLabel;
    private JLabel yawSpeedDataLabel;
    // Control Data Label
    private JLabel pitchControlDataLabel;
    private JLabel rollControlDataLabel;
    private JLabel yawControlDataLabel;
    private JLabel thrustDataLabel;
    // MRI Data Labels
    private JLabel xaccDataLabel;
    private JLabel xgyroDataLabel;
    private JLabel xmagDataLabel;
    private JLabel yaccDataLabel;
    private JLabel ygyroDataLabel;
    private JLabel ymagDataLabel;
    private JLabel zaccDataLabel;
    private JLabel zgyroDataLabel;
    private JLabel zmagDataLabel;
    private JLabel timeUsecDataLabel;
    //State Data Labels
    private JLabel modeDataLabel;
    private JLabel armedDataLabel;
    private JLabel guidedDataLabel;
    // VH Data Label
    private JLabel airSpeedDataLabel;
    private JLabel altDataLabel;
    private JLabel climbDataLabel;
    private JLabel groundSpeedDataLabel;
    private JLabel headingDataLabel;
    private JLabel throttleDataLabel;

    public ArduCopterDataPanel()
    {
        this.setLayout(null);

        init();

    }

    @Override
    public void addComponents() {

    }

    @Override
    public void removeComponents() {

    }

    private void init()
    {
        JLabel pitchLabel = new JLabel("Pitch");
        pitchLabel.setBounds(0, 1, 100, 15);
        pitchLabel.setBackground(Color.WHITE);
        pitchLabel.setOpaque(true);
        pitchLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(pitchLabel);

        pitchDataLabel = new JLabel("");
        pitchDataLabel.setBounds(0, 16, 100, 15);
        pitchDataLabel.setForeground(Color.GREEN);
        pitchDataLabel.setBackground(Color.BLACK);
        pitchDataLabel.setOpaque(true);
        pitchDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(pitchDataLabel);

        JLabel pitchSpeedLabel = new JLabel("Pitch Speed");
        pitchSpeedLabel.setBounds(101, 1, 100, 15);
        pitchSpeedLabel.setBackground(Color.WHITE);
        pitchSpeedLabel.setOpaque(true);
        pitchSpeedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(pitchSpeedLabel);

        pitchSpeedDataLabel = new JLabel("");
        pitchSpeedDataLabel.setBounds(101, 16, 100, 15);
        pitchSpeedDataLabel.setForeground(Color.GREEN);
        pitchSpeedDataLabel.setBackground(Color.BLACK);
        pitchSpeedDataLabel.setOpaque(true);
        pitchSpeedDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(pitchSpeedDataLabel);

        JLabel rollLabel = new JLabel("Roll");
        rollLabel.setBounds(202, 1, 100, 15);
        rollLabel.setBackground(Color.WHITE);
        rollLabel.setOpaque(true);
        rollLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(rollLabel);

        rollDataLabel = new JLabel("");
        rollDataLabel.setBounds(202, 16, 100, 15);
        rollDataLabel.setForeground(Color.GREEN);
        rollDataLabel.setBackground(Color.BLACK);
        rollDataLabel.setOpaque(true);
        rollDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(rollDataLabel);

        JLabel rollSpeedLabel = new JLabel("Roll Speed");
        rollSpeedLabel.setBounds(303, 1, 100, 15);
        rollSpeedLabel.setBackground(Color.WHITE);
        rollSpeedLabel.setOpaque(true);
        rollSpeedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(rollSpeedLabel);

        rollSpeedDataLabel = new JLabel("");
        rollSpeedDataLabel.setBounds(303, 16, 100, 15);
        rollSpeedDataLabel.setForeground(Color.GREEN);
        rollSpeedDataLabel.setBackground(Color.BLACK);
        rollSpeedDataLabel.setOpaque(true);
        rollSpeedDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(rollSpeedDataLabel);

        JLabel yawLabel = new JLabel("Yaw");
        yawLabel.setBounds(404, 1, 100, 15);
        yawLabel.setBackground(Color.WHITE);
        yawLabel.setOpaque(true);
        yawLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(yawLabel);

        yawDataLabel = new JLabel("");
        yawDataLabel.setBounds(404, 16, 100, 15);
        yawDataLabel.setForeground(Color.GREEN);
        yawDataLabel.setBackground(Color.BLACK);
        yawDataLabel.setOpaque(true);
        yawDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(yawDataLabel);

        JLabel yawSpeedLabel = new JLabel("Yaw Speed");
        yawSpeedLabel.setBounds(0, 32, 100, 15);
        yawSpeedLabel.setBackground(Color.WHITE);
        yawSpeedLabel.setOpaque(true);
        yawSpeedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(yawSpeedLabel);

        yawSpeedDataLabel = new JLabel("");
        yawSpeedDataLabel.setBounds(0, 47, 100, 15);
        yawSpeedDataLabel.setForeground(Color.GREEN);
        yawSpeedDataLabel.setBackground(Color.BLACK);
        yawSpeedDataLabel.setOpaque(true);
        yawSpeedDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(yawSpeedDataLabel);

        JLabel pitchControlLabel = new JLabel("Pitch Control");
        pitchControlLabel.setBounds(101, 32, 100, 15);
        pitchControlLabel.setBackground(Color.WHITE);
        pitchControlLabel.setOpaque(true);
        pitchControlLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(pitchControlLabel);

        pitchControlDataLabel = new JLabel("");
        pitchControlDataLabel.setBounds(101, 47, 100, 15);
        pitchControlDataLabel.setForeground(Color.GREEN);
        pitchControlDataLabel.setBackground(Color.BLACK);
        pitchControlDataLabel.setOpaque(true);
        pitchControlDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(pitchControlDataLabel);

        JLabel rollControlLabel = new JLabel("Roll Control");
        rollControlLabel.setBounds(202, 32, 100, 15);
        rollControlLabel.setBackground(Color.WHITE);
        rollControlLabel.setOpaque(true);
        rollControlLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(rollControlLabel);

        rollControlDataLabel = new JLabel("");
        rollControlDataLabel.setBounds(202, 47, 100, 15);
        rollControlDataLabel.setForeground(Color.GREEN);
        rollControlDataLabel.setBackground(Color.BLACK);
        rollControlDataLabel.setOpaque(true);
        rollControlDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(rollControlDataLabel);

        JLabel yawControlLabel = new JLabel("Yaw Control");
        yawControlLabel.setBounds(303, 32, 100, 15);
        yawControlLabel.setBackground(Color.WHITE);
        yawControlLabel.setOpaque(true);
        yawControlLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(yawControlLabel);

        yawControlDataLabel = new JLabel("");
        yawControlDataLabel.setBounds(303, 47, 100, 15);
        yawControlDataLabel.setForeground(Color.GREEN);
        yawControlDataLabel.setBackground(Color.BLACK);
        yawControlDataLabel.setOpaque(true);
        yawControlDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(yawControlDataLabel);

        JLabel thrustLabel = new JLabel("Thrust");
        thrustLabel.setBounds(404, 32, 100, 15);
        thrustLabel.setBackground(Color.WHITE);
        thrustLabel.setOpaque(true);
        thrustLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(thrustLabel);

        thrustDataLabel = new JLabel("");
        thrustDataLabel.setBounds(404, 47, 100, 15);
        thrustDataLabel.setForeground(Color.GREEN);
        thrustDataLabel.setBackground(Color.BLACK);
        thrustDataLabel.setOpaque(true);
        thrustDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(thrustDataLabel);

        JLabel xaccLabel = new JLabel("xacc");
        xaccLabel.setBounds(0, 63, 100, 15);
        xaccLabel.setBackground(Color.WHITE);
        xaccLabel.setOpaque(true);
        xaccLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(xaccLabel);

        xaccDataLabel = new JLabel("");
        xaccDataLabel.setBounds(0, 79, 100, 15);
        xaccDataLabel.setForeground(Color.GREEN);
        xaccDataLabel.setBackground(Color.BLACK);
        xaccDataLabel.setOpaque(true);
        xaccDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(xaccDataLabel);

        JLabel xgyroLabel = new JLabel("xgyro");
        xgyroLabel.setBounds(101, 63, 100, 15);
        xgyroLabel.setBackground(Color.WHITE);
        xgyroLabel.setOpaque(true);
        xgyroLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(xgyroLabel);

        xgyroDataLabel = new JLabel("");
        xgyroDataLabel.setBounds(101, 79, 100, 15);
        xgyroDataLabel.setForeground(Color.GREEN);
        xgyroDataLabel.setBackground(Color.BLACK);
        xgyroDataLabel.setOpaque(true);
        xgyroDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(xgyroDataLabel);

        JLabel xmagLabel = new JLabel("xmag");
        xmagLabel.setBounds(202, 63, 100, 15);
        xmagLabel.setBackground(Color.WHITE);
        xmagLabel.setOpaque(true);
        xmagLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(xmagLabel);

        xmagDataLabel = new JLabel("");
        xmagDataLabel.setBounds(202, 79, 100, 15);
        xmagDataLabel.setForeground(Color.GREEN);
        xmagDataLabel.setBackground(Color.BLACK);
        xmagDataLabel.setOpaque(true);
        xmagDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(xmagDataLabel);

        JLabel yaccLabel = new JLabel("yacc");
        yaccLabel.setBounds(303, 63, 100, 15);
        yaccLabel.setBackground(Color.WHITE);
        yaccLabel.setOpaque(true);
        yaccLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(yaccLabel);

        yaccDataLabel = new JLabel("");
        yaccDataLabel.setBounds(303, 79, 100, 15);
        yaccDataLabel.setForeground(Color.GREEN);
        yaccDataLabel.setBackground(Color.BLACK);
        yaccDataLabel.setOpaque(true);
        yaccDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(yaccDataLabel);

        JLabel ygyroLabel = new JLabel("ygyro");
        ygyroLabel.setBounds(404, 63, 100, 15);
        ygyroLabel.setBackground(Color.WHITE);
        ygyroLabel.setOpaque(true);
        ygyroLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(ygyroLabel);

        ygyroDataLabel = new JLabel("");
        ygyroDataLabel.setBounds(404, 79, 100, 15);
        ygyroDataLabel.setForeground(Color.GREEN);
        ygyroDataLabel.setBackground(Color.BLACK);
        ygyroDataLabel.setOpaque(true);
        ygyroDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(ygyroDataLabel);

        JLabel ymagLabel = new JLabel("ymag");
        ymagLabel.setBounds(0, 95, 100, 15);
        ymagLabel.setBackground(Color.WHITE);
        ymagLabel.setOpaque(true);
        ymagLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(ymagLabel);

        ymagDataLabel = new JLabel("");
        ymagDataLabel.setBounds(0, 111, 100, 15);
        ymagDataLabel.setForeground(Color.GREEN);
        ymagDataLabel.setBackground(Color.BLACK);
        ymagDataLabel.setOpaque(true);
        ymagDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(ymagDataLabel);

        JLabel zaccLabel = new JLabel("zacc");
        zaccLabel.setBounds(101, 95, 100, 15);
        zaccLabel.setBackground(Color.WHITE);
        zaccLabel.setOpaque(true);
        zaccLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(zaccLabel);

        zaccDataLabel = new JLabel("");
        zaccDataLabel.setBounds(101, 111, 100, 15);
        zaccDataLabel.setForeground(Color.GREEN);
        zaccDataLabel.setBackground(Color.BLACK);
        zaccDataLabel.setOpaque(true);
        zaccDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(zaccDataLabel);

        JLabel zgyroLabel = new JLabel("zgyro");
        zgyroLabel.setBounds(202, 95, 100, 15);
        zgyroLabel.setBackground(Color.WHITE);
        zgyroLabel.setOpaque(true);
        zgyroLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(zgyroLabel);

        zgyroDataLabel = new JLabel("");
        zgyroDataLabel.setBounds(202, 111, 100, 15);
        zgyroDataLabel.setForeground(Color.GREEN);
        zgyroDataLabel.setBackground(Color.BLACK);
        zgyroDataLabel.setOpaque(true);
        zgyroDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(zgyroDataLabel);

        JLabel zmagLabel = new JLabel("zmag");
        zmagLabel.setBounds(303, 95, 100, 15);
        zmagLabel.setBackground(Color.WHITE);
        zmagLabel.setOpaque(true);
        zmagLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(zmagLabel);

        zmagDataLabel = new JLabel("");
        zmagDataLabel.setBounds(303, 111, 100, 15);
        zmagDataLabel.setForeground(Color.GREEN);
        zmagDataLabel.setBackground(Color.BLACK);
        zmagDataLabel.setOpaque(true);
        zmagDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(zmagDataLabel);

        JLabel timeUsecLabel = new JLabel("Time Usec");
        timeUsecLabel.setBounds(404, 95, 100, 15);
        timeUsecLabel.setBackground(Color.WHITE);
        timeUsecLabel.setOpaque(true);
        timeUsecLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(timeUsecLabel);

        timeUsecDataLabel = new JLabel("");
        timeUsecDataLabel.setBounds(404, 111, 100, 15);
        timeUsecDataLabel.setForeground(Color.GREEN);
        timeUsecDataLabel.setBackground(Color.BLACK);
        timeUsecDataLabel.setOpaque(true);
        timeUsecDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(timeUsecDataLabel);

        JLabel modeLabel = new JLabel("mode");
        modeLabel.setBounds(0, 127, 100, 15);
        modeLabel.setBackground(Color.WHITE);
        modeLabel.setOpaque(true);
        modeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(modeLabel);

        modeDataLabel = new JLabel("");
        modeDataLabel.setBounds(0, 143, 100, 15);
        modeDataLabel.setForeground(Color.GREEN);
        modeDataLabel.setBackground(Color.BLACK);
        modeDataLabel.setOpaque(true);
        modeDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(modeDataLabel);

        JLabel armedLabel = new JLabel("armed");
        armedLabel.setBounds(101, 127, 100, 15);
        armedLabel.setBackground(Color.WHITE);
        armedLabel.setOpaque(true);
        armedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(armedLabel);

        armedDataLabel = new JLabel("");
        armedDataLabel.setBounds(101, 143, 100, 15);
        armedDataLabel.setForeground(Color.GREEN);
        armedDataLabel.setBackground(Color.BLACK);
        armedDataLabel.setOpaque(true);
        armedDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(armedDataLabel);

        JLabel guidedLabel = new JLabel("guided");
        guidedLabel.setBounds(202, 127, 100, 15);
        guidedLabel.setBackground(Color.WHITE);
        guidedLabel.setOpaque(true);
        guidedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(guidedLabel);

        guidedDataLabel = new JLabel("");
        guidedDataLabel.setBounds(202, 143, 100, 15);
        guidedDataLabel.setForeground(Color.GREEN);
        guidedDataLabel.setBackground(Color.BLACK);
        guidedDataLabel.setOpaque(true);
        guidedDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(guidedDataLabel);

        JLabel airSpeedLabel = new JLabel("Air Speed");
        airSpeedLabel.setBounds(303, 127, 100, 15);
        airSpeedLabel.setBackground(Color.WHITE);
        airSpeedLabel.setOpaque(true);
        airSpeedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(airSpeedLabel);

        airSpeedDataLabel = new JLabel("");
        airSpeedDataLabel.setBounds(303, 143, 100, 15);
        airSpeedDataLabel.setForeground(Color.GREEN);
        airSpeedDataLabel.setBackground(Color.BLACK);
        airSpeedDataLabel.setOpaque(true);
        airSpeedDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(airSpeedDataLabel);

        JLabel altitudeLabel = new JLabel("Altitude");
        altitudeLabel.setBounds(404, 127, 100, 15);
        altitudeLabel.setBackground(Color.WHITE);
        altitudeLabel.setOpaque(true);
        altitudeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(altitudeLabel);

        altDataLabel = new JLabel("");
        altDataLabel.setBounds(404, 143, 100, 15);
        altDataLabel.setForeground(Color.GREEN);
        altDataLabel.setBackground(Color.BLACK);
        altDataLabel.setOpaque(true);
        altDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(altDataLabel);

        JLabel climbLabel = new JLabel("Climb");
        climbLabel.setBounds(0, 159, 100, 15);
        climbLabel.setBackground(Color.WHITE);
        climbLabel.setOpaque(true);
        climbLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(climbLabel);

        climbDataLabel = new JLabel("");
        climbDataLabel.setBounds(0, 175, 100, 15);
        climbDataLabel.setForeground(Color.GREEN);
        climbDataLabel.setBackground(Color.BLACK);
        climbDataLabel.setOpaque(true);
        climbDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(climbDataLabel);

        JLabel groundSpeedLabel = new JLabel("GRNDSpeed");
        groundSpeedLabel.setBounds(101, 159, 100, 15);
        groundSpeedLabel.setBackground(Color.WHITE);
        groundSpeedLabel.setOpaque(true);
        groundSpeedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(groundSpeedLabel);

        groundSpeedDataLabel = new JLabel("");
        groundSpeedDataLabel.setBounds(101, 175, 100, 15);
        groundSpeedDataLabel.setForeground(Color.GREEN);
        groundSpeedDataLabel.setBackground(Color.BLACK);
        groundSpeedDataLabel.setOpaque(true);
        groundSpeedDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(groundSpeedDataLabel);

        JLabel headingLabel = new JLabel("Heading");
        headingLabel.setBounds(202, 159, 100, 15);
        headingLabel.setBackground(Color.WHITE);
        headingLabel.setOpaque(true);
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(headingLabel);

        headingDataLabel = new JLabel("");
        headingDataLabel.setBounds(202, 175, 100, 15);
        headingDataLabel.setForeground(Color.GREEN);
        headingDataLabel.setBackground(Color.BLACK);
        headingDataLabel.setOpaque(true);
        headingDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(headingDataLabel);

        JLabel throttleLabel = new JLabel("Throttle");
        throttleLabel.setBounds(303, 159, 100, 15);
        throttleLabel.setBackground(Color.WHITE);
        throttleLabel.setOpaque(true);
        throttleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(throttleLabel);

        throttleDataLabel = new JLabel("");
        throttleDataLabel.setBounds(303, 175, 100, 15);
        throttleDataLabel.setForeground(Color.GREEN);
        throttleDataLabel.setBackground(Color.BLACK);
        throttleDataLabel.setOpaque(true);
        throttleDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(throttleDataLabel);
    }

    public void updateAttitudeData(float pitch, float pitchSpeed, float roll, float rollSpeed, float yaw, float yawSpeed)
    {
        pitchDataLabel.setText(((Float)pitch).toString());
        pitchSpeedDataLabel.setText(((Float)pitchSpeed).toString());
        rollDataLabel.setText(((Float)roll).toString());
        rollSpeedDataLabel.setText(((Float)rollSpeed).toString());
        yawDataLabel.setText(((Float)yaw).toString());
        yawSpeedDataLabel.setText(((Float)yawSpeed).toString());
    }

    public void updateControlData(float pitch, float roll, float thrust, float yaw)
    {
        pitchControlDataLabel.setText(((Float)pitch).toString());
        rollControlDataLabel.setText(((Float)roll).toString());
        yawControlDataLabel.setText(((Float)yaw).toString());
        thrustDataLabel.setText(((Float)thrust).toString());
    }

    public void updateMRIData(long timeUsec, int xacc, int xgyro, int xmag,
                              int yacc, int ygyro, int ymag, int zacc, int zgyro, int zmag)
    {
        xaccDataLabel.setText(((Integer)xacc).toString());
        xgyroDataLabel.setText(((Integer)xgyro).toString());
        xmagDataLabel.setText(((Integer)xmag).toString());
        yaccDataLabel.setText(((Integer)yacc).toString());
        ygyroDataLabel.setText(((Integer)ygyro).toString());
        ymagDataLabel.setText(((Integer)ymag).toString());
        zaccDataLabel.setText(((Integer)zacc).toString());
        zgyroDataLabel.setText(((Integer)zgyro).toString());
        zmagDataLabel.setText(((Integer)zmag).toString());
        timeUsecDataLabel.setText(((Long)timeUsec).toString());
    }

    public void updateStateData(String mode, boolean armed, boolean guided)
    {
        modeDataLabel.setText(mode);
        armedDataLabel.setText(((Boolean)armed).toString());
        guidedDataLabel.setText(((Boolean)guided).toString());
    }

    public void updateVHData(float airSpeed, float alt, float climb, float groundSpeed, short heading, short throttle)
    {
        airSpeedDataLabel.setText(((Float)airSpeed).toString());
        altDataLabel.setText(((Float)alt).toString());
        climbDataLabel.setText(((Float)climb).toString());
        groundSpeedDataLabel.setText(((Float)groundSpeed).toString());
        headingDataLabel.setText(((Short)heading).toString());
        throttleDataLabel.setText(((Short)throttle).toString());
    }
}
