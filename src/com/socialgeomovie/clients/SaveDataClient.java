package com.socialgeomovie.clients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
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
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;
import com.socialgeomovie.servlets.DeprecatedMoviesServlet;
import com.socialgeomovie.utils.Converter;
import com.socialgeomovie.utils.Neo4JRequestException;
import com.uwetrottmann.trakt5.entities.CastMember;
import com.uwetrottmann.trakt5.entities.Movie;

public class SaveDataClient {

	private static final Logger logger = LoggerFactory.getLogger(SaveDataClient.class);

	private static Map<Integer, URI> saveMovies(List<Movie> movies)
			throws UnsupportedEncodingException, URISyntaxException {
		Map<Integer, URI> moviesURI = new HashMap<Integer, URI>();

		int count = movies.size();
		for (int i = 0; i < count; i++) {
			Movie movie = movies.get(i);
			logger.info("Processing movie: " + movie.title);

			// TODO store movie data

			URI movieNode;

			try {
				movieNode = Neo4JClient.createNodeWithProperties("Movie", Converter.movie2Map(movie));
				logger.info("Added movie: " + movie.title);

				moviesURI.put(movie.ids.trakt, movieNode);
			} catch (Neo4JRequestException e) {
//				GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "id_trakt",
//						movie.ids.trakt);
//				movieNode = new URI(movieNodes[0].getSelf());
			}

		}
		
		Neo4JConfig.cleanDB();
		
		return moviesURI;

	}

	private static Map<Integer, URI> saveMovieCast(Integer traktID, URI movieURI) {
		Map<Integer, URI> castURI = new HashMap<Integer, URI>();
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
					logger.info("adding cast :" +castMember.person.name);

					
				} catch (Neo4JRequestException e) {
					GetNodesByLabel[] castNodes = Neo4JClient.getNodesByLabelAndProperty("Cast", "id_trakt", castMember.person.ids.trakt);
					castNode = new URI(castNodes[0].getSelf());
				}

				Map<String, Object> characterMap = new HashMap<String, Object>();
				characterMap.put("character", character);
				URI relationship = Neo4JClient.createRelationshipWithProperties(castNode, movieURI, "acts in", characterMap);


				castURI.put(castMember.person.ids.trakt, castNode);
				relationshipURI.add(relationship);

			}

		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Neo4JConfig.cleanDB();
		
		return castURI;

	}

	public static Map<Integer, URI> saveAllMovies() {
		Map<Integer, URI> moviesURI = null;

		try {
			TraktClient trakt = new TraktClient();
			List<Movie> movies = trakt.getPopularMovies(1, 10);

			moviesURI = saveMovies(movies);
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return moviesURI;
	}

	public static Map<Integer, URI> saveAllMovieCast() {
		Map<Integer, URI> movieCastURI = null;
		GetNodesByLabel[] movies = Neo4JClient.getNodesByLabel("Movie");
		for (GetNodesByLabel getNodesByLabel : movies) {
			try {
				URI movieURI = new URI(getNodesByLabel.getSelf());
				Number id_trakt_response = (Number) Neo4JClient.getNodeProperty(movieURI, "id_trakt");
				int id_trakt = id_trakt_response.intValue();
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

	private static Map<Integer, URI> saveAll() {
		TraktClient trakt = new TraktClient();
		OpenSubsClient openSubs = new OpenSubsClient();
		TwitterClient twitterClient = new TwitterClient();

		try {
			List<Movie> movies = trakt.getPopularMovies(1, 10);
			Map<Integer, URI> moviesURI = saveMovies(movies);
			Map<Integer, URI> movieCastURI = new HashMap<Integer, URI>();
			for (Entry<Integer, URI> movie : moviesURI.entrySet()) {
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
					GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "id_trakt", movie.ids.trakt);
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
