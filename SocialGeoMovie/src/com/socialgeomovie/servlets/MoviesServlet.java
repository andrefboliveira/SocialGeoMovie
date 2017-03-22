package com.socialgeomovie.servlets;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("movies")
public class MoviesServlet 
{
	@GET
	@Produces("application/json")
	public Response testGet()
	{
		
		return Response.status(Status.OK).entity("{\"status\":\"OK\"}").build();
	}
}