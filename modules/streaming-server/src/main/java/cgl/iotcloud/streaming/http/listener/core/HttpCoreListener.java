package cgl.iotcloud.streaming.http.listener.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cgl.iotcloud.streaming.http.listener.MessageReceiver;
import cgl.iotcloud.streaming.http.server.CustomThreadFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.nio.DefaultNHttpServerConnection;
import org.apache.http.impl.nio.DefaultNHttpServerConnectionFactory;
import org.apache.http.impl.nio.DefaultHttpServerIODispatch;
import org.apache.http.impl.nio.reactor.DefaultListeningIOReactor;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.NHttpConnectionFactory;
import org.apache.http.nio.NHttpServerConnection;
import org.apache.http.nio.entity.ContentInputStream;
import org.apache.http.nio.protocol.AbstractAsyncRequestConsumer;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.nio.protocol.HttpAsyncRequestHandlerRegistry;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncService;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.ListeningIOReactor;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.nio.util.SharedInputBuffer;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP/1.1 file server based on the non-blocking I/O model and capable of direct channel
 * (zero copy) data transfer.
 */
public class HttpCoreListener {
    private Logger log = LoggerFactory.getLogger(HttpCoreListener.class);

    private int port;

    private MessageReceiver receiver;

    private String path;

    private ListeningIOReactor ioReactor;

    private ThreadPoolExecutor executor = null;

    public HttpCoreListener(int port, MessageReceiver receiver, String path) {
        this.port = port;
        this.receiver = receiver;
        this.path = path;
    }

    public void start() throws Exception {
        executor = new ThreadPoolExecutor(20, 100, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new CustomThreadFactory(new ThreadGroup("listener-io"), "listener-io-thread"));

        // HTTP parameters for the server
        HttpParams params = new SyncBasicHttpParams();
        params
                .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 30000)
                .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpTest/1.1");
        // Create HTTP protocol processing chain
        HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpResponseInterceptor[]{
                // Use standard server-side protocol interceptors
                new ResponseDate(),
                new ResponseServer(),
                new ResponseContent(),
                new ResponseConnControl()
        });
        // Create request handler registry
        HttpAsyncRequestHandlerRegistry reqistry = new HttpAsyncRequestHandlerRegistry();
        // Register the default handler for all URIs
        reqistry.register("*", new StreamHandler(receiver));
        // Create server-side HTTP protocol handler
        HttpAsyncService protocolHandler = new HttpAsyncService(
                httpproc, new DefaultConnectionReuseStrategy(), reqistry, params) {
            @Override
            public void connected(final NHttpServerConnection conn) {
                log.debug(conn + ": connection open");
                super.connected(conn);
            }

            @Override
            public void closed(final NHttpServerConnection conn) {
                log.debug(conn + ": connection closed");
                super.closed(conn);
            }

        };
        // Create HTTP connection factory
        NHttpConnectionFactory<DefaultNHttpServerConnection> connFactory;
        connFactory = new DefaultNHttpServerConnectionFactory(params);

        // Create server-side I/O event dispatch
        final IOEventDispatch ioEventDispatch = new DefaultHttpServerIODispatch(protocolHandler, connFactory);
        // Create server-side I/O reactor
        ioReactor = new DefaultListeningIOReactor();

        // Listen of the given port
        ioReactor.listen(new InetSocketAddress(port));
        // Ready to go!
        new Thread(new Runnable() {
            public void run() {
                try {
                    log.info("Starting the io reactor at port: " + port);
                    ioReactor.execute(ioEventDispatch);
                } catch (IOException e) {
                    log.error("Exception occurred while starting the listener", e);
                }
            }
        }).start();
    }

    public void stop() {
        log.info("Shutdown the HTTP Listener");
        try {
            ioReactor.shutdown(5000);
        } catch (IOException e) {
            log.error("Exception occurred while shutting down the server");
        }

    }

    private class StreamHandler implements HttpAsyncRequestHandler<HttpRequest> {
        private final MessageReceiver receiver;

        public StreamHandler(MessageReceiver receiver) {
            super();
            this.receiver = receiver;
        }

        public HttpAsyncRequestConsumer<HttpRequest> processRequest(
                final HttpRequest request,
                final HttpContext context) {
            // Buffer request content in memory for simplicity
            return new AsyncStreamHandler(receiver);
        }

        public void handle(
                final HttpRequest request,
                final HttpAsyncExchange httpexchange,
                final HttpContext context) throws HttpException, IOException {
            HttpResponse response = httpexchange.getResponse();
            handleInternal(request, response);
            httpexchange.submitResponse(new BasicAsyncResponseProducer(response));
        }

        private void handleInternal(
                final HttpRequest request,
                final HttpResponse response) throws HttpException, IOException {
            if (request != null) {
                String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
                if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
                    throw new MethodNotSupportedException(method + " method not supported");
                }
            }
            response.setStatusCode(HttpStatus.SC_ACCEPTED);

            response.setEntity(null);
        }
    }

    private  class AsyncStreamHandler extends AbstractAsyncRequestConsumer<HttpRequest> {
        private volatile HttpRequest request;

        private ContentInputStream is;

        private volatile SharedInputBuffer inputBuffer;

        private MessageReceiver receiver;

        private AsyncStreamHandler(MessageReceiver receiver) {
            this.receiver = receiver;
        }

        @Override
        protected void onRequestReceived(HttpRequest httpRequest) throws HttpException, IOException {
            if (request instanceof HttpEntityEnclosingRequest) {
                request = httpRequest;
            }
        }

        @Override
        protected void onEntityEnclosed(HttpEntity httpEntity,
                                        ContentType contentType) throws IOException {
        }

        @Override
        protected void onContentReceived(ContentDecoder contentDecoder,
                                         IOControl ioControl) throws IOException {
            if (this.inputBuffer == null) {
                synchronized (this) {
                    if (this.inputBuffer == null) {
                        inputBuffer = new SharedInputBuffer(8 * 2048, ioControl,
                                new HeapByteBufferAllocator());
                        is = new ContentInputStream(inputBuffer);
                        executor.execute(new Runnable() {
                            public void run() {
                                receiver.messageReceived(is);
                            }
                        });
                    }
                }
            }
            this.inputBuffer.consumeContent(contentDecoder);
        }

        @Override
        protected HttpRequest buildResult(HttpContext httpContext) throws Exception {
            return request;
        }

        @Override
        protected void releaseResources() {
            inputBuffer.close();
        }
    }

    public static void main(String[] args) {
        HttpCoreListener listener = new HttpCoreListener(5000, new MessageReceiver() {
            public void messageReceived(InputStream in) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                File file = new File("Test2.mp3");
                BufferedWriter writer;
                try {
                    file.createNewFile();
                    writer = new BufferedWriter(new FileWriter(file));
                } catch (IOException e) {
                    System.out.println("Error occurred" + e);
                    return;
                }
                int i;
                int count = 0;
                char r[] = new char[1];
                try {
                    i = reader.read(r);
                    while (i != -1) {
                        count++;
                        writer.write(r);
                        i =  reader.read(r);
                    }
                    writer.close();
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("count :" + count);
            }
        }, "ab");
        try {
            listener.start();
            Thread.sleep(100000000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

