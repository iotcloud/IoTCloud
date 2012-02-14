package cgl.iotcloud.streaming.http.server;


public class Worker implements Runnable {
    private MessageContext context;

    public Worker(MessageContext context) {
        this.context = context;
    }

    public void run() {
        // connect to all the endpoints in the rule and start streaming
        context.getInChannel().setAttachment(context);
        HttpClientEndpoint endpoint = context.getRule().getEndpoint();

        endpoint.connect(context);

        // endpoint.send();
    }
}
