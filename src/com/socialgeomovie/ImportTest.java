package com.socialgeomovie;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.socialgeomovie.clients.Neo4JClient;
import com.socialgeomovie.clients.OpenSubsClient;
import com.socialgeomovie.clients.TraktClient;
import com.socialgeomovie.clients.TwitterClient;
import com.socialgeomovie.config.Neo4JConfig;
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;
import com.socialgeomovie.utils.Converter;
import com.socialgeomovie.utils.Neo4JRequestException;
import com.uwetrottmann.trakt5.entities.CastMember;
import com.uwetrottmann.trakt5.entities.Movie;

public class ImportTest {

	public static void imp() throws URISyntaxException, IOException{
		TraktClient trakt = new TraktClient();
		OpenSubsClient openSubs = new OpenSubsClient();
		TwitterClient twitterClient = new TwitterClient();

		Map<String, Integer> report = new HashMap<String, Integer>();

		Neo4JConfig.setUniqueConstraints();

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
			URI movieNode;			

			try {
				movieNode = Neo4JClient.createNodeWithProperties("Movie", Converter.movie2Map(movie));
			} catch (Neo4JRequestException e) {
				GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "id_trakt", movie.ids.trakt);
				movieNode = new URI(movieNodes[0].getSelf());
			}


			//System.out.println(movie.ids.imdb);
			List<CastMember> cast = trakt.getCast(String.valueOf(movie.ids.trakt));
			int castCount = cast.size();
			report.put("cast members", castCount + report.get("cast members"));
			for(int j=0; j<castCount; j++)
			{
				CastMember castMember = cast.get(j);
				System.out.println("adding cast :" +castMember.person.name);
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
		
		}
		System.out.println("Import process done");
	}

		public static void main(String[] args) throws URISyntaxException, IOException {
			imp();
			Neo4JConfig.cleanDB();
		}

	}
