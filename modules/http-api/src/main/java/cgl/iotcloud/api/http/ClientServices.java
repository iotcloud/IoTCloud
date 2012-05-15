package cgl.iotcloud.api.http;

import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("/sensor")
public class ClientServices {
    @Context
    private ServletContext servletContext;


}
