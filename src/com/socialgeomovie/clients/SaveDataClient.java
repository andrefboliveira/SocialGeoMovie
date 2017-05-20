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
import java.util.Collections;
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
import com.socialgeomovie.pojos.tmdb.TMDbConfiguration;
import com.socialgeomovie.pojos.tmdb.TMDbMovie;
import com.socialgeomovie.pojos.tmdb.TMDbPerson;
import com.socialgeomovie.utils.Converter;
import com.socialgeomovie.utils.Merge;
import com.socialgeomovie.utils.Neo4JRequestException;
import com.uwetrottmann.trakt5.entities.CastMember;
import com.uwetrottmann.trakt5.entities.Movie;

public class SaveDataClient {

	private static final Logger logger = LoggerFactory.getLogger(SaveDataClient.class);

	private static boolean checkRelationExists(URI startNodeURI, URI endNodeURI, String type)
			throws UnsupportedEncodingException {
		List<String> types = new ArrayList<String>();
		types.add(type);
		GetNodeRelationship[] existingRelations = Neo4JClient.getNodeRelationshipsByType(startNodeURI.toString(),
				types);

		for (GetNodeRelationship getNodeRelationship : existingRelations) {
			if (endNodeURI.toString().equals(getNodeRelationship.getEnd())) {
				return true;
			}
		}
		return false;
	}

	private static Map<String, URI> saveTraktMovies(List<Movie> movies, boolean updateData, boolean override)
			throws UnsupportedEncodingException, URISyntaxException {
		Map<String, URI> moviesURI = new HashMap<String, URI>();

		int count = movies.size();
		for (int i = 0; i < count; i++) {
			Movie movie = movies.get(i);
			logger.info("Processing movie: " + movie.title);

			Map<String, Object> movieMap = Converter.traktMovie2Map(movie);

			URI movieNode;

			try {
				movieNode = Neo4JClient.createNodeWithProperties("Movie", movieMap);
				logger.info("Added movie: " + movie.title);

				moviesURI.put(String.valueOf(movie.ids.trakt), movieNode);
			} catch (Neo4JRequestException e) {
				if (updateData) {
					GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "id_trakt",
							String.valueOf(movie.ids.trakt));
					movieNode = new URI(movieNodes[0].getSelf());

					if (!override) {
						movieMap = Merge.mergeMapCombine(movieNodes[0].getData(), movieMap);
					}

					Neo4JClient.updateNodeProperties(movieNode, movieMap);

					logger.info("Updated movie: " + movie.title);
					moviesURI.put(String.valueOf(movie.ids.trakt), movieNode);

				}

			}

		}

		Neo4JConfig.cleanDB();

