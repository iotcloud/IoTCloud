package cgl.iotcloud.api.http;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.IoTCloud;
import cgl.iotcloud.core.sensor.SCSensor;
import cgl.iotcloud.core.sensor.SCSensorUtils;
import cgl.iotcloud.core.sensor.Sensor;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.status;

@Path("/sensor")
public class SensorServices {
    @Context
    private ServletContext servletContext;

    @PUT
    @Path("/register")
    @Produces("text/xml")
    public Response registerSensor(@QueryParam("name") String name, @QueryParam("type") String type) {
        IoTCloud iotCloud = (IoTCloud) servletContext.getAttribute(
                Constants.IOT_CLOUD_SERVLET_PROPERTY);

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

    @POST
    @Path("/unregister")
    @Produces("text/plain")
    public Response unRegisterSensor(@QueryParam("id") String id) {
        IoTCloud iotCloud = (IoTCloud) servletContext.getAttribute(
                Constants.IOT_CLOUD_SERVLET_PROPERTY);

        iotCloud.unRegisterSensor(id);

        return status(200).entity("Successfully un registered sensor").build();
    }
}
