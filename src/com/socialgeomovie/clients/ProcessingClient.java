package com.socialgeomovie.clients;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import com.socialgeomovie.clients.*;
import com.uwetrottmann.trakt5.entities.Movie;

public class ProcessingClient {
	
	public static void main(String[] args) throws IOException {
		// Indica que não pode haver mais do que um filme com o mesmo titulo ou o mesmo id do IMDb
		Neo4JClient.setUniquenessConstraint("Movie", "title");
		Neo4JClient.setUniquenessConstraint("Movie", "imdb");
		
		TraktClient trakt = new TraktClient();
		List<Movie> popular = trakt.getPopularMovies(1, 10);
		
		for (Movie movie : popular) {
			HashMap<String, Object> propriedades = new HashMap<String, Object>();
			
			propriedades.put("title", movie.title);
			propriedades.put("imdb", movie.ids.imdb);
			propriedades.put("rating", movie.rating);
			propriedades.put("trailer", movie.trailer);
			propriedades.put("year", movie.year);

			
			Neo4JClient.createNodeWithProperties("Movie", propriedades);
			System.out.println(movie.title);
		}
	}
}
