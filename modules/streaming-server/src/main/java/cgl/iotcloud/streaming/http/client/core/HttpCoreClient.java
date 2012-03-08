package cgl.iotcloud.streaming.http.client.core;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.nio.DefaultHttpClientIODispatch;
import org.apache.http.impl.nio.pool.BasicNIOConnPool;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestExecutor;
import org.apache.http.nio.protocol.HttpAsyncRequester;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

public class HttpCoreClient {
    private static Logger log = LoggerFactory.getLogger(HttpCoreClient.class);

    private String host;

    private int port;

    private String path;

    private ConnectingIOReactor ioReactor;

    private HttpProcessor httpproc;

    private HttpParams params;

    private BasicNIOConnPool pool;

    public HttpCoreClient(String host, int port, String path) {
        this.host = host;
        this.port = port;
        this.path = path;
    }

    public void init() throws IOReactorException {
        // HTTP parameters for the client
        params = new SyncBasicHttpParams();
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 3000)
                .setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000)
                .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                .setParameter(CoreProtocolPNames.USER_AGENT, "iotcloud");
        // Create HTTP protocol processing chain
        httpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[]{
                // Use standard client-side protocol interceptors
                new RequestContent(),
                new RequestTargetHost(),
                new RequestConnControl(),
                new RequestUserAgent(),
                new RequestExpectContinue()});
        // Create client-side HTTP protocol handler
        HttpAsyncRequestExecutor protocolHandler = new HttpAsyncRequestExecutor();
        // Create client-side I/O event dispatch
        final IOEventDispatch ioEventDispatch = new DefaultHttpClientIODispatch(protocolHandler, params);
        // Create client-side I/O reactor
        IOReactorConfig config = new IOReactorConfig();
        config.setIoThreadCount(1);
        try {
            ioReactor = new DefaultConnectingIOReactor(config);
        } catch (IOReactorException e) {
            log.error("Error occurred while starting the IO Reactor", e);
            throw e;
        }
        // Create HTTP connection pool
        pool = new BasicNIOConnPool(ioReactor, params);
        // Limit total number of connections to just two
        pool.setDefaultMaxPerRoute(2);
        pool.setMaxTotal(2);
        // Run the I/O reactor in a separate thread
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    // Ready to go!
                    ioReactor.execute(ioEventDispatch);
                } catch (InterruptedIOException ex) {
                    System.err.println("Interrupted");
                } catch (IOException e) {
                    System.err.println("I/O error: " + e.getMessage());
                }
                System.out.println("Shutdown");
            }

        });
        // Start the client thread
        t.start();
    }

    public void send(InputStream inputStream, final SendCallBack callBack) throws Exception {
        // Create HTTP requester
        HttpAsyncRequester requester = new HttpAsyncRequester(
                httpproc, new DefaultConnectionReuseStrategy(), params);
        // Execute HTTP GETs to the following hosts and
        final HttpHost target =
                new HttpHost(host, port, "http");

        BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST", path);
        request.setEntity(new InputStreamEntity(inputStream, -1));
        requester.execute(
                new BasicAsyncRequestProducer(target, request),
                new BasicAsyncResponseConsumer(),
                pool,
                new BasicHttpContext(),
                // Handle HTTP response from a callback
                new FutureCallback<HttpResponse>() {
                    public void completed(final HttpResponse response) {
                        System.out.println(target + "->" + response.getStatusLine());
                        callBack.completed();
                    }
                    public void failed(final Exception ex) {
                        System.out.println(target + "->" + ex);
                        callBack.failed(ex);
                    }
                    public void cancelled() {
                        System.out.println(target + " cancelled");
                        callBack.cancelled();
                    }
                });
    }

    public interface SendCallBack {
        void completed();
        void failed(Exception e);
        void cancelled();
    }

    public void destroy() throws IOException {
        try {
            ioReactor.shutdown();
        } catch (IOException e) {
            log.error("Error occurred while shutting down the client..", e);
            throw e;
        }
    }

    public static void main(String[] args) {
        HttpCoreClient client = new HttpCoreClient("localhost", 5050, "/");
        try {
            client.init();

            File f = new File("Test.txt");
            FileInputStream file = new FileInputStream(f);

            client.send(file, new DefaultCallback());

            Thread.sleep(50000);
        } catch (IOReactorException e) {
            log.error("Error occurred", e);
            e.printStackTrace();
        } catch (Exception e) {
            log.error("Error occurred", e);
            e.printStackTrace();
        }
    }

    public static class DefaultCallback implements SendCallBack {
        public void completed() {

        }

        public void failed(Exception e) {

        }

        public void cancelled() {

        }
    }
}
