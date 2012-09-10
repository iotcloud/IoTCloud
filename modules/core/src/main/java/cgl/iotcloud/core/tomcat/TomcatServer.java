package cgl.iotcloud.core.tomcat;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.IOTRuntimeException;
import cgl.iotcloud.core.IoTCloud;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.deployment.FileSystemConfigurator;
import org.apache.axis2.transport.http.AxisServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * The embedded tomcat server to be used
 */
public class TomcatServer {
    private Logger log = LoggerFactory.getLogger(TomcatServer.class);

    /** Tomcat instance */
    private Tomcat tomcat = null;
    /** IOTCloud instance */
    private IoTCloud iotCloud = null;
    /** base directory */
    private static final String baseDir = ".";
    /** web apps directory */
    private static final String webAppsSir = "webapps";

    public TomcatServer(IoTCloud iotCloud) {
        this.iotCloud = iotCloud;
    }

    /**
     * Start the tomcat server.
     */
    public void start() {
        tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.setBaseDir(baseDir);
        tomcat.getHost().setAppBase(webAppsSir);

        // register apache axis2
        Context soapContext = tomcat.addContext(Constants.CONTEXT_PATH,
                new File(".").getAbsolutePath());
        Tomcat.addServlet(soapContext, "axisServlet",
                AxisServlet.class.getCanonicalName());
        soapContext.addServletMapping(Constants.SERVICES_PATH, "axisServlet");
        soapContext.getServletContext().setAttribute(
                AxisServlet.CONFIGURATION_CONTEXT, initConfigContext());

        // register the resteasy
        Context restContext = tomcat.addContext("/rest", new File(".").getAbsolutePath());
        restContext.addParameter("resteasy.resources",
                "cgl.iotcloud.api.http.SensorServices, cgl.iotcloud.api.http.ClientServices");
        restContext.addApplicationListener(
                "org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap");
        Tomcat.addServlet(restContext, "rest", new HttpServletDispatcher());
        restContext.addServletMapping("/*", "rest");

        Wrapper jspServlet = restContext.createWrapper();
        jspServlet.setName("jsp");
        jspServlet.setServletClass("org.apache.jasper.servlet.JspServlet");
        jspServlet.addInitParameter("fork", "false");
        jspServlet.addInitParameter("xpoweredBy", "false");
        jspServlet.setLoadOnStartup(2);
        restContext.addChild(jspServlet);
        restContext.addServletMapping("*.jsp", "jsp");

        restContext.getServletContext().setAttribute(Constants.IOT_CLOUD_SERVLET_PROPERTY, iotCloud);

        try {
            tomcat.start();
        } catch (LifecycleException e) {
            handleException("Failed to start the tomcat server....", e);
        }

        tomcat.getServer().await();
    }

    public void stop() {
        try {
            if (tomcat != null) {
                tomcat.stop();
            }
        } catch (LifecycleException e) {
            handleException("Failed to stop the tomcat server.....", e);
        }
    }

    protected void handleException(String s, Exception e) {
        log.error(s, e);
        throw new IOTRuntimeException(s, e);
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
