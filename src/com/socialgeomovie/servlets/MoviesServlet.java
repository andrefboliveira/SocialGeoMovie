package com.socialgeomovie.servlets;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.socialgeomovie.clients.Neo4JClient;
import com.socialgeomovie.clients.OpenSubsClient;
import com.socialgeomovie.clients.TraktClient;
import com.socialgeomovie.clients.TwitterClient;
import com.socialgeomovie.pojos.Subtitle;
import com.socialgeomovie.utils.Converter;
import com.uwetrottmann.trakt5.entities.CastMember;
import com.uwetrottmann.trakt5.entities.Movie;

// http://localhost:8080/aw2017/rest/movies

@Path("movies")
public class MoviesServlet 
{
	private static final Logger logger = LoggerFactory
			.getLogger(MoviesServlet.class);
	
	@GET
	@Produces("application/json")
	public Response testGet()
	{
		
		return Response.status(Status.OK).entity("{\"status\":\"OK\"}").build();
	}
	
	@GET
	@Path("import")
	@Produces("application/json")
	public Response importData()
	{
		TraktClient trakt = new TraktClient();
		OpenSubsClient openSubs = new OpenSubsClient();
		TwitterClient twitterClient = new TwitterClient();
		//Neo4JClient.setUniquenessConstraint("Movie", "imdb");
		
		Map<String, Integer> report = new HashMap<String, Integer>();
		
		List<Movie> movies;
		try 
		{
			movies = trakt.getPopularMovies(1, 10);
			
			int count = movies.size();
			report.put("movies", count);
			report.put("cast members", 0);
			report.put("subtitles", 0);
			report.put("tweets", 0);
			for(int i=0; i<count; i++)
			{
				Movie movie = movies.get(i);
				
				logger.info("Processing movie: " + movie.title);
				// TODO store movie data
				//Neo4JClient.createNodeWithProperties("Movie", m);
				URI movieNode = Neo4JClient.createNodeWithProperties("Movie", Converter.movie2Map(movie));
				
				
				
				//System.out.println(movie.ids.imdb);
				List<CastMember> cast = trakt.getCast(movie.ids.trakt+"");
				int castCount = cast.size();
				report.put("cast members", castCount + report.get("cast members"));
				for(int j=0; j<castCount; j++)
				{
					CastMember castMember = cast.get(j);
					logger.info("adding cast :" +castMember.person.name);
					// TODO store cast information
					
					Map<String, Object> castData = Converter.cast2Map(castMember);
					String character = (String) castData.get("character");
					castData.remove("character");
					URI castNode = Neo4JClient.createNodeWithProperties("Cast", castData);
					Map<String, String> characterMap = new HashMap<String, String>();
					characterMap.put("character", character);
					URI relationship = Neo4JClient.createRelationshipWithProperties(castNode, movieNode, "acts in", characterMap);
				}

				// TODO get subtitles
				Subtitle subtitle = openSubs.getSubtitle(movie.ids.imdb);
				if(subtitle != null)
				{
					report.put("subtitles", 1 + report.get("subtitles"));
					logger.info("Downloded movie substitle");
					//logger.info(subtitle.toString());
					// TODO process subtitles
					// TODO store subtitle information
				}
				
				int tweetCount = 10;
				twitterClient.getTweet(tweetCount);
				report.put("tweets", tweetCount + report.get("tweets"));
				
				logger.info("Import process done");
			}
		}
		catch (IOException | InterruptedException | URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Gson gson = new Gson();
		logger.info("Movie data imported");
		return Response.status(Status.OK).entity(gson.toJson(report)).build();
	}
	
	
}