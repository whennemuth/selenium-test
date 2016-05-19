package edu.bu.ist.apps.kualiautomation.rest.resource;

import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JFileChooser;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import edu.bu.ist.apps.kualiautomation.Utils;
import edu.bu.ist.apps.kualiautomation.model.Directory;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfigResource {
	
	@GET
	@Path("/browse/for/directory")
	public Response getDirectory() {
		// http://localhost:8080/rest/browse/for/directory
		Directory dir = null;
		final JFileChooser fc = new JFileChooser();
		try {
			int returnVal = fc.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
			    File file = fc.getSelectedFile();
			    dir = new Directory(file);
			} 
			else {
			    dir = new Directory();
			}
		}
		catch (HeadlessException e) {
			dir = new Directory();
			dir.setError(Utils.stackTraceToString(e));
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(dir).build();
	}
	
	@GET
	@Path("/config")
	public Response getConfig() {
		return null;
	}

}
