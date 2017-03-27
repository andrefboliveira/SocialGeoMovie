package com.socialgeomovie.clients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.socialgeomovie.clients.*;
import com.socialgeomovie.config.Neo4JConfig;
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;
import com.socialgeomovie.utils.Neo4JRequestException;
import com.uwetrottmann.trakt5.entities.CastMember;
import com.uwetrottmann.trakt5.entities.Movie;

public class ProcessingClient {
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		// Indica que não pode haver mais do que um filme com o mesmo titulo ou o mesmo id do IMDb
//		Neo4JClient.setUniquenessConstraint("Movie", "title");
//		Neo4JClient.setUniquenessConstraint("Movie", "id_imdb");
//		Neo4JClient.setUniquenessConstraint("Movie", "id_trakt");
//		Neo4JClient.setUniquenessConstraint("Cast", "name");
		
		Neo4JConfig.setUniqueConstraints();

		
		TraktClient trakt_movie = new TraktClient();
		List<Movie> popular = trakt_movie.getPopularMovies(1, 10);
		URI movieNode;

		for (Movie movie : popular) {
			movieNode = null;
			HashMap<String, Object> movieProperties = new HashMap<String, Object>();
			Integer id = movie.ids.trakt;
			
			
			movieProperties.put("title", movie.title);
			movieProperties.put("trakt", id);
			movieProperties.put("imdb", movie.ids.imdb);
			movieProperties.put("rating", movie.rating);
			movieProperties.put("trailer", movie.trailer);
			movieProperties.put("year", movie.year);
			
			try {
				movieNode = Neo4JClient.createNodeWithProperties("Movie", movieProperties);
				System.out.println("Movie" + movie.title);
			} catch (Neo4JRequestException e) {
				GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "trakt", id);
				for (GetNodesByLabel getNodesByLabel : movieNodes) {
					System.out.println(getNodesByLabel.getLabels());
				}
				movieNode = new URI(movieNodes[0].getSelf());
			}
			
			
			
			TraktClient trakt_cast = new TraktClient();
			List<CastMember> cast = trakt_cast.getCast(String.valueOf(id));
			System.out.println(cast.size());
			
			for (CastMember castMember : cast) {
				HashMap<String, Object> castProperties = new HashMap<String, Object>();
				List<String> castLabels = new ArrayList<String>();
				castProperties.put("name", castMember.person.name);
				
				Map<String, Object> relationProp = new HashMap<String, Object>();				
				relationProp.put("character", castMember.character);
				
				castLabels.add("Cast");
				URI castNode;
				try {
		

					castNode = Neo4JClient.createNodeWithProperties(castLabels, castProperties);
					System.out.println(castMember.person.name);
					
				} catch (Neo4JRequestException e) {
					GetNodesByLabel[] castNodes = Neo4JClient.getNodesByLabelAndProperty("Cast", "name", castMember.person.name);
					castNode = new URI(castNodes[0].getSelf());
				}
				
				try {
					Neo4JClient.createRelationshipWithProperties(castNode, movieNode, "acts in", relationProp);
				} catch (Neo4JRequestException e) {
					// TODO: handle exception
				}

			}
			
		}
			
		
	
		
		
		
	}
}
