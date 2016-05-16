package com.warren.selenium.helloworld.rest.resource;

import java.awt.HeadlessException;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JFileChooser;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.warren.selenium.helloworld.model.Test1;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestResource {
	
	@GET
	@Path("/test1")
	public Response getTournament() {
		// http://localhost:8080/rest/test1
		Test1 test1 = null;
		final JFileChooser fc = new JFileChooser();
		try {
			int returnVal = fc.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
			    File file = fc.getSelectedFile();
			    test1 = new Test1(file);
			} 
			else {
			    test1 = new Test1();
			}
		}
		catch (HeadlessException e) {
			test1 = new Test1();
			test1.setError(stackTraceToString(e));
			e.printStackTrace();
		}
		return Response.status(Status.OK).entity(test1).build();
	}

	public static String stackTraceToString(Throwable e) {
		if(e == null)
			return null;
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		String trace = sw.getBuffer().toString();
		return trace;
	}

}
