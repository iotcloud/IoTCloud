package cgl.iotcloud.streaming.http.server;

import java.util.ArrayList;
import java.util.List;

public class RoutingRule {
    private String incomingUrl;

    private List<HttpClientEndpoint> targetEndpoints = new ArrayList<HttpClientEndpoint>();

    public RoutingRule(String incomingUrl) {
        this.incomingUrl = incomingUrl;
    }

    public RoutingRule(String incomingUrl, List<HttpClientEndpoint> targetEprs) {
        this.incomingUrl = incomingUrl;
        this.targetEndpoints = targetEprs;
    }

    public boolean isMatch(String url) {
        return url == null || url.matches(incomingUrl);
    }

    public List<HttpClientEndpoint> getTargetEndpoints() {
        return targetEndpoints;
    }

    public void addTargetEndpoint(HttpClientEndpoint epr) {
        targetEndpoints.add(epr);
    }

    public void addTargetEndpoints(List<HttpClientEndpoint> eprs) {
        targetEndpoints.addAll(eprs);
    }

    public HttpClientEndpoint getEndpoint() {
        return targetEndpoints.get(0);
    }
}
