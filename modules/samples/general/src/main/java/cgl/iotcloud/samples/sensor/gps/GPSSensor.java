package cgl.iotcloud.samples.sensor.gps;

import gnu.io.SerialPort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.control.DefaultControlMessage;
import cgl.iotcloud.core.sensor.AbstractSensor;
import cgl.iotcloud.sensors.SensorAdaptor;

public class GPSSensor extends AbstractSensor implements GPSReciever{
	private int baudRate;
	private String port;
	private String gpsData;
	private Boolean run = true;
	private GPSSerialPortHandler serialPortHandler;
	private static int DATA_END = 13;
	private SerialPort serialPort = null;

	//TODO : Add WatchDog, Virtual GPS sensor.
	public GPSSensor(String type,String name) { 
		super(type, name);

		SensorAdaptor adaptor = new SensorAdaptor("http://localhost:8080");
		adaptor.registerSensor(this);
		adaptor.start();

		serialPortHandler = GPSSerialPortHandler.getInstance();
		getUserInput();

		//port set to null when user input is not valid.
		if(port != null){
			if((serialPort = serialPortHandler.openPort(port, baudRate))!= null){
				System.out.println("port name is "+serialPort.getName());
				serialPortHandler.addReciever(this,serialPort.getName());

				while (run) {
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				adaptor.stop();
			}else{
				if(serialPortHandler.isPortOpen(port)){
					serialPort = serialPortHandler.getPortByName(port);
					serialPortHandler.addReciever(this,serialPort.getName());
				}else{
					adaptor.stop();
					System.out.println("Failed to open comm port :"+port+"for GPS communication");
				}
			}
		}
	}

	private void getUserInput() {
		System.out.println("Adding a GPS sensor");
		List<String> ports = serialPortHandler.getPorts();

		System.out.println("Serial Ports Available :");
		for(String port :ports){
			System.out.println(port);
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Select a port   :");
		try{
			port =  in.readLine();

			if(!ports.contains(port))
				port = null;
		}catch(IOException e){
			port = null;
			e.printStackTrace();
		}

		System.out.println("Select Baud Rate :");
		try{
			baudRate =  Integer.parseInt(in.readLine());
		}catch(Exception e){
			port = null;
			e.printStackTrace();
		}
	}

	@Override
	public void onControlMessage(SensorMessage message) {
		if (message instanceof DefaultControlMessage) {
			Object command = ((DefaultControlMessage) message).getControl("action");

			if(command != null && command.equals("stop")){
				serialPortHandler.removeReciever(this,port);
				run = false;
			}
		}   
	}

	@Override
	public void onData(String data,String port) {

		List<String> parsedData = GPSEventParser.parseData(data);

		if (parsedData.size() == 0) {
			System.out.println("Parse Error: No data after parsing");
		} else {
			String msgType = (String) parsedData.get(0);
			if (msgType.equals("$GPRMC")) {
					GpsData gpsData = getGPSData(parsedData);
					sendMessage(gpsData);
			}
		}
	}


	private GpsData getGPSData(List<String> parsedData){
		try {
			String lats = (String) parsedData.get(3);
			lats = Double.toString(Integer.parseInt(lats
					.substring(0, 2))
					+ Double.parseDouble(lats.substring(2))
					/ 60.0);

			double lat = Double.parseDouble(lats);

			if (((String) parsedData.get(4)).equalsIgnoreCase("S")) {
				lat = -lat;
			} else if (!((String) parsedData.get(4))
					.equalsIgnoreCase("N")) {
				throw new NumberFormatException();
			}

			String lngs = (String) parsedData.get(5);
			lngs = Double.toString(Integer.parseInt(lngs
					.substring(0, 3))
					+ Double.parseDouble(lngs.substring(3))
					/ 60.0);

			double lon = Double.parseDouble(lngs);
			
			if (((String) parsedData.get(6)).equalsIgnoreCase("W")) {
				lon = -lon;
			} else if (!((String) parsedData.get(6))
					.equalsIgnoreCase("E")) {
				throw new NumberFormatException();
			}

			System.out.println("longitude  is "+lon+" ,latitude is :"+lat);
			GpsData gpsData = new GpsData(
					System.currentTimeMillis(), lat, lon);
			return gpsData;

		} catch (NumberFormatException e) {
			System.out.println("GPS device may not be configured properly ...");
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		GPSSensor sensor = new GPSSensor(Constants.SENSOR_TYPE_BLOCK, "gps-sensor");
	}
}
