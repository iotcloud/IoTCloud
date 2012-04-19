package cgl.iotcloud.api.http;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/iotcs")
public class IOTCloud {

    @GET
    @Path("/init/{ip}")
    @Produces("text/plain")
    public String init(@PathParam("ip") String sgxIP){
        return "Client already Initialized";
    }
}
