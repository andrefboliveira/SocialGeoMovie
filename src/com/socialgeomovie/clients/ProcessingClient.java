package com.socialgeomovie.clients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.socialgeomovie.clients.*;
import com.uwetrottmann.trakt5.entities.CastMember;
import com.uwetrottmann.trakt5.entities.Movie;

public class ProcessingClient {
	
	public static void main(String[] args) throws IOException {
		// Indica que não pode haver mais do que um filme com o mesmo titulo ou o mesmo id do IMDb
//		Neo4JClient.setUniquenessConstraint("Movie", "title");
//		Neo4JClient.setUniquenessConstraint("Movie", "imdb");
//		Neo4JClient.setUniquenessConstraint("Movie", "trakt");
//		Neo4JClient.setUniquenessConstraint("Cast", "name");

		
		TraktClient trakt_movie = new TraktClient();
		List<Movie> popular = trakt_movie.getPopularMovies(1, 10);
		
		for (Movie movie : popular) {
			HashMap<String, Object> movieProperties = new HashMap<String, Object>();
			Integer id = movie.ids.trakt;
			
			
			movieProperties.put("title", movie.title);
			movieProperties.put("trakt", id);
			movieProperties.put("imdb", movie.ids.imdb);
			movieProperties.put("rating", movie.rating);
			movieProperties.put("trailer", movie.trailer);
			movieProperties.put("year", movie.year);

			
			URI movieNode = Neo4JClient.createNodeWithProperties("Movie", movieProperties);
			System.out.println(movie.title);
			
			
			TraktClient trakt_cast = new TraktClient();
			List<CastMember> cast = trakt_cast.getCast(String.valueOf(id));
			
			for (CastMember castMember : cast) {
				System.out.println(castMember.person.name);
				HashMap<String, Object> castProperties = new HashMap<String, Object>();
				List<String> castLabels = new ArrayList<String>();
				castProperties.put("name", castMember.person.name);
				castProperties.put("character", castMember.character);
				
				castLabels.add("Cast");
				URI castNode = Neo4JClient.createNodeWithProperties(castLabels, castProperties);
				
				try {
					Neo4JClient.createRelationship(castNode, movieNode, "acts in");
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
		}
			
		
	
		
		
		
	}
}
