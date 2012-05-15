package cgl.iotcloud.api.http;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.IoTCloud;
import cgl.iotcloud.core.sensor.SCSensor;
import cgl.iotcloud.core.sensor.SCSensorUtils;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.status;

@Path("/client")
public class ClientServices {
    @Context
    private ServletContext servletContext;

    @GET
    @Path("/sensors")
    @Produces("text/xml")
    public Response getSensors() {
        IoTCloud iotCloud = (IoTCloud) servletContext.getAttribute(
                Constants.IOT_CLOUD_SERVLET_PROPERTY);

        List<SCSensor> sensorList = iotCloud.getSensorCatalog().getSensors();

        Response.ResponseBuilder r = status(200);

        String sensors = SCSensorUtils.convertToString(sensorList);
        r = r.entity(sensors);

        return r.build();
    }
}
