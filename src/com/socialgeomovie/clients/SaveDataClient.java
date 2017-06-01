package com.socialgeomovie.clients;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.socialgeomovie.config.Neo4JConfig;
import com.socialgeomovie.pojos.Subtitle;
import com.socialgeomovie.pojos.neo4j.GetNodeRelationship;
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;
import com.socialgeomovie.pojos.sparql.BindingMovie;
import com.socialgeomovie.pojos.sparql.DBpediaMovieResult;
import com.socialgeomovie.pojos.sparql.DBpediaPersonResult;
import com.socialgeomovie.pojos.tmdb.ProductionCountry;
import com.socialgeomovie.pojos.tmdb.TMDbConfiguration;
import com.socialgeomovie.pojos.tmdb.TMDbMovie;
import com.socialgeomovie.pojos.tmdb.TMDbPerson;
import com.socialgeomovie.utils.Converter;
import com.socialgeomovie.utils.MapUtils;
import com.socialgeomovie.utils.exceptions.Neo4JRequestException;
import com.uwetrottmann.trakt5.entities.CastMember;
import com.uwetrottmann.trakt5.entities.Movie;

public class SaveDataClient {

	private static final Logger logger = LoggerFactory.getLogger(SaveDataClient.class);

	public static boolean checkRelationExists(URI startNodeURI, URI endNodeURI, String type)
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
						movieMap = MapUtils.mergeMapCombine(movieNodes[0].getData(), movieMap);
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
						castData = MapUtils.mergeMapCombine(castNodes[0].getData(), castData);
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
					
					
					URI tweetNode;
					try {
						tweetNode = Neo4JClient.createNodeWithProperties("Tweet", tweet);
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
	
	public static void addMovieDateRelation() throws URISyntaxException, UnsupportedEncodingException {
		Neo4JConfig.setUniqueConstraints();
		
		GetNodesByLabel[] movies = Neo4JClient.getNodesByLabel("Movie");
		for (GetNodesByLabel getNodesByLabel : movies) {
			URI movieURI = new URI(getNodesByLabel.getSelf());
			if (movieURI != null) {
				Map<String, Object> movieProperties = getNodesByLabel.getData();
				Object releaseObject = movieProperties.get("released");
						
				addDateRelation(releaseObject, movieURI, "released in", (String) movieProperties.get("title"));
				
				
			}
		}
	}

	public static void addCastDateRelation() throws URISyntaxException, UnsupportedEncodingException {
		Neo4JConfig.setUniqueConstraints();
		
		GetNodesByLabel[] cast = Neo4JClient.getNodesByLabel("Cast");
		for (GetNodesByLabel getNodesByLabel : cast) {
			URI castURI = new URI(getNodesByLabel.getSelf());
			if (castURI != null) {
				Map<String, Object> castProperties = getNodesByLabel.getData();
				
				addDateRelation(castProperties.get("birthday"), castURI, "born in", (String) castProperties.get("name"));
				addDateRelation(castProperties.get("deathday"), castURI, "dead in", (String) castProperties.get("name"));


				
				
			}
		}
	}
	
	private static void addDateRelation(Object dateObject, URI nodeURI, String relationName, String nodeTag) throws URISyntaxException, UnsupportedEncodingException {
		String dateString = null;
		
		if (dateObject instanceof String) {
			dateString = (String) dateObject;
		} else  if (dateObject instanceof List<?>) {
			dateString = (String) ((List) dateObject).get(0);
		}
		
		if (dateString != null && !("".equals(dateString))) {
			try {
				DateTime date = new DateTime(Converter.dateFormat.parse(dateString));
				logger.info("Processing Date node: " + date.toString("dd-MM-yyyy") + " for object " + nodeTag);

				
				// Year
				int year = date.getYear();

				URI yearNode;
				try {
					GetNodesByLabel[] yearNodes = Neo4JClient.getNodesByLabelAndProperty("Year", "year",
							year);
					yearNode = new URI(yearNodes[0].getSelf());

					
				} catch (Neo4JRequestException | ArrayIndexOutOfBoundsException | NullPointerException e) {
					List<String> yearLabels = new ArrayList<String>();
					yearLabels.add("Date");
					yearLabels.add("Year");
					
					Map<String, Object> yearMap = new HashMap<String, Object>();
					yearMap.put("year", year);

					yearNode = Neo4JClient.createNodeWithProperties(yearLabels, yearMap);
					logger.info("Adding Date node - Year: " + year);
				}
				
				boolean existingRelationYear = checkRelationExists(nodeURI, yearNode, relationName);

				if (!existingRelationYear) {
					URI relationshipYear = Neo4JClient.createRelationship(nodeURI, yearNode, relationName);
					logger.info("Connecting Date node - Year: " + year + " to " + nodeTag);
				}
				
				
				// Month
				int month = date.getMonthOfYear();

				URI monthNode;
				try {
					GetNodesByLabel[] monthNodes = Neo4JClient.getNodesByLabelAndProperty("Month", "month",
							month);
					monthNode = new URI(monthNodes[0].getSelf());
					
				} catch (Neo4JRequestException | ArrayIndexOutOfBoundsException | NullPointerException e) {
					List<String> monthLabels = new ArrayList<String>();
					monthLabels.add("Date");
					monthLabels.add("Month");
					
					Map<String, Object> monthMap = new HashMap<String, Object>();
					monthMap.put("month", month);

					monthNode = Neo4JClient.createNodeWithProperties(monthLabels, monthMap);
					logger.info("Adding Date node - Month: " + month);
				}
				
				boolean existingRelationMonth = checkRelationExists(nodeURI, monthNode, relationName);

				if (!existingRelationMonth) {
					URI relationshipMonth = Neo4JClient.createRelationship(nodeURI, monthNode, relationName);
					logger.info("Connecting Date node - Month: " + month + " to " + nodeTag);

				}
				
				
				// Day
				int day = date.getDayOfMonth();

				URI dayNode;
				try {
					GetNodesByLabel[] dayNodes = Neo4JClient.getNodesByLabelAndProperty("Day", "day",
							day);
					dayNode = new URI(dayNodes[0].getSelf());
					
				} catch (Neo4JRequestException | ArrayIndexOutOfBoundsException | NullPointerException e) {
					List<String> dayLabels = new ArrayList<String>();
					dayLabels.add("Date");
					dayLabels.add("Day");
					
					Map<String, Object> dayMap = new HashMap<String, Object>();
					dayMap.put("day", day);

					dayNode = Neo4JClient.createNodeWithProperties(dayLabels, dayMap);
					logger.info("Adding Date node - Day: " + day);
				}
				
				boolean existingRelationDay = checkRelationExists(nodeURI, dayNode, relationName);

				if (!existingRelationDay) {
					URI relationshipDay = Neo4JClient.createRelationship(nodeURI, dayNode, relationName);
					logger.info("Connecting Date node - Day: " + day + " to " + nodeTag);

				}
				
				
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		}
	}
	
	private static void addLocationRelation(ProductionCountry prodCountry, URI nodeURI, String relationName, String nodeTag) throws UnsupportedEncodingException, URISyntaxException  {
		
		String countryISO = prodCountry.getIso31661();
		String countryName = prodCountry.getName();
		
		if (countryISO != null && !("".equals(countryISO))) {
			
				URI countryNode;
				try {
					GetNodesByLabel[] countryNodes = Neo4JClient.getNodesByLabelAndProperty("Country", "iso_3166_1",
							countryISO);
					countryNode = new URI(countryNodes[0].getSelf());

					
				} catch (Neo4JRequestException | ArrayIndexOutOfBoundsException | NullPointerException e) {
					List<String> countryLabels = new ArrayList<String>();
					countryLabels.add("Location");
					countryLabels.add("Country");
					
					Map<String, Object> countryMap = new HashMap<String, Object>();
					countryMap.put("iso_3166_1", countryISO);
					countryMap.put("name", countryName);

					countryNode = Neo4JClient.createNodeWithProperties(countryLabels, countryMap);
					logger.info("Adding Location node - Country: " + countryName);
				}
				
				boolean existingRelation = checkRelationExists(nodeURI, countryNode, relationName);

				if (!existingRelation) {
					URI relationship = Neo4JClient.createRelationship(nodeURI, countryNode, relationName);
					logger.info("Adding Location node - Country: " + countryName + " to " + nodeTag);
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
	
	
	

	public static List<Subtitle> saveAllMovieSubtitles() throws UnsupportedEncodingException {
		Neo4JConfig.setUniqueConstraints();

		OpenSubsClient openSubs = new OpenSubsClient();
		List<Subtitle> subtitles = new ArrayList<Subtitle>();

		GetNodesByLabel[] movies = Neo4JClient.getNodesByLabel("Movie");
		for (GetNodesByLabel getNodesByLabel : movies) {
			try {
				URI movieURI = new URI(getNodesByLabel.getSelf());
				String id_imdb = (String) getNodesByLabel.getData().get("id_imdb");
				String title = (String) getNodesByLabel.getData().get("title");
				
				Subtitle subtitle = openSubs.getSubtitle(id_imdb);
				subtitles.add(subtitle);
				// Falta adicionar à db
				
//				Map<String, Object> subtitlesMap = null;
//				
//				URI subtitleNode = null;
//				try {
//					subtitleNode = Neo4JClient.createNodeWithProperties("Subtitles", subtitlesMap);
//					logger.info("adding Subtitles from Movie: " + title);
//					
//				} catch (Neo4JRequestException e) {
////					GetNodesByLabel[] subtitleNodes = Neo4JClient.getNodesByLabelAndProperty("Subtitles", chave_subtitle,
////							valor_da_chave_subtitle);
////					subtitleNode = new URI(subtitleNodes[0].getSelf());
//				}
//				
//				
//				boolean existingRelation = checkRelationExists(movieURI, subtitleNode, "has");
//
//				if (!existingRelation) {
//					URI relationship = Neo4JClient.createRelationship(movieURI, subtitleNode, "has");
////					logger.info("Connecting subtitle with id: " + valor_da_chave_subtitle + " to Movie: " + title);
//				}

			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return subtitles;
	}

	public static Map<String, URI> addOMDbData() throws IOException, URISyntaxException {
		Neo4JConfig.setUniqueConstraints();
		Map<String, URI> addNodesResult = new HashMap<String, URI>();

		GetNodesByLabel[] movies = Neo4JClient.getNodesByLabel("Movie");
		for (GetNodesByLabel getNodesByLabel : movies) {
			Map<String, Object> movieProperties = getNodesByLabel.getData();
			String id_imdb = (String) movieProperties.get("id_imdb");
			String title = (String) movieProperties.get("title");


			if (id_imdb != null && !id_imdb.equals("")) {
				logger.info("Search OMDb for id: " + id_imdb);
				Map<String, Object> omdbProperties = OMDbClient.getOMDbMovie(id_imdb);
				if (omdbProperties != null) {
					Map<String, Object> resultMap = MapUtils.mergeMapCombine(movieProperties,
							Converter.omdbMap(omdbProperties));
					
					URI nodeURI = new URI(getNodesByLabel.getSelf());

					Neo4JClient.updateNodeProperties(nodeURI, resultMap);
					logger.info("Added OMDb info for: " + title);
					addNodesResult.put(id_imdb, nodeURI);

				}

			}
		}
		return addNodesResult;
	}

	public static Map<String, URI> addTMDbMovieData()
			throws URISyntaxException, NumberFormatException, UnsupportedEncodingException {
		Neo4JConfig.setUniqueConstraints();
		Map<String, URI> addNodesResult = new HashMap<String, URI>();

		GetNodesByLabel[] movies = Neo4JClient.getNodesByLabel("Movie");
		for (GetNodesByLabel getNodesByLabel : movies)
		{
			try
			{
				Map<String, Object> movieProperties = getNodesByLabel.getData();
				String id_tmdb = (String) movieProperties.get("id_tmdb");
				String title = (String) movieProperties.get("title");
	
				if (id_tmdb != null && !id_tmdb.equals("")) {
					logger.info("Search TMDb Movie for id: " + id_tmdb);
	
					TMDbClient tmdb = new TMDbClient();
					TMDbMovie movie = tmdb.getMovie(Integer.valueOf(id_tmdb));
	
					Map<String, Object> resultMap = MapUtils.mergeMapCombine(movieProperties,
							Converter.tmdbMovie2Map(movie, tmdb.getConfiguration()));
					
					URI movieURI = new URI(getNodesByLabel.getSelf());
	
					Neo4JClient.updateNodeProperties(movieURI, resultMap);
					logger.info("Added TMDb Movie info for: " + title);
					addNodesResult.put(id_tmdb, movieURI);
	
					List<ProductionCountry> countries = movie.getProductionCountries();
					for (ProductionCountry prodCountry : countries) {
						addLocationRelation(prodCountry,  movieURI, "created in",  (String) getNodesByLabel.getData().get("title"));
	
					}
					
					
				}
			}
			catch(Exception e){}
		}
		return addNodesResult;

	}

	public static Map<String, URI> addTMDbCastData()
			throws URISyntaxException, NumberFormatException, UnsupportedEncodingException {
		Neo4JConfig.setUniqueConstraints();
		Map<String, URI> addNodesResult = new HashMap<String, URI>();

		GetNodesByLabel[] cast = Neo4JClient.getNodesByLabel("Cast");
		for (GetNodesByLabel getNodesByLabel : cast)
		{
			
				Map<String, Object> castProperties = getNodesByLabel.getData();
				String id_tmdb = (String) castProperties.get("id_tmdb");
				String name = (String) castProperties.get("name");
	
				if (id_tmdb != null && !id_tmdb.equals("")) {
					logger.info("Search TMDb People for id: " + id_tmdb);
	
					TMDbClient tmdb = new TMDbClient();
					TMDbPerson person = tmdb.getPerson(Integer.valueOf(id_tmdb));
	
					Map<String, Object> resultMap = MapUtils.mergeMapCombine(castProperties,
							Converter.tmdbPerson2Map(person, tmdb.getConfiguration()));
					
					URI nodeURI = new URI(getNodesByLabel.getSelf());
					
					
					Integer gender = person.getGender();
					String label = gender == 2 ? "Actor" : (gender == 1 ? "Actress" : "");
					if (label != null && !("".equals(label))) {
						Neo4JClient.addNodeLabel(nodeURI, label);
					}							
	
					Neo4JClient.updateNodeProperties(nodeURI, resultMap);
					logger.info("Added TMDb People info for: " + name);
					addNodesResult.put(id_tmdb, nodeURI);
	
				}
			
		}
		return addNodesResult;

	}
	
	public static Map<String, URI> addDBpediaMovieData() throws URISyntaxException, IOException {
		Neo4JConfig.setUniqueConstraints();
		Map<String, URI> addNodesResult = new HashMap<String, URI>();
		
		GetNodesByLabel[] movies = Neo4JClient.getNodesByLabel("Movie");
		
		for (GetNodesByLabel getNodesByLabel : movies) {
			Map<String, Object> movieProperties = getNodesByLabel.getData();
			String title = (String) movieProperties.get("title");
			Double yearDouble = (Double) movieProperties.get("year");
			Integer year = yearDouble.intValue();


			if (title != null && !title.equals("")) {
				logger.info("Search DBpedia for Movie: " + title);

				DBpediaMovieResult dbpediaResult = SPARQLClient.getDBpediaMovie(title, year);

				Map<String, Object> resultMap = MapUtils.mergeMapCombine(movieProperties,
						Converter.dbpediaMovie2Map(dbpediaResult));
				
				if (!movieProperties.equals(resultMap)) {
					URI movieURI = new URI(getNodesByLabel.getSelf());

					Neo4JClient.updateNodeProperties(movieURI, resultMap);
					logger.info("Added DBpedia info for Movie: " + title);
					addNodesResult.put(title, movieURI);
				}
				
			}			
		}
		return addNodesResult;

	}
	
	public static Map<String, URI> addDBpediaCastData() throws IOException, URISyntaxException {
		Neo4JConfig.setUniqueConstraints();
		Map<String, URI> addNodesResult = new HashMap<String, URI>();

		GetNodesByLabel[] cast = Neo4JClient.getNodesByLabel("Cast");
		for (GetNodesByLabel getNodesByLabel : cast) {
			Map<String, Object> castProperties = getNodesByLabel.getData();
			String name = (String) castProperties.get("name");

			if (name != null && !name.equals("")) {
				logger.info("Search DBpedia for Movie: " + name);

				DBpediaPersonResult dbpediaResult = SPARQLClient.getDBpediaPerson(name);

				Map<String, Object> resultMap = MapUtils.mergeMapCombine(castProperties,
						Converter.dbpediaPerson2Map(dbpediaResult));
				if (!castProperties.equals(resultMap)) {
					URI castURI = new URI(getNodesByLabel.getSelf());

					Neo4JClient.updateNodeProperties(castURI, resultMap);
					logger.info("Added DBpedia info for Cast: " + name);
					addNodesResult.put(name, castURI);

				}
				
			}

		}
		return addNodesResult;

	}

}
