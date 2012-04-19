package cgl.iotcloud.streaming.http.server;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Handles the routing of messages to an endpoint group
 */
public class MultiEndpoint {
    private ArrayList<HttpClientEndpoint> endpoints = new ArrayList<HttpClientEndpoint>();

    private LinkedList<LinkedList> messages = new LinkedList<LinkedList>();


}
