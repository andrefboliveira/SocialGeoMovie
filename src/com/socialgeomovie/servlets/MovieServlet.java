package com.socialgeomovie.servlets;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.socialgeomovie.clients.Neo4JClient;
import com.socialgeomovie.pojos.neo4j.Data_GetNodesByLabel;
import com.socialgeomovie.pojos.neo4j.GetNodeByID;
import com.socialgeomovie.pojos.neo4j.GetNodeRelationship;
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;
import com.socialgeomovie.utils.Servlet;

@Path("/movie")
public class MovieServlet {
	// http://localhost:8080/aw2017/rest/movie

	private static final Logger logger = LoggerFactory.getLogger(MovieServlet.class);

	/**
	 * Get all movies. // Include filtering number of results option
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMovies(@DefaultValue("false") @QueryParam("include_details") final boolean details,
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

		for (int nodeNumber = firstResult; nodeNumber < lastResult; nodeNumber++) {
			GetNodesByLabel getNodesByLabel = movieNodes[nodeNumber];

			Map<String, Object> nodeInfo = new HashMap<String, Object>();
			Map<String, Object> propertiesResponse = getNodesByLabel.getData();

			nodeInfo.put("uri", propertiesResponse.get("uri"));

			if (details) {
				nodeInfo.putAll(propertiesResponse);
			} else {
				nodeInfo.put("title", propertiesResponse.get("title"));
				nodeInfo.put("poster", propertiesResponse.get("poster"));
			}

			nodeList.add(nodeInfo);

		}
		return Response.status(Status.OK).entity(gson.toJson(nodeList)).build();
	}

	/**
	 * Add a movie
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createMovie(String requestJSON) {
		return null;
	}

	/**
	 * Get movie info by given movie name Example(deadpool):
	 * http://localhost:8080/aw2017/rest/movie/Deadpool
	 */
	@GET
	@Path("/{movie_uri}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMovie(@PathParam("movie_uri") String movie_uri) {
		Gson gson = new Gson();

		Map<String, Object> nodeInfo = new HashMap<String, Object>();

		try {
			GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "uri", movie_uri);

			nodeInfo.putAll((movieNodes[0].getData()));

		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return Response.status(Status.OK).entity(gson.toJson(nodeInfo)).build();

	}

	/**
	 * Update info about a movie
	 */
	@PUT
	@Path("/{movie_uri}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateMovie(
			@PathParam("movie_uri") String movie_uri, 
			String requestJSON) {
		Gson gson = new Gson();
		
		Map<String, Object> nodeInfo = Servlet.updateResource("Movie", movie_uri, requestJSON);

		return Response.status(Status.OK).entity(gson.toJson(nodeInfo)).build();
	}

	/**
	 * Delete a movie
	 */
	@DELETE
	@Path("/{movie_uri}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteMovie(@PathParam("movie_uri") String movie_uri) {
		// TODO Return type
		Neo4JClient.safeDeleteNode(movie_uri);

		// return null;
		return Response.status(Status.NO_CONTENT).entity("{\"status\":\"NO CONTENT\"}").build();
	}

	@GET
	@Path("/{movie_uri}/tweets")
	@Produces(MediaType.APPLICATION_JSON)
	public Response tweetsResource(@PathParam("movie_uri") String movie_uri) 
	{
		List<LinkedTreeMap> tweets = new ArrayList<LinkedTreeMap>();
		try 
		{
			GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "uri", movie_uri);
			
			GetNodeRelationship[] nodeRelationship = Neo4JClient.getNodeRelationshipsByType(movieNodes[0].getSelf(), "talks about");
			for(int i=0; i<nodeRelationship.length; i++)
			{
				GetNodeRelationship getNodeRelationship = nodeRelationship[i];
				Map<String, Object> nodeInfo = new HashMap<String, Object>();
				
				String nodeID = getNodeRelationship.getStart();
				String nodePropertiesURI = Neo4JClient.getNode(nodeID).getProperties();

				LinkedTreeMap<String, Object> propertiesResponse = (LinkedTreeMap<String, Object>) Neo4JClient.getNodeProperties(nodePropertiesURI);
				tweets.add(propertiesResponse);
			}
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Gson gson = new Gson();
		return Response.status(Status.OK).entity(gson.toJson(tweets)).build();
	}
	
	@Path("/{movie_uri}/people")
	public MoviePeople peopleSubResource() {
		return new MoviePeople();
	}

	public class MoviePeople {
		// http://localhost:8080/aw2017/rest/movie/{movie_uri}/people

		/**
		 * Add info about a movie
		 */
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Response getMoviePeople(@PathParam("movie_uri") String movie_uri,
				@DefaultValue("false") @QueryParam("include_details") final boolean details,
				@DefaultValue("-1") @QueryParam("limit") final int limit,
				@DefaultValue("1") @QueryParam("page") final int page) {

			List<Map<String, Object>> nodeList = new ArrayList<>();
			Gson gson = new Gson();

			try {
				GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "uri", movie_uri);
				GetNodeRelationship[] nodeRelationship = Neo4JClient.getNodeRelationshipsByType(movieNodes[0].getSelf(), "acts in");

				int length = nodeRelationship.length;

				int firstResult, lastResult;
				if (limit > -1) {
					firstResult = Integer.min(length, ((page - 1) * limit));
					lastResult = Integer.min(length, (firstResult + limit));
				} else {
					firstResult = 0;
					lastResult = length;
				}

				for (int nodeNumber = firstResult; nodeNumber < lastResult; nodeNumber++) {
					GetNodeRelationship getNodeRelationship = nodeRelationship[nodeNumber];

					Map<String, Object> nodeInfo = new HashMap<String, Object>();

					String nodeID = getNodeRelationship.getStart();
					String nodePropertiesURI = Neo4JClient.getNode(nodeID).getProperties();

					LinkedTreeMap<String, Object> propertiesResponse = (LinkedTreeMap<String, Object>) Neo4JClient
							.getNodeProperties(nodePropertiesURI);

					String uri = (String) propertiesResponse.get("uri");
					nodeInfo.put("uri", uri);

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
		 * Add a movie person relationship
		 */
		@PUT
		@Path("/{person_uri}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response addMoviePerson(
				@PathParam("movie_uri") String movie_uri, 
				@PathParam("person_uri") String person_uri) {
			return null;
		}

		/**
		 * Delete a movie person relationship
		 */
		@DELETE
		@Path("/{person_uri}")
		@Produces(MediaType.APPLICATION_JSON)
		public Response deleteMoviePerson(
				@PathParam("movie_uri") String movie_uri, 
				@PathParam("person_uri") String person_uri) {
			return null;
		}
	}
}
