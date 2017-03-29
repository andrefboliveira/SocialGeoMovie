package com.socialgeomovie.servlets;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.socialgeomovie.clients.Neo4JClient;
import com.socialgeomovie.pojos.neo4j.GetNodeByID;
import com.socialgeomovie.pojos.neo4j.GetNodeRelationship;
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
			@DefaultValue("false") @QueryParam("include_details") final boolean details,
			@DefaultValue("-1") @QueryParam("limit") final int limit,
			@DefaultValue("1") @QueryParam("page") final int page) {
		List<Map<String, Object>> nodeList = new ArrayList<>();
		Gson gson = new Gson();

		GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabel("Movie");
		int length = movieNodes.length;

		int firstResult, lastResult;
		if (limit > -1) {
			firstResult = Integer.min(length, ((page - 1) * limit));
			lastResult = Integer.min(length, (firstResult + limit));
		} else {
			firstResult = 0;
			lastResult = length;
		}

		for (GetNodesByLabel getNodesByLabel : Arrays.copyOfRange(movieNodes, firstResult, lastResult)) {
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
					nodeInfo.put("title", propertiesResponse.get("title"));
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
		// TODO Return type
		// Using trakt id
		Neo4JClient.safeDeleteNode(movie_id);
		
//		return null;
		return Response.status(Status.OK).entity("{\"status\":\"OK\"}").build();
	}
	
	@Path("/{movie_id}/people")
    public MoviePeople peopleSubResource() {
        return new MoviePeople();
    }

	public class MoviePeople {
		// http://localhost:8080/aw2017/rest/movie/{movie_id}/people

		/**
		 * Add info about a movie
		 */
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Response getMovie(
				@PathParam("movie_id") int movie_id,
				@DefaultValue("false") @QueryParam("include_details") final boolean details,
				@DefaultValue("-1") @QueryParam("limit") final int limit,
				@DefaultValue("1") @QueryParam("page") final int page) {

			List<Map<String, Object>> nodeList = new ArrayList<>();
			Gson gson = new Gson();
			
			try {
				GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "id_trakt", movie_id);
				String movieRelationURI = movieNodes[0].getAllRelationships();
				GetNodeRelationship[] nodeRelationship = Neo4JClient.getNodeRelationships(movieRelationURI);
				
				int length = nodeRelationship.length;

				int firstResult, lastResult;
				if (limit > -1) {
					firstResult = Integer.min(length, ((page - 1) * limit));
					lastResult = Integer.min(length, (firstResult + limit));
				} else {
					firstResult = 0;
					lastResult = length;
				}
				
				for (GetNodeRelationship getNodeRelationship : Arrays.copyOfRange(nodeRelationship, firstResult, lastResult)) {
					Map<String, Object> nodeInfo = new HashMap<String, Object>();
					
					String nodeID = getNodeRelationship.getStart();
					String nodePropertiesURI = Neo4JClient.getNode(nodeID).getProperties();
					
					LinkedTreeMap<String, Object> propertiesResponse = (LinkedTreeMap<String, Object>) Neo4JClient
							.getNodeProperties(nodePropertiesURI);

					Number idNumb = (Number) propertiesResponse.get("id_trakt");
					nodeInfo.put("id", idNumb.intValue());

					if (details) {
						nodeInfo.putAll(propertiesResponse);
					} else {
						nodeInfo.put("name", propertiesResponse.get("name"));
					}

					nodeList.add(nodeInfo);

				}

			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
		 * Delete a movie
		 */
		@DELETE
		@Path("/{person_id}")
		@Produces(MediaType.APPLICATION_JSON)
		public Response deleteMovie(@PathParam("movie_id") int movie_id, @PathParam("person_id") int person_id) {
			return null;
		}
	}
}
