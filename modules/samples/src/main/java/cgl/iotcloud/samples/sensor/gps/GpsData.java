package cgl.iotcloud.samples.sensor.gps;

import cgl.iotcloud.core.message.data.ObjectDataMessage;

public class GpsData extends ObjectDataMessage {
    private static final long serialVersionUID = 1L;

    private double lat;

    private double lng;

    public GpsData() {
        super(System.currentTimeMillis());
    }

    public GpsData(long timestamp, double lat, double lng) {
        super(timestamp);
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }


    public double getLng() {
        return lng;
    }

    public String getLatString() {
        return String.format("%8.5f", lat);
    }

    public String getLngString() {
        return String.format("%8.5f", lng);
    }
}

