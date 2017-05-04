package com.socialgeomovie.servlets;

import java.io.IOException;
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
import com.socialgeomovie.clients.SaveDataClient;
import com.socialgeomovie.clients.TwitterClient;
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
		public Response importTraktMovies(@DefaultValue("10") @QueryParam("quantity") final int quantity) 
		{
			Map<String, URI> moviesReport = SaveDataClient.saveAllTraktMovies(quantity);
			Map<String, String> report = new HashMap<String,String>();
			report.put("status", "OK");
			report.put("movies", ""+moviesReport.size());
			Gson gson = new Gson();
			return Response.status(Status.OK).entity(gson.toJson(report)).build();
		}
		
		@GET
		@Path("/omdb")
		@Produces(MediaType.APPLICATION_JSON)
		public Response importOMDBMovies() 
		{	
			
				try {
					SaveDataClient.addingOMDbData();
				} catch (IOException | URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			Map<String, String> report = new HashMap<String,String>();
			report.put("status", "OK");
			Gson gson = new Gson();
			return Response.status(Status.OK).entity(gson.toJson(report)).build();
		}
		
		@GET
		@Path("/process")
		@Produces(MediaType.APPLICATION_JSON)
		public Response processMovies() 
		{	
			try {
				SaveDataClient.addMovieLinks();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map<String, String> report = new HashMap<String,String>();
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
		/**
		 * Import Cast Data
		 */
		@GET
		@Path("/cast")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response importTraktCast() 
		{
			Map<String, URI> castReport = SaveDataClient.saveAllTraktMovieCast();
			Map<String, String> report = new HashMap<String,String>();
			report.put("status", "OK");
			report.put("cast", ""+castReport.size());
			Gson gson = new Gson();
			return Response.status(Status.OK).entity(gson.toJson(report)).build();
		}
		
		
		@GET
		@Path("/process")
		@Produces(MediaType.APPLICATION_JSON)
		public Response processCast() 
		{	
			try {
				SaveDataClient.addCastLinks();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map<String, String> report = new HashMap<String,String>();
			report.put("status", "OK");
			Gson gson = new Gson();
			return Response.status(Status.OK).entity(gson.toJson(report)).build();
		}
	}

	@GET
    @Path("/tweets")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response importTweets(@PathParam("movie_id") int movie_id) 
    {
//        TwitterClient twitterClient = new TwitterClient();

        // TODO needs to fetch the movie title from the database
        try {
            List<String> tweets = TwitterClient.getTweets("movie.title");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Gson gson = new Gson();
        return Response.status(Status.OK).entity("gson.toJson(report)").build();
    }
}
