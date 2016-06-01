package edu.bu.ist.apps.kualiautomation.rest.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import edu.bu.ist.apps.kualiautomation.entity.Cycle;
import edu.bu.ist.apps.kualiautomation.entity.LabelAndValue;
import edu.bu.ist.apps.kualiautomation.entity.Module;
import edu.bu.ist.apps.kualiautomation.entity.Suite;
import edu.bu.ist.apps.kualiautomation.entity.Tab;
import edu.bu.ist.apps.kualiautomation.entity.User;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScriptResource {

	@GET
	@Path("/cycle/empty")
	public Response getEmptyCycle() throws Exception {
		
		Cycle cycle = new Cycle();
		Suite suite = new Suite();
		User user = new User();
		Module module = new Module();
		Tab tab = new Tab();
		LabelAndValue lv = new LabelAndValue();
		
		suite.setCycle(cycle);
		suite.setUser(user);
		cycle.addSuite(suite);
		module.setSuite(suite);
		suite.addModule(module);
		tab.setModule(module);
		module.addTab(tab);
		lv.setTab(tab);
		tab.addLabelAndValue(lv);
		
		return Response.status(Status.OK).entity(cycle).build();
		
	}

}