		return moviesURI;

	}

	private static Map<String, URI> saveTraktMovieCast(String traktID, URI movieURI, boolean updateData,
			boolean override) throws IOException, URISyntaxException {
		Map<String, URI> castURI = new HashMap<String, URI>();
		List<URI> relationshipURI = new ArrayList<URI>();

		TraktClient trakt = new TraktClient();

		List<CastMember> cast = trakt.getCast(String.valueOf(traktID));
		int castCount = cast.size();

		for (int j = 0; j < castCount; j++) {
			CastMember castMember = cast.get(j);
			logger.info("processing cast: " + castMember.person.name);
			// TODO store cast information
			Map<String, Object> castData = Converter.traktCast2Map(castMember);
			String castCharacter = castMember.character;

			List<String> castLabels = new ArrayList<String>();
			castLabels.add("Cast");
			castLabels.add("Person");

			URI castNode;
			try {
				castNode = Neo4JClient.createNodeWithProperties(castLabels, castData);
				castURI.put(String.valueOf(castMember.person.ids.trakt), castNode);

				Map<String, Object> characterMap = new HashMap<String, Object>();
				characterMap.put("character", castCharacter);
				URI relationship = Neo4JClient.createRelationshipWithProperties(castNode, movieURI, "acts in",
						characterMap);
				relationshipURI.add(relationship);
				logger.info("adding cast: " + castMember.person.name);

			} catch (Neo4JRequestException e) {
				GetNodesByLabel[] castNodes = Neo4JClient.getNodesByLabelAndProperty("Cast", "id_trakt",
						String.valueOf(castMember.person.ids.trakt));
				castNode = new URI(castNodes[0].getSelf());

				if (updateData) {
					if (!override) {
						castData = Merge.mergeMapCombine(castNodes[0].getData(), castData);
					}
					Neo4JClient.updateNodeProperties(castNode, castData);

					logger.info("Updated cast: " + castMember.person.name);
					castURI.put(String.valueOf(castMember.person.ids.trakt), castNode);
				}

				boolean existingRelation = checkRelationExists(castNode, movieURI, "acts in");

				if (!existingRelation) {
					Map<String, Object> characterMap = new HashMap<String, Object>();
					characterMap.put("character", castCharacter);
					URI relationship = Neo4JClient.createRelationshipWithProperties(castNode, movieURI, "acts in",
							characterMap);
					relationshipURI.add(relationship);
					logger.info("adding existing cast: " + castMember.person.name + "to movie: " + traktID);

				}

			}

		}

		Neo4JConfig.cleanDB();

		return castURI;

	}

	public static Map<String, URI> saveAllTraktMovies(int quantity) {
		Neo4JConfig.setUniqueConstraints();

		Map<String, URI> moviesURI = null;

		try {
			TraktClient trakt = new TraktClient();
			List<Movie> movies = trakt.getPopularMovies(1, quantity);

			moviesURI = saveTraktMovies(movies, true, false);
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return moviesURI;
	}

	public static Map<String, URI> saveAllTraktMovieCast() {
		Neo4JConfig.setUniqueConstraints();

		Map<String, URI> movieCastURI = null;
		GetNodesByLabel[] movies = Neo4JClient.getNodesByLabel("Movie");
		for (GetNodesByLabel getNodesByLabel : movies) {
			try {
				URI movieURI = new URI(getNodesByLabel.getSelf());
				String id_trakt = (String) getNodesByLabel.getData().get("id_trakt");
			
				movieCastURI = saveTraktMovieCast(id_trakt, movieURI, true, false);
			} catch (URISyntaxException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return movieCastURI;
	}


	public static void saveTweets() throws UnsupportedEncodingException {
		Neo4JConfig.setUniqueConstraints();

		NewTwitterClient client = new NewTwitterClient();
		
		GetNodesByLabel[] movies = Neo4JClient.getNodesByLabel("Movie");
		for (GetNodesByLabel getNodesByLabel : movies) {
			try {
				URI movieURI = new URI(getNodesByLabel.getSelf());
				String uri = (String) getNodesByLabel.getData().get("uri");
				String title = (String) getNodesByLabel.getData().get("title");
				
				logger.info("Search tweets for movie: " + title);

//				List<HashMap<String, Object>> tweets = client.fetchTweets("#" + uri + " -filter:retweets");
				List<HashMap<String, Object>> tweets = client.fetchTweets(title + " -filter:retweets");


				for (int j = 0; j < tweets.size(); j++) {
					
					HashMap<String, Object> tweet = tweets.get(j);
					String tweetUrl = (String) tweet.get("url");
					
					List<String> tweetLabels = new ArrayList<String>();
					tweetLabels.add("Tweet");
					
					URI tweetNode;
					try {
						tweetNode = Neo4JClient.createNodeWithProperties(tweetLabels, tweet);
						logger.info("adding tweet from: " + tweet.get("user"));
						
					} catch (Neo4JRequestException e) {
						GetNodesByLabel[] tweetNodes = Neo4JClient.getNodesByLabelAndProperty("Tweet", "url",
								tweetUrl);
						tweetNode = new URI(tweetNodes[0].getSelf());
					}
					
					
					boolean existingRelation = checkRelationExists(tweetNode, movieURI, "talks about");

					if (!existingRelation) {
						URI relationship = Neo4JClient.createRelationship(tweetNode, movieURI, "talks about");
						logger.info("Connecting tweet from: " + tweet.get("user") + " to Movie: " + title);
					}
				}
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void addMovieLinks() throws URISyntaxException {
		GetNodesByLabel[] movies = Neo4JClient.getNodesByLabel("Movie");
		for (GetNodesByLabel getNodesByLabel : movies) {
			URI movieURI = new URI(getNodesByLabel.getSelf());
			if (movieURI != null) {
				Map<String, Object> movieProperties = getNodesByLabel.getData();
		
				Map<String, Object> newMovieProperties = Converter.traktMovieLinks(movieProperties);
				Neo4JClient.updateNodeProperties(movieURI, newMovieProperties);
				logger.info("added links to movie: " + movieProperties.get("title"));
			}
		}
	}

	public static void addCastLinks() throws URISyntaxException {

		GetNodesByLabel[] cast = Neo4JClient.getNodesByLabel("Cast");
		for (GetNodesByLabel getNodesByLabel : cast) {
			URI castPersonURI = new URI(getNodesByLabel.getSelf());
			if (castPersonURI != null) {
				Map<String, Object> castProperties = getNodesByLabel.getData();
				Map<String, Object> newCastProperties = Converter.traktCastLinks(castProperties);
				Neo4JClient.updateNodeProperties(castPersonURI, newCastProperties);
				logger.info("added links to person: " + castProperties.get("name"));
			}

		}
	}

	public static List<Subtitle> saveAllMovieSubtitles() {
		OpenSubsClient openSubs = new OpenSubsClient();
		List<Subtitle> subtitles = new ArrayList<Subtitle>();

		GetNodesByLabel[] movies = Neo4JClient.getNodesByLabel("Movie");
		for (GetNodesByLabel getNodesByLabel : movies) {
			try {
				URI movieURI = new URI(getNodesByLabel.getSelf());
				String id_imdb = (String) getNodesByLabel.getData().get("id_imdb");
				// String id_imdb = (String)
				// Neo4JClient.getNodeProperty(movieURI, "id_imdb");
				Subtitle subtitle = openSubs.getSubtitle(id_imdb);
				subtitles.add(subtitle);

			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return subtitles;
	}

	public static void addOMDbData() throws IOException, URISyntaxException {
		GetNodesByLabel[] movies = Neo4JClient.getNodesByLabel("Movie");
		for (GetNodesByLabel getNodesByLabel : movies) {
			Map<String, Object> movieProperties = getNodesByLabel.getData();
			String id_imdb = (String) movieProperties.get("id_imdb");

			if (id_imdb != null && !id_imdb.equals("")) {
				logger.info("Search OMDb for id: " + id_imdb);
				Map<String, Object> omdbProperties = OMDbClient.getOMDbMovie(id_imdb);

				Map<String, Object> resultMap = Merge.mergeMapCombine(movieProperties,
						Converter.omdbMap(omdbProperties));

				Neo4JClient.updateNodeProperties(new URI(getNodesByLabel.getSelf()), resultMap);
				logger.info("Added OMDb info for: " + movieProperties.get("title"));

			}
		}

	}

	public static void addTMDbMovieData()
			throws URISyntaxException, NumberFormatException, UnsupportedEncodingException {
		GetNodesByLabel[] movies = Neo4JClient.getNodesByLabel("Movie");
		for (GetNodesByLabel getNodesByLabel : movies) {
			Map<String, Object> movieProperties = getNodesByLabel.getData();
			String id_tmdb = (String) movieProperties.get("id_tmdb");

			if (id_tmdb != null && !id_tmdb.equals("")) {
				logger.info("Search TMDb Movie for id: " + id_tmdb);

				TMDbClient tmdb = new TMDbClient();
				TMDbMovie movie = tmdb.getMovie(Integer.valueOf(id_tmdb));

				Map<String, Object> resultMap = Merge.mergeMapCombine(movieProperties,
						Converter.tmdbMovie2Map(movie, tmdb.getConfiguration()));

				Neo4JClient.updateNodeProperties(new URI(getNodesByLabel.getSelf()), resultMap);
				logger.info("Added TMDb Movie info for: " + movieProperties.get("title"));
			}

		}
	}

	public static void addTMDbCastData()
			throws URISyntaxException, NumberFormatException, UnsupportedEncodingException {
		GetNodesByLabel[] cast = Neo4JClient.getNodesByLabel("Cast");
		for (GetNodesByLabel getNodesByLabel : cast) {
			Map<String, Object> castProperties = getNodesByLabel.getData();
			String id_tmdb = (String) castProperties.get("id_tmdb");

			if (id_tmdb != null && !id_tmdb.equals("")) {
				logger.info("Search TMDb People for id: " + id_tmdb);

				TMDbClient tmdb = new TMDbClient();
				TMDbPerson person = tmdb.getPerson(Integer.valueOf(id_tmdb));

				Map<String, Object> resultMap = Merge.mergeMapCombine(castProperties,
						Converter.tmdbPerson2Map(person, tmdb.getConfiguration()));
				
				logger.info(resultMap.values().toString());		
				

				Neo4JClient.updateNodeProperties(new URI(getNodesByLabel.getSelf()), resultMap);
				logger.info("Added TMDb People info for: " + castProperties.get("name"));
			}

		}
	}

}
