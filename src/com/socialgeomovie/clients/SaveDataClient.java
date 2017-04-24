package com.socialgeomovie.clients;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.socialgeomovie.config.Neo4JConfig;
import com.socialgeomovie.pojos.Subtitle;
import com.socialgeomovie.pojos.neo4j.GetNodeRelationship;
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;
import com.socialgeomovie.servlets.DeprecatedMoviesServlet;
import com.socialgeomovie.utils.Converter;
import com.socialgeomovie.utils.Neo4JRequestException;
import com.uwetrottmann.trakt5.entities.CastMember;
import com.uwetrottmann.trakt5.entities.Movie;

public class SaveDataClient {

	private static final Logger logger = LoggerFactory.getLogger(SaveDataClient.class);
	
	

	private static Map<String, URI> saveMovies(List<Movie> movies)
			throws UnsupportedEncodingException, URISyntaxException {
		Map<String, URI> moviesURI = new HashMap<String, URI>();

		int count = movies.size();
		for (int i = 0; i < count; i++) {
			Movie movie = movies.get(i);
			logger.info("Processing movie: " + movie.title);

			// TODO store movie data

			URI movieNode;

			try
			{
				Map<String, Object> movieMap = Converter.movie2Map(movie);
						
						
						
						
				URL url;
				try
				{
					url = new URL("http://www.omdbapi.com/?i="+movie.ids.imdb);
					InputStreamReader reader = new InputStreamReader(url.openStream());
			        HashMap extraInfoMap = new Gson().fromJson(reader, new HashMap<String,Object>().getClass());
			        movieMap.put("poster",extraInfoMap.get("Poster"));
				} 
				catch (IOException e)
				{

				}
		        
		        
		        
				movieNode = Neo4JClient.createNodeWithProperties("Movie", movieMap);
				logger.info("Added movie: " + movie.title);

				moviesURI.put(String.valueOf(movie.ids.trakt), movieNode);
			} catch (Neo4JRequestException e) {
				GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "id_trakt",
						String.valueOf(movie.ids.trakt));
				movieNode = new URI(movieNodes[0].getSelf());
			}

		}
		
		Neo4JConfig.cleanDB();
		
