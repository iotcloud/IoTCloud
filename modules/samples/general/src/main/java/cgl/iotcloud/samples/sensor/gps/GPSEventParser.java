package cgl.iotcloud.samples.sensor.gps;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class GPSEventParser {

	//TODO: Handle error cases.
	public static List<String> parseData(String gpsData) {
		List<String> dataFields = new ArrayList<String>();
		String token;
		StringTokenizer tokenizer = new StringTokenizer(gpsData,",");

		while(tokenizer.hasMoreElements()) {
			token = tokenizer.nextToken();
			dataFields.add(token.trim());
		}
		return dataFields;
	}

}
