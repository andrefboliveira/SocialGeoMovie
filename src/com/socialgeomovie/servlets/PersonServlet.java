package com.socialgeomovie.servlets;

import java.io.UnsupportedEncodingException;
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

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.socialgeomovie.clients.Neo4JClient;
import com.socialgeomovie.pojos.neo4j.GetNodeByID;
import com.socialgeomovie.pojos.neo4j.GetNodeRelationship;
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;
import com.socialgeomovie.pojos.neo4j.cypher.Datum;
import com.socialgeomovie.servlets.MovieServlet.MoviePeople;
import com.socialgeomovie.utils.Servlet;

@Path("/person")
public class PersonServlet {
	// http://localhost:8080/aw2017/rest/person

	/**
	 * Get all people
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPeople(@DefaultValue("false") @QueryParam("include_details") final boolean details,
			@DefaultValue("-1") @QueryParam("limit") final int limit,
			@DefaultValue("1") @QueryParam("page") final int page) {
		List<Map<String, Object>> nodeList = new ArrayList<>();
		Gson gson = new Gson();

		GetNodesByLabel[] personNodes = Neo4JClient.getNodesByLabel("Person");
		int length = personNodes.length;

		int firstResult, lastResult;
		if (limit > -1) {
			firstResult = Integer.min(length, ((page - 1) * limit));
			lastResult = Integer.min(length, (firstResult + limit));
		} else {
			firstResult = 0;
			lastResult = length;
		}

		for (int nodeNumber = firstResult; nodeNumber < lastResult; nodeNumber++) {
			GetNodesByLabel getNodesByLabel = personNodes[nodeNumber];

			Map<String, Object> nodeInfo = new HashMap<String, Object>();

			Map<String, Object> propertiesResponse = getNodesByLabel.getData();

			nodeInfo.put("uri", propertiesResponse.get("uri"));

			if (details) {
				nodeInfo.putAll(propertiesResponse);
			} else {
				nodeInfo.put("name", propertiesResponse.get("name"));
			}

			nodeList.add(nodeInfo);

		}
		return Response.status(Status.OK).entity(gson.toJson(nodeList)).build();
	}

	/**
	 * Create a new person
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPerson(String requestJSON) {
		return null;
	}

	/**
	 * Get info about a given Neo4J person Example(Ryan Reynolds):
	 * http://localhost:8080/aw2017/rest/person/534
	 */
	@GET
	@Path("/{person_uri}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPerson(@PathParam("person_uri") String person_uri) {
		Gson gson = new Gson();

		Map<String, Object> nodeInfo = new HashMap<String, Object>();

		try {
			GetNodesByLabel[] personNodes = Neo4JClient.getNodesByLabelAndProperty("Person", "uri", person_uri);

			nodeInfo.putAll((personNodes[0].getData()));


		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Response.status(Status.OK).entity(gson.toJson(nodeInfo)).build();

	}

	/**
	 * update person info
	 */
	@PUT
	@Path("/{person_uri}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePerson(@PathParam("person_uri") String person_uri,
			String requestJSON) {
		Gson gson = new Gson();
		
		Map<String, Object> nodeInfo = Servlet.updateResource("Person", person_uri, requestJSON);

		return Response.status(Status.OK).entity(gson.toJson(nodeInfo)).build();
	}

	/**
	 * delete person
	 */
	@DELETE
	@Path("/{person_uri}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePerson(@PathParam("person_uri") String person_uri) {
		Neo4JClient.safeDeleteNode(person_uri);
		// return null;
		return Response.status(Status.NO_CONTENT).entity("{\"status\":\"NO CONTENT\"}").build();
	}
	
	
	/**
	 * Autocomplete
	 * @param propertyName 
	 * @param propertyValue 
	 */
	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPeopleByProperty(
			@DefaultValue("-1") @QueryParam("limit") final int limit, 
			@DefaultValue("name") @QueryParam("property") final String propertyName, 
			@QueryParam("value") final String propertyValue,
			@DefaultValue("false") @QueryParam("include_details") final boolean details) {
		List<Map<String, Object>> nodeList = new ArrayList<>();
		Gson gson = new Gson();

		String query = "MATCH (n:Person) WHERE n." + propertyName + " =~ '(?i).*" + propertyValue + ".*' RETURN n";
		query = limit > -1 ? (query + " LIMIT " + limit ): query;
		System.out.println(query);
		
		List<Datum> results = Neo4JClient.sendTransactionalCypherQuery(query).getResults().get(0).getData();
		
		
		for (Datum line : results) {
			Map<String, Object> resultMap = (Map<String, Object>) line.getRow().get(0);
			
			Map<String, Object> nodeInfo = new HashMap<String, Object>();

			if (details) {
				nodeInfo.putAll(resultMap);
			} else {
				nodeInfo.put("name", resultMap.get("name"));
				nodeInfo.put("uri", resultMap.get("uri"));

			}

			nodeList.add(nodeInfo);

		}
		return Response.status(Status.OK).entity(gson.toJson(nodeList)).build();
	}
	
	@Path("/{person_uri}/movies")
	public MoviePeople peopleSubResource() {
		return new MoviePeople();
	}

	public class MoviePeople {
		// http://localhost:8080/aw2017/rest/person/{person_uri}/movies
		/**
		 * Add info about a movie
		 */
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Response getMoviePeople(@PathParam("person_uri") String person_uri,
				@DefaultValue("false") @QueryParam("include_details") final boolean details,
				@DefaultValue("-1") @QueryParam("limit") final int limit,
				@DefaultValue("1") @QueryParam("page") final int page) {

			List<Map<String, Object>> nodeList = new ArrayList<>();
			Gson gson = new Gson();

			try {
				GetNodesByLabel[] castNodes = Neo4JClient.getNodesByLabelAndProperty("Cast", "uri", person_uri);
				
				GetNodeRelationship[] nodeRelationship = Neo4JClient.getNodeRelationshipsByType(castNodes[0].getSelf(), "acts in");

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

					String nodeID = getNodeRelationship.getEnd();
					String nodePropertiesURI = Neo4JClient.getNode(nodeID).getProperties();

					LinkedTreeMap<String, Object> propertiesResponse = (LinkedTreeMap<String, Object>) Neo4JClient
							.getNodeProperties(nodePropertiesURI);

					String uri = (String) propertiesResponse.get("uri");
					nodeInfo.put("uri", uri);

					if (details) {
						nodeInfo.putAll(propertiesResponse);
					} else {
						nodeInfo.put("title", propertiesResponse.get("title"));
					}
					
					nodeInfo.put("character", getNodeRelationship.getData().get("character"));

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
		@Path("/{movie_uri}")
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
		@Path("/{movie_uri}")
		@Produces(MediaType.APPLICATION_JSON)
		public Response deleteMoviePerson(
				@PathParam("movie_uri") String movie_uri, 
				@PathParam("person_uri") String person_uri) {
			return null;
		}
		
		/**
		 * Main People
		 * 
		 * @param propertyName
		 * @param propertyValue
		 */
		@GET
		@Path("/main")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getMainMoviePeople(@PathParam("person_uri") String person_uri,
				@DefaultValue("popularity") @QueryParam("order_by") final String orderby,
				@DefaultValue("-1") @QueryParam("limit") final int limit,
				@DefaultValue("false") @QueryParam("include_details") final boolean details) {
			List<Object> nodeList = new ArrayList<Object>();
			Gson gson = new Gson();

			String query = "MATCH(movie:Movie) <-[r:`acts in`]- (cast:Cast {uri:\"" + person_uri
					+ "\"}) RETURN movie, r ORDER BY movie." + orderby + " DESC";
			query = limit > -1 ? (query + " LIMIT " + limit) : query;

			List<Datum> results = Neo4JClient.sendTransactionalCypherQuery(query).getResults().get(0).getData();
			for (Datum line : results) {
				Map<String, Object> resultMap = (Map<String, Object>) line.getRow().get(0);
				Map<String, Object> relationMap = (Map<String, Object>) line.getRow().get(1);
				Map<String, Object> nodeInfo = new HashMap<String, Object>();

				if (details) {
					nodeInfo.putAll(resultMap);
				} else {
					nodeInfo.put("uri", resultMap.get("uri"));
					nodeInfo.put("title", resultMap.get("title"));
				}
				nodeInfo.put("character", relationMap.get("character"));
				nodeList.add(nodeInfo);

			}
			return Response.status(Status.OK).entity(gson.toJson(nodeList)).build();
		}
	}

}
