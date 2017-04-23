package com.socialgeomovie.servlets;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.socialgeomovie.clients.Neo4JClient;
import com.socialgeomovie.pojos.neo4j.GetNodeByID;
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;

@Path("/person")
public class PersonServlet {
	// http://localhost:8080/aw2017/rest/person

	/**
	 * Get all people
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPeople(
			@DefaultValue("false") @QueryParam("include_details") final boolean details,
			@DefaultValue("-1") @QueryParam("limit") final int limit,
			@DefaultValue("1") @QueryParam("page") final int page) {
		List<Map<String, Object>> nodeList = new ArrayList<>();
		Gson gson = new Gson();

		GetNodesByLabel[] personNodes = Neo4JClient.getNodesByLabel("Person");
		int length = personNodes.length;
		
		int firstResult, lastResult;
		if (limit > -1) {
			firstResult = Integer.min(length, ((page - 1) * limit));
			lastResult =  Integer.min(length, (firstResult + limit));
		} else {
			firstResult = 0;
			lastResult = length;
		}
		
		for (GetNodesByLabel getNodesByLabel : Arrays.copyOfRange(personNodes, firstResult, lastResult)) {
			Map<String, Object> nodeInfo = new HashMap<String, Object>();
			try {
				URI propertiesURI = new URI(getNodesByLabel.getSelf());
				LinkedTreeMap<String, Object> propertiesResponse = (LinkedTreeMap<String, Object>) Neo4JClient
						.getNodeProperties(propertiesURI);
				
				Number idNumb = (Number) propertiesResponse.get("id_trakt");
				nodeInfo.put("id", idNumb.intValue());
				
				
				if (details) {
					nodeInfo.putAll(propertiesResponse);
				} else {
					nodeInfo.put("name", propertiesResponse.get("name"));
				}
				

				nodeList.add(nodeInfo);

			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Response.status(Status.OK).entity(gson.toJson(nodeList)).build();
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
	 * Get info about a given Neo4J person 
	 * Example(Ryan Reynolds): http://localhost:8080/aw2017/rest/person/534
	 */
	@GET
	@Path("/{person_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPerson(@PathParam("person_id") String person_id) {
		Gson gson = new Gson();
		GetNodeByID personNode = Neo4JClient.getNodeByID(person_id);
		LinkedTreeMap<String, Object> propertiesResponse = (LinkedTreeMap<String, Object>) Neo4JClient
				.getNodeProperties(personNode.getProperties());
		return Response.status(Status.OK).entity(gson.toJson(propertiesResponse)).build();
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
		Neo4JClient.safeDeleteNode(person_id);
//		return null;
		return Response.status(Status.NO_CONTENT).entity("{\"status\":\"NO CONTENT\"}").build();
		}
}
