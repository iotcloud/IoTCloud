package cgl.iotcloud.core.config.xml;

import cgl.iotcloud.core.IOTRuntimeException;
import cgl.iotcloud.core.broker.Broker;
import cgl.iotcloud.core.stream.StreamingServer;
import com.iotcloud.xsd.BrokerConfigDocument;
import com.iotcloud.xsd.Param;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class StreamingServerFactory {
    private Logger log = LoggerFactory.getLogger(StreamingServerFactory.class);

    public StreamingServer create(String filePath) {
        log.info("Creating Streaming Server Configuration using file: " + filePath);
        Broker broker = new Broker();

        File brokerFile = new File(filePath);
        BrokerConfigDocument document;
        try {
            document = BrokerConfigDocument.Factory.parse(brokerFile);
        } catch (XmlException e) {
            handleException("Error parsing the broker configuration xml file: " + filePath, e);
            return null;
        } catch (IOException e) {
            handleException("IO error reading the file: " + filePath, e);
            return null;
        }

        BrokerConfigDocument.BrokerConfig bc = document.getBrokerConfig();

        com.iotcloud.xsd.StreamingServer b = bc.getStreamingServer();
        Param fac[] = b.getParamArray();

        int port = 7888;
        String path = "*";

        HashMap<String, String> params = new HashMap<String, String>();
        for (Param f : fac) {
            params.put(f.getName(), f.getStringValue());
            if (f.getName().equals("PORT")) {
                port = Integer.parseInt(f.getStringValue());
            }

            if (f.getName().equals("PATH")) {
                path = f.getStringValue();
            }
        }

        return new StreamingServer(path, port, params);
    }

    private void handleException(String s, Exception e) {
        log.error(s, e);
        throw new IOTRuntimeException(s, e);
    }
}