		return moviesURI;

	}
	
	private static boolean checkRelationExists(URI movieURI, URI castNodeURI) throws UnsupportedEncodingException{
		List<String> types = new ArrayList<String>();
		types.add("acts in");
		GetNodeRelationship[] existingRelations = Neo4JClient.getNodeRelationshipsByType(castNodeURI, types);
		
		for (GetNodeRelationship getNodeRelationship : existingRelations) {
			if (movieURI.toString().equals(getNodeRelationship.getEnd())){
				return true;
			}
		}
		return false;
	}

	private static Map<String, URI> saveMovieCast(String traktID, URI movieURI) {
		Map<String, URI> castURI = new HashMap<String, URI>();
		List<URI> relationshipURI = new ArrayList<URI>();

		TraktClient trakt = new TraktClient();
		try {
			List<CastMember> cast = trakt.getCast(String.valueOf(traktID));
			int castCount = cast.size();
			
			for(int j=0; j<castCount; j++)
			{
				CastMember castMember = cast.get(j);
				logger.info("processing cast :" + castMember.person.name);
				// TODO store cast information
				
				Map<String, Object> castData = Converter.cast2Map(castMember);
				String character = (String) castData.get("character");
				castData.remove("character");
				List<String> castLabels = new ArrayList<String>();
				castLabels.add("Cast");
				castLabels.add("Person");
				
				
				URI castNode;
				try {
					castNode = Neo4JClient.createNodeWithProperties(castLabels, castData);
					castURI.put(String.valueOf(castMember.person.ids.trakt), castNode);
					
					Map<String, Object> characterMap = new HashMap<String, Object>();
					characterMap.put("character", character);
					URI relationship = Neo4JClient.createRelationshipWithProperties(castNode, movieURI, "acts in", characterMap);
					relationshipURI.add(relationship);
					logger.info("adding cast :" +castMember.person.name);

					
				} catch (Neo4JRequestException e) {
					GetNodesByLabel[] castNodes = Neo4JClient.getNodesByLabelAndProperty("Cast", "id_trakt", String.valueOf(castMember.person.ids.trakt));
					castNode = new URI(castNodes[0].getSelf());
					
					boolean existingRelation = checkRelationExists(movieURI, castNode);
					
					if (!existingRelation) {
						Map<String, Object> characterMap = new HashMap<String, Object>();
						characterMap.put("character", character);
						URI relationship = Neo4JClient.createRelationshipWithProperties(castNode, movieURI, "acts in", characterMap);
						relationshipURI.add(relationship);
						logger.info("adding existing cast :" +castMember.person.name);

					}
					
				}

				

			}

		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Neo4JConfig.cleanDB();
		
		return castURI;

	}

	public static Map<String, URI> saveAllMovies(int quantity) 
	{
		Map<String, URI> moviesURI = null;

		try
		{
			TraktClient trakt = new TraktClient();
			List<Movie> movies = trakt.getPopularMovies(1, quantity);

			moviesURI = saveMovies(movies);
		} 
		catch (IOException | URISyntaxException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return moviesURI;
	}

	public static Map<String, URI> saveAllMovieCast() {
		Map<String, URI> movieCastURI = null;
		GetNodesByLabel[] movies = Neo4JClient.getNodesByLabel("Movie");
		for (GetNodesByLabel getNodesByLabel : movies) {
			try {
				URI movieURI = new URI(getNodesByLabel.getSelf());
				String id_trakt = (String) Neo4JClient.getNodeProperty(movieURI, "id_trakt");
				movieCastURI = saveMovieCast(id_trakt, movieURI);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return movieCastURI;
	}

	public static List<Subtitle> saveAllMovieSubtitles() {
		OpenSubsClient openSubs = new OpenSubsClient();
		List<Subtitle> subtitles = new ArrayList<Subtitle>();

		GetNodesByLabel[] movies = Neo4JClient.getNodesByLabel("Movie");
		for (GetNodesByLabel getNodesByLabel : movies) {
			try {
				URI movieURI = new URI(getNodesByLabel.getSelf());
				String id_imdb = (String) Neo4JClient.getNodeProperty(movieURI, "id_imdb");
				Subtitle subtitle = openSubs.getSubtitle(id_imdb);
				subtitles.add(subtitle);

			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return subtitles;
	}

	private static Map<String, URI> saveAll() {
		TraktClient trakt = new TraktClient();
		OpenSubsClient openSubs = new OpenSubsClient();
		TwitterClient twitterClient = new TwitterClient();

		try {
			List<Movie> movies = trakt.getPopularMovies(1, 10);
			Map<String, URI> moviesURI = saveMovies(movies);
			Map<String, URI> movieCastURI = new HashMap<String, URI>();
			for (Entry<String, URI> movie : moviesURI.entrySet()) {
				movieCastURI.putAll(saveMovieCast(movie.getKey(), movie.getValue()));

			}
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	private static Response importAll() {
		TraktClient trakt = new TraktClient();
		OpenSubsClient openSubs = new OpenSubsClient();
		TwitterClient twitterClient = new TwitterClient();
		
		Map<String, Integer> report = new HashMap<String, Integer>();
		
		Neo4JConfig.setUniqueConstraints();
		
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
				URI movieNode;			
				
				try {
					movieNode = Neo4JClient.createNodeWithProperties("Movie", Converter.movie2Map(movie));
				} catch (Neo4JRequestException e) {
					GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "id_trakt", String.valueOf(movie.ids.trakt));
					movieNode = new URI(movieNodes[0].getSelf());
					logger.info(movieNode.toString());
				}
				
				
				//System.out.println(movie.ids.imdb);
				List<CastMember> cast = trakt.getCast(String.valueOf(movie.ids.trakt));
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
					List<String> castLabels = new ArrayList<String>();
					castLabels.add("Cast");
					castLabels.add("Person");
					
					
					URI castNode;
					try {
						castNode = Neo4JClient.createNodeWithProperties(castLabels, castData);
						
					} catch (Neo4JRequestException e) {
						GetNodesByLabel[] castNodes = Neo4JClient.getNodesByLabelAndProperty("Cast", "id_trakt", String.valueOf(castMember.person.ids.trakt));
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
				
//				int tweetCount = 10;
//				twitterClient.getTweet(tweetCount);
//				report.put("tweets", tweetCount + report.get("tweets"));
				
				logger.info("Import process done");
			}
		}
		catch (IOException | /*InterruptedException | */URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Gson gson = new Gson();
		logger.info("Movie data imported");
		return Response.status(Status.OK).entity(gson.toJson(report)).build();
	}


}
