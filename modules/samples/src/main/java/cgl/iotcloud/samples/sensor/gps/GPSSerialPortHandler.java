package cgl.iotcloud.samples.sensor.gps;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

public class GPSSerialPortHandler implements SerialPortEventListener,GPSController{

	private static GPSSerialPortHandler serialPortHandler;
	private Map<String,List<GPSReciever>> portReceiversMap;
	private Map<String,SerialPort> portNameMap;
	
	/**
	 * Opens a serial port.
	 * @return returns true a port is opened successfully.
	 */
	
	private GPSSerialPortHandler(){
		portReceiversMap = new HashMap<String,List<GPSReciever>>();
		portNameMap = new HashMap<String,SerialPort>();
	}
	
	public static GPSSerialPortHandler getInstance(){
		if(serialPortHandler == null)
			serialPortHandler =  new GPSSerialPortHandler();
		return serialPortHandler;
	}
	
	public boolean openPort(String port,int speed){
		Boolean portOpen = true;
		SerialPort serialPort = null;
	
		try{
			CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(port);
			serialPort = (SerialPort) portId.open("Serial Port " + port, 2000);

			try{
				serialPort.setSerialPortParams(speed,SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);
			} catch (UnsupportedCommOperationException e) {
				System.out.println("Failed to set Comm port params : "+e.getMessage());
				portOpen = false;
			}

			try {
				serialPort.addEventListener(this);
				serialPort.notifyOnDataAvailable(true);
			} catch (TooManyListenersException e) {
				System.out.println("Couldn't add serial I/O listener");
				portOpen = false;
			}

		}catch(Exception e){
			System.out.println("Failed to open port "+e.getMessage());
			portOpen = false;
		}
		
		if(portOpen && serialPort != null)
			portNameMap.put(serialPort.getName(),serialPort);
		
		return portOpen;
	}

	/**
	 * Closes the port
	 *  
	 */
	public synchronized void closePort(String portName){
		
		if(portNameMap != null && portNameMap.containsKey(portName)) {
			List<GPSReciever> recievers = (List<GPSReciever>)portReceiversMap.get(portName);
			
			if(recievers.size() == 0){
				SerialPort port = portNameMap.get(portName);
				portNameMap.remove(portName);
				port.close();
			}else{
				System.out.println("Still recievers for this port");
			}
		}
	}


	public boolean isPortOpen(String portName){
		if(portNameMap.containsKey(portName)){
			return true;
		}
		return false;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		SerialPort port = (SerialPort)event.getSource();
		
		if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try{
				InputStream in = port.getInputStream();
				int data;
				while((data = in.read()) != -1) {
					notifyRecievers(data,port.getName());
				}
			}catch(Exception e){
				System.out.println("Failed to get data from serial port");
			}
		}
	}

	@Override
	public void addReciever(GPSReciever reciever,String portName) {
		if(portReceiversMap.containsKey(portName)){
			List<GPSReciever> recievers = portReceiversMap.get(portName);
			recievers.add(reciever);
		}else{
			List<GPSReciever> recievers = new ArrayList<GPSReciever>();
			recievers.add(reciever);
			portReceiversMap.put(portName, recievers);
		}
	}


	@Override
	public synchronized void notifyRecievers(int data,String portName) {
		if(portReceiversMap.containsKey(portName)){
			List<GPSReciever> recievers = portReceiversMap.get(portName);
			for(GPSReciever reciever :recievers){
				reciever.onData(data,portName);
			}
		}
	}

	public List<String> getPorts() {
		List<String> portsAvailable = new ArrayList<String>();
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();

			while (portList.hasMoreElements()) {
				CommPortIdentifier portId = (CommPortIdentifier) portList
						.nextElement();

				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					portsAvailable.add(portId.getName());
				}
			}
			return portsAvailable;
	}

	@Override
	public void removeReciever(GPSReciever reciever, String port) {
		if(portReceiversMap != null && portReceiversMap.containsKey(port)){
			List<GPSReciever> recievers = portReceiversMap.get(port);
			recievers.remove(reciever);
			closePort(port);
		}	
	}
}
