package edu.bu.ist.apps.kualiautomation.rest.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import edu.bu.ist.apps.kualiautomation.entity.Cycle;
import edu.bu.ist.apps.kualiautomation.services.ScriptService;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScriptResource {

	@GET
	@Path("/cycle/empty")
	public Response getEmptyCycle() throws Exception {
		
		Response response = ServiceResponse.getResponse((new ScriptService()).getEmptyCycle(), Status.OK);
		return response;		
	}

	@POST
	@Path("/cycle/save")
	public Response saveCycle(Cycle cycle) throws Exception {
		Response response = ServiceResponse.getResponse(cycle, Status.OK);
		return response;		
	}

}
