package cgl.iotcloud.core.thrift;

import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.IoTCloud;
import cgl.iotcloud.thrift.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

public class ThriftServer {
    private Log log = LogFactory.getLog(ThriftServer.class);

    private int port = 9090;

    private IoTCloud ioTCloud = null;

    private TServer server;

    public ThriftServer(IoTCloud ioTCloud) {
        this.ioTCloud = ioTCloud;
    }

    public ThriftServer(int port, IoTCloud ioTCloud) {
        this.port = port;
        this.ioTCloud = ioTCloud;
    }

    public void start() throws IOTException {
        try {
            TServerTransport serverTransport = new TServerSocket(port);
            server = new TSimpleServer(
                    new TServer.Args(serverTransport).processor(
                            new NodeService.Processor <NodeServiceHandler>(new NodeServiceHandler(ioTCloud))));
            server.serve();
        } catch (TTransportException e) {
            String msg = "Error starting the Thrift server";
            log.error(msg);
            throw new IOTException(msg);
        }
    }

    public void stop() {
        server.stop();
    }

}
