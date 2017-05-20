package com.socialgeomovie.servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;
import com.socialgeomovie.clients.Neo4JClient;
import com.socialgeomovie.clients.NewTwitterClient;
import com.socialgeomovie.clients.SaveDataClient;
import com.socialgeomovie.clients.TwitterClient;
import com.socialgeomovie.pojos.neo4j.GetNodeRelationship;
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;
import com.socialgeomovie.servlets.MovieServlet.MoviePeople;

@Path("/db")
public class AdminServlet {
	// http://localhost:8080/aw2017/rest/db

	@Path("/movies")
	public ImportMovies moviesSubResource() {
		return new ImportMovies();
	}

	public class ImportMovies {
		/**
		 * Import Movies Data
		 */
		@GET
		@Path("/trakt")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response importTraktMovies(@DefaultValue("10") @QueryParam("quantity") final int quantity) {
			Map<String, URI> moviesReport = SaveDataClient.saveAllTraktMovies(quantity);
			Map<String, String> report = new HashMap<String, String>();
			report.put("status", "OK");
			report.put("movies", "" + moviesReport.size());
			Gson gson = new Gson();
			return Response.status(Status.OK).entity(gson.toJson(report)).build();
		}

		@GET
		@Path("/omdb")
		@Produces(MediaType.APPLICATION_JSON)
		public Response importOMDbMovies() {
			Map<String, String> report = new HashMap<String, String>();
			try {
				SaveDataClient.addOMDbData();
				report.put("status", "OK");
				Gson gson = new Gson();
				return Response.status(Status.OK).entity(gson.toJson(report)).build();
			} catch (IOException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@GET
		@Path("/tmdb")
		@Produces(MediaType.APPLICATION_JSON)
		public Response importTMDbMovies() {
			Map<String, String> report = new HashMap<String, String>();

			try {
				SaveDataClient.addTMDbMovieData();
				report.put("status", "OK");
				Gson gson = new Gson();
				return Response.status(Status.OK).entity(gson.toJson(report)).build();
			} catch (NumberFormatException | UnsupportedEncodingException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@GET
		@Path("/process")
		@Produces(MediaType.APPLICATION_JSON)
		public Response processMovies() {
			try {
				SaveDataClient.addMovieLinks();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map<String, String> report = new HashMap<String, String>();
			report.put("status", "OK");
			Gson gson = new Gson();
			return Response.status(Status.OK).entity(gson.toJson(report)).build();
		}
	}

	@Path("/people")
	public ImportPeople peopleSubResource() {
		return new ImportPeople();
	}

	public class ImportPeople {
		
		@Path("/cast")
		public ImportCast castSubResource() {
			return new ImportCast();
		}
		
		public class ImportCast {
			/**
			 * Import Cast Data
			 */
			@GET
			@Path("/trakt")
			@Consumes(MediaType.APPLICATION_JSON)
			@Produces(MediaType.APPLICATION_JSON)
			public Response importTraktCast() {
				Map<String, URI> castReport = SaveDataClient.saveAllTraktMovieCast();
				Map<String, String> report = new HashMap<String, String>();
				report.put("status", "OK");
				report.put("cast", "" + castReport.size());
				Gson gson = new Gson();
				return Response.status(Status.OK).entity(gson.toJson(report)).build();
			}
			
			
			@GET
			@Path("/tmdb")
			@Produces(MediaType.APPLICATION_JSON)
			public Response importTMDbCast() {
				Map<String, String> report = new HashMap<String, String>();

				try {
					SaveDataClient.addTMDbCastData();
					report.put("status", "OK");
					Gson gson = new Gson();
					return Response.status(Status.OK).entity(gson.toJson(report)).build();
				} catch (NumberFormatException | UnsupportedEncodingException | URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
			}
			
		}
		

		@GET
		@Path("/process")
		@Produces(MediaType.APPLICATION_JSON)
		public Response processCast() {
			try {
				SaveDataClient.addCastLinks();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map<String, String> report = new HashMap<String, String>();
			report.put("status", "OK");
			Gson gson = new Gson();
			return Response.status(Status.OK).entity(gson.toJson(report)).build();
		}
	}

	@GET
	@Path("/tweets")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response importTweets() {
		SaveDataClient.saveTweets();

		/*
		 * GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabel("Movie");
		 * NewTwitterClient client = new NewTwitterClient(); for(int i=0;
		 * i<movieNodes.length; i++) { GetNodesByLabel movie = movieNodes[i];
		 * try { URI movieURI = new URI(movie.getSelf()); String a =
		 * movie.getProperties(); Map<String, Object> nodeRelationship =
		 * Neo4JClient.getNodeProperties(a);
		 * 
		 * String uri = (String) nodeRelationship.get("uri");
		 * List<HashMap<String, Object>> tweets = client.fetchTweets("#" + uri +
		 * " -filter:retweets");
		 * 
		 * 
		 * 
		 * System.out.println("asdf"); } catch (URISyntaxException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * 
		 * }
		 */

		// client.fetchTweets(movie_id+"");
		// List<HashMap<String, Object>> tweets =
		// client.fetchTweets("#guardiansofthegalaxy");

		/*
		 * try { //GetNodesByLabel[] movieNodes =
		 * Neo4JClient.getNodesByLabelAndProperty("Movie", "uri", movie_uri);
		 * System.out.println("asdf"); } catch (UnsupportedEncodingException e)
		 * { // TODO Auto-generated catch block e.printStackTrace(); }
		 */

		// nodeInfo.putAll((movieNodes[0].getData()));

		/*
		 * // TwitterClient twitterClient = new TwitterClient();
		 * 
		 * // TODO needs to fetch the movie title from the database try {
		 * List<String> tweets = TwitterClient.getTweets("movie.title"); } catch
		 * (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		Gson gson = new Gson();
		return Response.status(Status.OK).entity("gson.toJson(tweets)").build();

	}
}
