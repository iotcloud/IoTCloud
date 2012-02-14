package cgl.iotcloud.core.jetty;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.IoTCloud;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.deployment.FileSystemConfigurator;
import org.apache.axis2.transport.http.AxisServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;

public class SGCHTTPServer {
    private static Logger log = LoggerFactory.getLogger(SGCHTTPServer.class);

    private String jettyConfigFile = Constants.JETTY_CONFIG_FILE;

    private IoTCloud iotCloud = null;

    public SGCHTTPServer(IoTCloud iotCloud) {
        this(iotCloud, Constants.JETTY_CONFIG_FILE);
    }

    public SGCHTTPServer(IoTCloud iotCloud, String configurationFile) {
        this.jettyConfigFile = configurationFile;
        this.iotCloud = iotCloud;
    }

    public void start() {
        try {
            File file = new File(jettyConfigFile);
            Resource serverXML = Resource.newResource(file);
            XmlConfiguration configuration = new XmlConfiguration(serverXML.getInputStream());

            Server server = (Server) configuration.configure();

            ServletContextHandler servletContextHandler = new ServletContextHandler(server,
                    Constants.CONTEXT_PATH, true, false);
            ServletContext context = servletContextHandler.getServletContext();
            context.setAttribute(AxisServlet.CONFIGURATION_CONTEXT, initConfigContext());

            servletContextHandler.addServlet(AxisServlet.class, Constants.SERVICES_PATH);

            server.start();
            server.join();
        } catch (IOException e) {
            handleException("Failed to create the HTTP Configuration from the file: " +
                    jettyConfigFile, e);
        } catch (SAXException e) {
            handleException("Failed to create the HTTP Configuration from the file: " +
                    jettyConfigFile, e);
        } catch (InterruptedException e) {
            handleException("HTTP Server interrupted", e);
        } catch (Exception e) {
            handleException("Exception running the HTTP Server", e);
        }
    }

    protected void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }

    /**
     * Initialize the Axis configuration context
     *
     * @return ConfigurationContext axis2 ConfigurationContext
     */
    protected ConfigurationContext initConfigContext() {
        try {
            ConfigurationContext configContext =
                    ConfigurationContextFactory
                            .createConfigurationContext(
                                    new FileSystemConfigurator(
                                            Constants.REPOSITORY_AXIS2,
                                            Constants.REPOSITORY_AXIS2_CONF_AXIS2_XML));
            configContext.setProperty(org.apache.axis2.Constants.CONTAINER_MANAGED,
                    org.apache.axis2.Constants.VALUE_TRUE);

            configContext.setProperty(Constants.SENSOR_CLOUD_AXIS2_PROPERTY, iotCloud);
            return configContext;
        } catch (Exception e) {
            handleException("Error occurred while initializing axis2", e);
        }
        return null;
    }
}
