package com.socialgeomovie.servlets;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;

@Path("/movie")
public class MovieServelt {
	// http://localhost:8080/aw2017/rest/movie

	/**
	 * Get all movies. // Include filtering number of results option
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMovies(
			@QueryParam("include_details") final boolean details,
			@QueryParam("limit") final int limit,
			@QueryParam("page") final int page) {
		List<Map<String, Object>> nodeList = new ArrayList<>();
		Gson gson = new Gson();

		GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabel("Movie");
		for (GetNodesByLabel getNodesByLabel : movieNodes) {
			Map<String, Object> nodeInfo = new HashMap<String, Object>();
			try {
				URI propertiesURI = new URI(getNodesByLabel.getSelf());
				LinkedTreeMap<String, Object> propertiesResponse = (LinkedTreeMap<String, Object>) Neo4JClient
						.getNodeProperties(propertiesURI);
				nodeInfo.put("title", propertiesResponse.get("title"));
				Number idNumb = (Number) propertiesResponse.get("id_trakt");
				nodeInfo.put("id", idNumb.intValue());
				if (details) {
					nodeInfo.putAll(propertiesResponse);
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
	 * Add a movie
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createMovie() {
		return null;
	}

	/**
	 * Add info about a movie
	 */
	@GET
	@Path("/{movie_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMovie(@PathParam("movie_id") int movie_id) {
		return null;
	}

	/**
	 * Update info about a movie
	 */
	@PUT
	@Path("/{movie_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateMovie(@PathParam("movie_id") int movie_id) {
		return null;
	}

	/**
	 * Delete a movie
	 */
	@DELETE
	@Path("/{movie_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteMovie(@PathParam("movie_id") int movie_id) {
		return null;
	}
}
