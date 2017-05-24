package com.socialgeomovie.servlets;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import com.socialgeomovie.clients.SaveDataClient;
import com.socialgeomovie.pojos.neo4j.GetNodeByID;
import com.socialgeomovie.pojos.neo4j.GetNodeRelationship;
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;
import com.socialgeomovie.pojos.neo4j.cypher.CypherResultNormal;
import com.socialgeomovie.pojos.neo4j.cypher.Datum;
import com.socialgeomovie.pojos.neo4j.cypher.Result;
import com.socialgeomovie.utils.Servlet;
import com.socialgeomovie.utils.exceptions.Neo4JRequestException;

@Path("/movie")
public class MovieServlet {
	// http://localhost:8080/aw2017/rest/movie

	private static final Logger logger = LoggerFactory.getLogger(MovieServlet.class);

	/**
	 * Get all movies. // Include filtering number of results option
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMovies(
			@DefaultValue("-1") @QueryParam("limit") final int limit,
			@DefaultValue("1") @QueryParam("page") final int page,
			@DefaultValue("false") @QueryParam("include_poster") final boolean poster,
			@DefaultValue("false") @QueryParam("include_details") final boolean details) {
		try {
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


				if (details) {
					nodeInfo.putAll(propertiesResponse);
				} else {
					nodeInfo.put("uri", propertiesResponse.get("uri"));
					nodeInfo.put("title", propertiesResponse.get("title"));
					if (poster) {
						nodeInfo.put("poster", propertiesResponse.get("poster"));
					}
				}

				nodeList.add(nodeInfo);

			}
			return Response.status(Status.OK).entity(gson.toJson(nodeList)).build();
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			e.printStackTrace();
			return Response.status(Status.NOT_FOUND).entity("{\"status\":\"NOT FOUND\"}").build();
		}  catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}")
					.build();
		}
	}

	/**
	 * Get movie info by given movie name Example(deadpool):
	 * http://localhost:8080/aw2017/rest/movie/Deadpool
	 */
	@GET
	@Path("/{movie_uri}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMovie(@PathParam("movie_uri") String movie_uri) {

		try {
			Map<String, Object> nodeInfo = new HashMap<String, Object>();

			GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "uri", movie_uri);

			nodeInfo.putAll((movieNodes[0].getData()));

			return Response.status(Status.OK).entity(new Gson().toJson(nodeInfo)).build();
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			e.printStackTrace();
			return Response.status(Status.NOT_FOUND).entity("{\"status\":\"NOT FOUND\"}").build();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}")
					.build();
		}

	}

	/**
	 * Add a movie
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createMovie(String requestJSON) {
		try {
			Map<String, String> report = new HashMap<String, String>();
			Gson gson = new Gson();
			Type type = new TypeToken<Map<String, Object>>() {
			}.getType();
			Map<String, Object> movieProperties = gson.fromJson(requestJSON, type);
			String movieURI = (String) movieProperties.get("uri");

			Neo4JClient.createNodeWithProperties("Movie", movieProperties);
			report.put("uri", movieURI);
			return Response.status(Status.CREATED).entity(new Gson().toJson(report)).build();

		} catch (NullPointerException e) {
			e.printStackTrace();
			return Response.status(Status.NOT_ACCEPTABLE).entity("{\"status\":\"NOT ACCEPTABLE\"}").build();
		} catch (Neo4JRequestException e) {
			e.printStackTrace();
			return Response.status(Status.CONFLICT).entity("{\"status\":\"CONFLICT\"}").build();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}")
					.build();
		}
	}

	/**
	 * Update info about a movie
	 */
	@PUT
	@Path("/{movie_uri}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateMovie(@PathParam("movie_uri") String movie_uri, String requestJSON) {
		try {

			Map<String, Object> nodeInfo = Servlet.updateResource("Movie", movie_uri, requestJSON);

			return Response.status(Status.OK).entity(new Gson().toJson(nodeInfo)).build();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}")
					.build();
		}
	}

	/**
	 * Delete a movie
	 */
	@DELETE
	@Path("/{movie_uri}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteMovie(@PathParam("movie_uri") String movie_uri) {
		// TODO Return type
		try {
			Neo4JClient.safeDeleteNode(movie_uri);

			return Response.status(Status.NO_CONTENT).entity("{\"status\":\"NO CONTENT\"}").build();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}")
					.build();
		}
	}

	/**
	 * Autocomplete
	 * 
	 * @param propertyName
	 * @param propertyValue
	 */
	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchMoviesByProperty(@DefaultValue("-1") @QueryParam("limit") final int limit,
			@DefaultValue("title") @QueryParam("property") final String propertyName,
			@QueryParam("value") final String propertyValue,
			@DefaultValue("true") @QueryParam("compact") final boolean compact_mode,
			@DefaultValue("false") @QueryParam("include_poster") final boolean poster,
			@DefaultValue("false") @QueryParam("include_details") final boolean details) {
		try {
			List<Object> nodeList = new ArrayList<Object>();
			Gson gson = new Gson();
	
			String query = "MATCH (n:Movie) WHERE n." + propertyName + " =~ '(?i).*" + propertyValue + ".*' RETURN n";
			query = limit > -1 ? (query + " LIMIT " + limit) : query;
	
			List<Datum> results = Neo4JClient.sendTransactionalCypherQuery(query).getResults().get(0).getData();
			for (Datum line : results) {
				Map<String, Object> resultMap = (Map<String, Object>) line.getRow().get(0);
	
				Map<String, Object> nodeInfo = new HashMap<String, Object>();
	
				if (compact_mode) {
					nodeList.add(resultMap.get(propertyName));
				} else {
					
					if (details) {
						nodeInfo.putAll(resultMap);
					} else {
						nodeInfo.put("uri", resultMap.get("uri"));
						nodeInfo.put(propertyName, resultMap.get(propertyName));
						
						if (poster) {
							nodeInfo.put("poster", resultMap.get("poster"));
						}
					}
					
					nodeList.add(nodeInfo);
				}
			}
			return Response.status(Status.OK).entity(gson.toJson(nodeList)).build();
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			e.printStackTrace();
			return Response.status(Status.NOT_FOUND).entity("{\"status\":\"NOT FOUND\"}").build();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}")
					.build();
		}
	
	}

	@Path("/{movie_uri}/person")
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
				@DefaultValue("-1") @QueryParam("limit") final int limit,
				@DefaultValue("1") @QueryParam("page") final int page,
				@DefaultValue("false") @QueryParam("include_profile_image") final boolean profile_image,
				@DefaultValue("false") @QueryParam("include_details") final boolean details) {

			try {

				List<Map<String, Object>> nodeList = new ArrayList<>();
				Gson gson = new Gson();

				GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "uri", movie_uri);
				GetNodeRelationship[] nodeRelationship = Neo4JClient.getNodeRelationshipsByType(movieNodes[0].getSelf(),
						"acts in");

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
						
						if (profile_image) {
							nodeInfo.put("profile_image", propertiesResponse.get("profile_image"));
						}
					}

					nodeInfo.put("character", getNodeRelationship.getData().get("character"));

					nodeList.add(nodeInfo);
				}

				return Response.status(Status.OK).entity(gson.toJson(nodeList)).build();
			} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
				// TODO: handle exception
				e.printStackTrace();
				return Response.status(Status.NOT_FOUND).entity("{\"status\":\"NOT FOUND\"}").build();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}")
						.build();
			}
		}

		/**
		 * Add a movie person relationship
		 */
		@PUT
		@Path("/{person_uri}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response addMoviePerson(@PathParam("movie_uri") String movie_uri,
				@PathParam("person_uri") String person_uri,
				String requestJSON) {
			
			try {
				Map<String, String> report = new HashMap<String, String>();

				Gson gson = new Gson();
				Type type = new TypeToken<Map<String, Object>>() {}.getType();
				Map<String, Object> request = gson.fromJson(requestJSON, type);
				
				Map<String, Object> characterMap = new HashMap<String, Object>();
				String character = (String) request.get("character");
				characterMap.put("character", character);
				
				GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "uri", movie_uri);
				URI movieURI = new URI(movieNodes[0].getSelf());
				
				GetNodesByLabel[] peopleNodes = Neo4JClient.getNodesByLabelAndProperty("People", "uri", person_uri);
				URI personURI = new URI(peopleNodes[0].getSelf());
				
				if (!SaveDataClient.checkRelationExists(personURI, movieURI, "acts in")) {
					Neo4JClient.createRelationshipWithProperties(personURI, movieURI, "acts in",
							characterMap);
					report.put("movie", movie_uri);
					report.put("person", person_uri);
					report.put("character", character);
					return Response.status(Status.CREATED).entity(gson.toJson(report)).build();

				} else {
					return Response.status(Status.CONFLICT).entity("{\"status\":\"CONFLICT\"}")
							.build();
				}
				
			} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
				// TODO: handle exception
				e.printStackTrace();
				return Response.status(Status.BAD_REQUEST).entity("{\"status\":\"BAD REQUEST\"}").build();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}")
						.build();
			}
			
			
		}

		/**
		 * Delete a movie person relationship
		 */
		@DELETE
		@Path("/{person_uri}")
		@Produces(MediaType.APPLICATION_JSON)
		public Response deleteMoviePerson(@PathParam("movie_uri") String movie_uri,
				@PathParam("person_uri") String person_uri) {
			try {
				Neo4JClient.safeDeleteRelation(movie_uri, person_uri);

				return Response.status(Status.NO_CONTENT).entity("{\"status\":\"NO CONTENT\"}").build();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}")
						.build();
			}
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
		public Response getMainMoviePeople(@PathParam("movie_uri") String movie_uri,
				@DefaultValue("popularity") @QueryParam("order_by") final String orderby,
				@DefaultValue("-1") @QueryParam("limit") final int limit,
				@DefaultValue("false") @QueryParam("include_profile_image") final boolean profile_image,
				@DefaultValue("false") @QueryParam("include_details") final boolean details) {
			try {
				List<Object> nodeList = new ArrayList<Object>();
				Gson gson = new Gson();

				String query = "MATCH(cast:Cast) -[r:`acts in`]-> (movie:Movie {uri:\"" + movie_uri
						+ "\"}) RETURN cast, r ORDER BY cast." + orderby + " DESC";
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
						nodeInfo.put("name", resultMap.get("name"));
						if (profile_image) {
							nodeInfo.put("profile_image", resultMap.get("profile_image"));
						}
					}
					nodeInfo.put("character", relationMap.get("character"));
					nodeList.add(nodeInfo);

				}
				return Response.status(Status.OK).entity(gson.toJson(nodeList)).build();

			} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
				// TODO: handle exception
				e.printStackTrace();
				return Response.status(Status.NOT_FOUND).entity("{\"status\":\"NOT FOUND\"}").build();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}")
						.build();
			}

		}
	}

	@Path("/{movie_uri}/tweets")
	public MovieTweets tweetsSubResource() {
		return new MovieTweets();
	}

	public class MovieTweets {
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Response tweetsResource(@PathParam("movie_uri") String movie_uri,
				@DefaultValue("-1") @QueryParam("limit") final int limit,
				@DefaultValue("1") @QueryParam("page") final int page) {
			List<LinkedTreeMap> tweets = new ArrayList<LinkedTreeMap>();
			try {
				GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "uri", movie_uri);
	
				GetNodeRelationship[] nodeRelationship = Neo4JClient
						.getNodeRelationshipsByType(movieNodes[0].getSelf(), "talks about");
	
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
					tweets.add(propertiesResponse);
				}
	
				Gson gson = new Gson();
				return Response.status(Status.OK).entity(gson.toJson(tweets)).build();
	
			} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
				// TODO: handle exception
				e.printStackTrace();
				return Response.status(Status.NOT_FOUND).entity("{\"status\":\"NOT FOUND\"}").build();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity("{\"status\":\"INTERNAL SERVER ERROR\"}").build();
			}
	
		}
	}
}