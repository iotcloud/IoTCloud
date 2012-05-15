package cgl.iotcloud.api.http;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.IoTCloud;
import cgl.iotcloud.core.sensor.SCSensor;
import cgl.iotcloud.core.sensor.SCSensorUtils;
import cgl.iotcloud.core.sensor.Sensor;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.status;

@Path("/iotcs")
public class SensorServices {
    @Resource
    private ServletContext servletContext;

    private IoTCloud iotCloud = null;

    public SensorServices() {
        iotCloud = (IoTCloud) servletContext.getAttribute(
                Constants.IOT_CLOUD_SERVLET_PROPERTY);
    }

    @GET
    @Path("/init/{ip}")
    @Produces("text/plain")
    public String init(@PathParam("ip") String sgxIP){
        return "Client already Initialized";
    }

    @PUT
    @Path("/register")
    @Produces("text/xml")
    public Response registerSensor(@QueryParam("name") String name, @QueryParam("type") String type) {
        Sensor s = iotCloud.registerSensor(name, type);
        if (s instanceof SCSensor) {
            SCSensor sc = (SCSensor) s;

            Response.ResponseBuilder r = status(200);

            String info = SCSensorUtils.convertToString(sc);
            r = r.entity(info);

            return r.build();
        } else {
            return status(400).entity("Invalid parameters").build();
        }
    }
}
