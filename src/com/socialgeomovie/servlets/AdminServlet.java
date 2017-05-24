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
import com.socialgeomovie.SaveLocationData;
import com.socialgeomovie.clients.Neo4JClient;
import com.socialgeomovie.clients.NewTwitterClient;
import com.socialgeomovie.clients.SaveDataClient;
import com.socialgeomovie.pojos.neo4j.GetNodeRelationship;
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;
import com.socialgeomovie.servlets.MovieServlet.MoviePeople;
import com.socialgeomovie.utils.exceptions.OMDbRequestException;
import com.socialgeomovie.utils.exceptions.TMDbRequestException;

@Path("/db")
public class AdminServlet {
	// http://localhost:8080/aw2017/rest/db
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkServiceRunning() {
		// TODO Return type
		try{
		Neo4JClient.checkDatabaseIsRunning();
		return Response.status(Status.OK).entity("{\"status\":\"OK\"}").build();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}").build();
		}
	}

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
			try {
				Map<String, URI> moviesReport = SaveDataClient.saveAllTraktMovies(quantity);
				Map<String, String> report = new HashMap<String, String>();
				report.put("status", "OK");
				report.put("movies", "" + moviesReport.size());
				Gson gson = new Gson();
				return Response.status(Status.OK).entity(gson.toJson(report)).build();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}").build();

			}

		}

		@GET
		@Path("/omdb")
		@Produces(MediaType.APPLICATION_JSON)
		public Response importOMDbMovies() {
			try {
				Map<String, URI> moviesReport = SaveDataClient.addOMDbData();
				Map<String, String> report = new HashMap<String, String>();
				report.put("status", "OK");
				report.put("movies_omdb", "" + moviesReport.size());
				
				Gson gson = new Gson();
				return Response.status(Status.OK).entity(gson.toJson(report)).build();
			} catch (OMDbRequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Response.status(Status.SERVICE_UNAVAILABLE).entity("{\"status\":\"SERVICE UNAVAILABLE\"}").build();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}").build();
			}

		}

		@GET
		@Path("/tmdb")
		@Produces(MediaType.APPLICATION_JSON)
		public Response importTMDbMovies() {
			try {
				Map<String, URI> moviesReport =SaveDataClient.addTMDbMovieData();
				Map<String, String> report = new HashMap<String, String>();
				report.put("status", "OK");
				report.put("movies_tmdb", "" + moviesReport.size());
				Gson gson = new Gson();
				return Response.status(Status.OK).entity(gson.toJson(report)).build();
			} catch (TMDbRequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Response.status(Status.SERVICE_UNAVAILABLE).entity("{\"status\":\"SERVICE UNAVAILABLE\"}").build();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}").build();
			}

		}
		
		@GET
		@Path("/dbpedia")
		@Produces(MediaType.APPLICATION_JSON)
		public Response importDBpediaMovies() {
			try {
				Map<String, URI> moviesReport = SaveDataClient.addDBpediaMovieData();
				Map<String, String> report = new HashMap<String, String>();
				report.put("status", "OK");
				report.put("movies_dbpedia", "" + moviesReport.size());
				Gson gson = new Gson();
				return Response.status(Status.OK).entity(gson.toJson(report)).build();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}").build();
			}

		}

		@GET
		@Path("/process")
		@Produces(MediaType.APPLICATION_JSON)
		public Response processMovies() {
			try {
				Map<String, String> report = new HashMap<String, String>();
				SaveDataClient.addMovieLinks();
				SaveDataClient.addMovieDateRelation();
				report.put("status", "OK");
				Gson gson = new Gson();
				return Response.status(Status.OK).entity(gson.toJson(report)).build();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}").build();
			}

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
				try{
					Map<String, URI> castReport = SaveDataClient.saveAllTraktMovieCast();
					Map<String, String> report = new HashMap<String, String>();
					report.put("status", "OK");
					report.put("cast", "" + castReport.size());
					Gson gson = new Gson();
					return Response.status(Status.OK).entity(gson.toJson(report)).build();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}").build();
				}
			}
	
	
			@GET
			@Path("/tmdb")
			@Produces(MediaType.APPLICATION_JSON)
			public Response importTMDbCast() {
	
				try {
					Map<String, URI> castReport = SaveDataClient.addTMDbCastData();
					Map<String, String> report = new HashMap<String, String>();
					report.put("status", "OK");
					report.put("cast_tmdb", "" + castReport.size());
					Gson gson = new Gson();
					return Response.status(Status.OK).entity(gson.toJson(report)).build();
				} catch (TMDbRequestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return Response.status(Status.SERVICE_UNAVAILABLE).entity("{\"status\":\"SERVICE UNAVAILABLE\"}").build();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}").build();
				}
	
			}
			
			@GET
			@Path("/dbpedia")
			@Produces(MediaType.APPLICATION_JSON)
			public Response importDBpediaCast() {
	
				try {
					Map<String, URI> castReport = SaveDataClient.addDBpediaCastData();
					Map<String, String> report = new HashMap<String, String>();
					report.put("status", "OK");
					report.put("cast_dbpedia", "" + castReport.size());
					Gson gson = new Gson();
					return Response.status(Status.OK).entity(gson.toJson(report)).build();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}").build();
				}
	
			}
	
		}
	
	
		@GET
		@Path("/process")
		@Produces(MediaType.APPLICATION_JSON)
		public Response processCast() {
			try {
				SaveDataClient.addCastLinks();
				SaveDataClient.addCastDateRelation();
	
				Map<String, String> report = new HashMap<String, String>();
				report.put("status", "OK");
				Gson gson = new Gson();
				return Response.status(Status.OK).entity(gson.toJson(report)).build();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}").build();
			}
		}
	}

	@GET
	@Path("/tweets")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response importTweets() {
	
		try {
			SaveDataClient.saveTweets();
			
			Map<String, String> report = new HashMap<String, String>();
			report.put("status", "OK");
			Gson gson = new Gson();
			return Response.status(Status.OK).entity(gson.toJson(report)).build();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}").build();
		}
	}
	
	@GET
	@Path("/natural_language")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response importNaturalLanguageResults() {
		Map<String, String> report = new HashMap<String, String>();
		report.put("status", "OK");
		try {
			SaveLocationData.saveData();
	
			Gson gson = new Gson();
			return Response.status(Status.OK).entity(gson.toJson(report)).build();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"status\":\"INTERNAL SERVER ERROR\"}").build();
		}
	}
}