package com.socialgeomovie.servlets;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;
import com.socialgeomovie.clients.SaveDataClient;

@Path("/db")
public class AdminServelt {
	// http://localhost:8080/aw2017/rest/db

	/**
	 * Import Movies Data
	 */
	@GET
	@Path("/movies")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response importMovies(@DefaultValue("10") @QueryParam("quantity") final int quantity) 
	{
		Map<String, URI> moviesReport = SaveDataClient.saveAllMovies(quantity);
		Map<String, String> report = new HashMap<String,String>();
		report.put("status", "OK");
		report.put("movies", ""+moviesReport.size());
		Gson gson = new Gson();
		return Response.status(Status.OK).entity(gson.toJson(report)).build();
	}
	
	/**
	 * Import Cast Data
	 */
	@GET
	@Path("/cast")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response importCast() 
	{
		Map<String, URI> castReport = SaveDataClient.saveAllMovieCast();
		Map<String, String> report = new HashMap<String,String>();
		report.put("status", "OK");
		report.put("cast", ""+castReport.size());
		Gson gson = new Gson();
		return Response.status(Status.OK).entity(gson.toJson(report)).build();
	}

}
