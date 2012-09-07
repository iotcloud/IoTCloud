package cgl.iotcloud.samples.sensor.gps;

public interface GPSController {
	void addReciever(GPSReciever reciever,String port);
	void removeReciever(GPSReciever reciever,String port);
}
