package com.socialgeomovie.servlets;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.socialgeomovie.clients.SaveDataClient;

@Path("/db")
public class AdminServelt {
	// http://localhost:8080/aw2017/rest/db

	/**
	 * Import Movies Data
	 */
	@POST
	@Path("/movies")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response importMovies() {
		SaveDataClient.saveAllMovies();
		//return null;
		return Response.status(Status.OK).entity("{\"status\":\"OK\"}").build();
	}
	
	/**
	 * Import Cast Data
	 */
	@POST
	@Path("/cast")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response importCast() {
		SaveDataClient.saveAllMovieCast();
		//return null;
		return Response.status(Status.OK).entity("{\"status\":\"OK\"}").build();
	}

}
