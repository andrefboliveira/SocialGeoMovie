package com.socialgeomovie;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.socialgeomovie.clients.Neo4JClient;
import com.socialgeomovie.clients.OMDbClient;
import com.socialgeomovie.clients.OpenSubsClient;
import com.socialgeomovie.clients.SPARQLClient;
import com.socialgeomovie.clients.SaveDataClient;
import com.socialgeomovie.clients.TMDbClient;
import com.socialgeomovie.clients.TraktClient;

import com.socialgeomovie.config.Neo4JConfig;
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;
import com.socialgeomovie.pojos.sparql.BindingMovie;
import com.socialgeomovie.pojos.sparql.DBpediaMovieResult;
import com.socialgeomovie.pojos.tmdb.TMDbConfiguration;
import com.socialgeomovie.pojos.tmdb.Images_Config;
import com.socialgeomovie.pojos.tmdb.TMDbPerson;
import com.socialgeomovie.utils.Converter;
import com.socialgeomovie.utils.IDParser;
import com.socialgeomovie.utils.MapUtils;
import com.socialgeomovie.utils.exceptions.Neo4JRequestException;
import com.uwetrottmann.trakt5.entities.CastMember;
import com.uwetrottmann.trakt5.entities.Movie;



import java.net.URL;

public class ImportTest {

	public static void imp() throws URISyntaxException, IOException{
		TraktClient trakt = new TraktClient();
//		OpenSubsClient openSubs = new OpenSubsClient();
//		TwitterClient twitterClient = new TwitterClient();

		Map<String, Integer> report = new HashMap<String, Integer>();

//		Neo4JConfig.setUniqueConstraints();

		List<Movie> movies;

		movies = trakt.getPopularMovies(1, 10);

		int count = movies.size();
		report.put("movies", count);
		report.put("cast members", 0);
		report.put("subtitles", 0);
		report.put("tweets", 0);
		for(int i=0; i<count; i++)
		{
			Movie movie = movies.get(i);

			System.out.println("Processing movie: " + movie.title);
			// TODO store movie data
//			URI movieNode;			
//
//			try {
//				movieNode = Neo4JClient.createNodeWithProperties("Movie", Converter.traktMovie2Map(movie));
//			} catch (Neo4JRequestException e) {
//				GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "id_trakt", movie.ids.trakt);
//				movieNode = new URI(movieNodes[0].getSelf());
//			}


			//System.out.println(movie.ids.imdb);
			List<CastMember> cast = trakt.getCast(String.valueOf(movie.ids.trakt));
			int castCount = cast.size();
			report.put("cast members", castCount + report.get("cast members"));
			for(int j=0; j<castCount; j++)
			{
				CastMember castMember = cast.get(j);
				System.out.println("adding cast :" +castMember.person.name);
				// TODO store cast information
				
				System.out.println(castMember.person.birthplace != null);

//				Map<String, Object> castData = Converter.traktCast2Map(castMember);
//				String character = (String) castData.get("character");
//				castData.remove("character");
				List<String> castLabels = new ArrayList<String>();
				castLabels.add("Cast");
				castLabels.add("Person");



//				URI castNode;
//				try {
//					castNode = Neo4JClient.createNodeWithProperties(castLabels, castData);
//
//				} catch (Neo4JRequestException e) {
//					GetNodesByLabel[] castNodes = Neo4JClient.getNodesByLabelAndProperty("Cast", "id_trakt", castMember.person.ids.trakt);
//					castNode = new URI(castNodes[0].getSelf());
//					
//				}
//
//				Map<String, Object> characterMap = new HashMap<String, Object>();
//				characterMap.put("character", character);
//				URI relationship = Neo4JClient.createRelationshipWithProperties(castNode, movieNode, "acts in", characterMap);
			}
		
		}
		System.out.println("Import process done");
	}
	


		public static void main(String[] args) throws URISyntaxException, IOException {
//			imp();
//			Neo4JConfig.cleanDB();
//			Neo4JConfig.deleteAll();
//			Neo4JConfig.setUniqueConstraints();
//			System.out.println(IDParser.createURI(" ola tTESte asd153a 'olá' é um dia cão"));
			//int id = 293660;
//			
//			Map<String, Object> h1 = new HashMap<String, Object>();
//			Map<String, Object> h2 = new HashMap<String, Object>();
//			
//			ArrayList<Object> s1 = new ArrayList<Object>();
//			s1.add("teste");
//			s1.add("ola");
//			s1.add(123);
//
//
			
//			Map<String,Object> result = gson.fromJson(output, Map.class);
//			System.out.println(result.keySet());
//			
//			TMDbClient tmdb = new TMDbClient();
//			
//			Images_Config i = tmdb.getConfiguration().getImages();
//			
//
//			
//			 TMDbPerson m = tmdb.getPerson(10859);
//			 System.out.println(m.getName());
		
//			SaveLocationData.saveData();
			
//			SaveDataClient.addDBpediaCastData();
			SaveDataClient.addDBpediaMovieData();
			
		}

	}
