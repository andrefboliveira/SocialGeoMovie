package com.socialgeomovie.servlets;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import com.socialgeomovie.config.Neo4JConfig;
import com.socialgeomovie.pojos.Subtitle;
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;
import com.socialgeomovie.utils.Converter;
import com.socialgeomovie.utils.Neo4JRequestException;
import com.uwetrottmann.trakt5.entities.CastMember;
import com.uwetrottmann.trakt5.entities.Movie;

// http://localhost:8080/aw2017/rest
/* Replace with DB Import Servlet */
@Path("movies")
public class DeprecatedMoviesServlet 
{
	private static final Logger logger = LoggerFactory
			.getLogger(DeprecatedMoviesServlet.class);
	
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
		Neo4JConfig.setUniqueConstraints();
		
		Map<String, Integer> report = new HashMap<String, Integer>();
		report.put("movies", 0);
		report.put("cast members", 0);
		report.put("subtitles", 0);
		report.put("tweets", 0);
		
		List<Movie> movies;
		try 
		{
			movies = trakt.getPopularMovies(1, 10);
			int count = movies.size();
			report.put("movies", count);
			
			for(int i=0; i<count; i++)
			{
				Movie movie = movies.get(i);
				
				logger.info("Processing movie: " + movie.title);
				URI movieNode;			
				try 
				{
					movieNode = Neo4JClient.createNodeWithProperties("Movie", Converter.traktMovie2Map(movie));
				} 
				catch (Neo4JRequestException e) 
				{
					GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "id_trakt", movie.ids.trakt);
					movieNode = new URI(movieNodes[0].getSelf());
				}

				List<CastMember> cast = trakt.getCast(movie.ids.trakt+"");
				int castCount = cast.size();
				report.put("cast members", castCount + report.get("cast members"));
				for(int j=0; j<castCount; j++)
				{
					CastMember castMember = cast.get(j);
					logger.info("adding cast :" +castMember.person.name);
					// TODO store cast information
					
					Map<String, Object> castData = Converter.traktCast2Map(castMember);
					String character = (String) castData.get("character");
					castData.remove("character");
					List<String> castLabels = new ArrayList<String>();
					castLabels.add("Cast");
					castLabels.add("Person");


					URI castNode;
					try 
					{
						castNode = Neo4JClient.createNodeWithProperties(castLabels, castData);
					} 
					catch (Neo4JRequestException e) 
					{
						GetNodesByLabel[] castNodes = Neo4JClient.getNodesByLabelAndProperty("Cast", "id_trakt", castMember.person.ids.trakt);
						castNode = new URI(castNodes[0].getSelf());
					}

					Map<String, Object> characterMap = new HashMap<String, Object>();
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
				
				int tweetCount = 3;
//				List<String> tweets = twitterClient.getTweet(movie.title, tweetCount);
//				report.put("tweets", tweets.size() + report.get("tweets"));
				
				logger.info("Import process done");
				Neo4JConfig.cleanDB();
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Gson gson = new Gson();
		logger.info("Movie data imported");
		return Response.status(Status.OK).entity(gson.toJson(report)).build();
	}
	
	
}