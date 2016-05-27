package edu.bu.ist.apps.kualiautomation.rest.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.bu.ist.apps.kualiautomation.entity.Suite;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScriptResource {

	@POST
	@Path("/script/add")
	public Response updateOutputDirectory(Suite suite) throws Exception {
		
		return null;
		
	}

}
