package com.socialgeomovie.servlets;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/movie")
public class MovieServelt {
	// http://localhost:8080/aw2017/rest/movie

	/**
	 * Get all movies. // Include filtering number of results option
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMovies(){
		return null;
	}
	
	/**
	 * Add a movie
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createMovie(){
		return null;
	}
	
	/**
	 * Add info about a movie
	 */
	@GET
	@Path("/{movie_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMovie(@PathParam("movie_id") int movie_id){
		return null;
	}
	
	/**
	 * Update info about a movie
	 */
	@PUT
	@Path("/{movie_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateMovie(@PathParam("movie_id") int movie_id){
		return null;
	}
	
	/**
	 * Delete a movie
	 */
	@DELETE
	@Path("/{movie_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteMovie(@PathParam("movie_id") int movie_id){
		return null;
	}
}
