package cgl.iotcloud.core.config;

public class ContentRepositoryConstants {

	public static final String SENSOR_NODE = "SENSOR_NODE";
	
	public static final String CLIENT_NODE = "CLIENT_NODE";
	
	public static final String PUBLIC_END_POINT = "PUBLIC_END_POINT";
	
	public static final String PUBLIC_END_POINT_KEY_NODE = "PUBLIC_END_POINT_KEY_NODE";
	
	public static enum SENSOR_PROPERTIES {ID, TYPE, DATA_END_POINT_NODE, DATA_END_POINT_NODE_KEY, CNTRL_END_POINT_NODE, CNTRL_END_POINT_KEY_NODE, UPDATE_SENDING_END_POINT_NODE, UPDATE_SENDING_END_POINT_KEY_NODE}
	
	public static enum CLIENT_PROPERTIES {CLIENT_ID, SENSOR_ID, DATA_END_POINT_NODE, END_POINT, STREAMING_END_POINT, JMS_END_POINT}
	
	public static enum PUBLIC_END_POINT_PROPERTIES {public_end_point_address}
	
}
