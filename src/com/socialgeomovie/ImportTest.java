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
import com.socialgeomovie.clients.SaveDataClient;
import com.socialgeomovie.clients.TraktClient;
import com.socialgeomovie.clients.TwitterClient;
import com.socialgeomovie.config.Neo4JConfig;
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;
import com.socialgeomovie.utils.Converter;
import com.socialgeomovie.utils.IDParser;
import com.socialgeomovie.utils.Merge;
import com.socialgeomovie.utils.Neo4JRequestException;
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
//			
//			h1.put("teste", s1);
//			h2.put("teste", "asda");
//			h2.put("teste", 122);
//
//			h2.put("123", "asd");
//
//			Map<String, Object> result = Merge.mergeMap(h1, h2);
//			Collection<Object> list =  result.values();
//			for (Object o : list) {
//				ArrayList<Object> l2 = (ArrayList<Object>) o;
//				for (Object object : l2) {
//					System.out.println(object);
//					System.out.println(object instanceof String);
//					System.out.println(object instanceof Number);
//				}
//				
//			}
//			System.out.println(list);
//			GetNodesByLabel[] result = Neo4JClient.getNodesByLabelAndProperty("Movie", "uri", "Deadpool");
//			Object a = result[0].getData().get("genres");
//			System.out.println(a instanceof List);
//			GetNodesByLabel[] result = Neo4JClient.getNodesByLabelAndProperty("Movie", "uri", "Deadpool");
//			Map<String, Object> data = result[0].getData();
//			
//			Map<String, Object> newInfo = new HashMap<String, Object>();
//			newInfo.put("uri", "abc");
//			List<Number> list = new ArrayList<Number>();
//			list.add(100);
//			list.add(200);
//			
//			newInfo.put("runtime", list);
//
//			
//			Map<String, Object> resultMap = Merge.mergeMap(data, newInfo);
//			for (String key : resultMap.keySet()) {
//				System.out.println(key + ": " + resultMap.get(key));
//			}
//			
			
			GetNodesByLabel[] movies = Neo4JClient.getNodesByLabel("Movie");
			for (GetNodesByLabel getNodesByLabel : movies) {
				Map<String, Object> movieProperties = getNodesByLabel.getData();
				String id_imdb = (String) movieProperties.get("id_imdb");
				Map<String, Object> omdbData = OMDbClient.getOMDbMovie(id_imdb);
				System.out.println("Search OMDb for id: " + id_imdb);

//				Map<String, Object> resultMap = Merge.mergeMap(movieProperties, omdbProperties);
//				
//				for (String key : resultMap.keySet()) {
//					System.out.println(key + ": " + resultMap.get(key));
//				}
//				
//				

				Map<String, Object> omdbProcessed = Converter.omdbMap(omdbData);
//				omdbProcessed.values().removeAll(Collections.singleton("N/A"));
				
//				List<Object> nullList = new ArrayList<Object>();
				
				List<Object> nullList = Arrays.asList("N/A", "NA", "null", "empty", null);
				omdbProcessed.values().removeIf(val -> nullList.contains(val));
				
				



				Map<String, Object> resultMap = Merge.mergeMap(movieProperties, omdbProcessed);

				for (String key : resultMap.keySet()) {
					System.out.println(key + ": " + resultMap.get(key));
				}
				
				
				System.out.println("Added OMDb info for: " + movieProperties.get("title"));
				System.out.println("\n\n");


				
			}
			
		}

	}
