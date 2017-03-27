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

@Path("/people")
public class PersonServlet {
	// http://localhost:8080/aw2017/rest/people

	/**
	 * Get all people
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPeople() {
		return null;
	}
		

	/**
	 * Create a new person
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPerson() {
		return null;
	}

	/**
	 * Get info about a person
	 */
	@GET
	@Path("/{person_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPerson(@PathParam("person_id") int person_id) {
		return null;
	}

	/**
	 * update person info
	 */
	@PUT
	@Path("/{person_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePerson(@PathParam("person_id") int person_id) {
		return null;
	}

	/**
	 * delete person
	 */
	@DELETE
	@Path("/{person_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePerson(@PathParam("person_id") int person_id) {
		return null;
	}
}
