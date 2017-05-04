package com.socialgeomovie.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.uwetrottmann.trakt5.entities.CastMember;
import com.uwetrottmann.trakt5.entities.Movie;

public class Converter {
	public static Map<String, Object> traktMovie2Map(Movie movie) {
		Map<String, Object> movieData = new HashMap<String, Object>();
		movieData.put("uri", IDParser.createURI(movie.title));
		movieData.put("id_imdb", movie.ids.imdb);
		movieData.put("id_trakt", String.valueOf(movie.ids.trakt));
		movieData.put("id_tmdb", String.valueOf(movie.ids.tmdb));
		movieData.put("imdb_rating", movie.rating);
		movieData.put("title", movie.title);
		movieData.put("tagline", movie.tagline);
		movieData.put("certification", movie.certification);
		movieData.put("homepage", movie.homepage);
		movieData.put("trailer", movie.trailer);
		movieData.put("released", movie.released.toString());
		movieData.put("runtime", movie.runtime);
		movieData.put("overview", movie.overview);
		movieData.put("genres", movie.genres);
		movieData.put("url_trakt", "https://trakt.tv/movies/" + movie.ids.slug);

		return movieData;
	}

	public static Map<String, Object> traktCast2Map(CastMember cast) {
		Map<String, Object> castData = new HashMap<String, Object>();
		castData.put("uri", IDParser.createURI(cast.person.name));
		castData.put("name", cast.person.name);
		castData.put("id_imdb", cast.person.ids.imdb);
		castData.put("id_trakt", String.valueOf(cast.person.ids.trakt));
		castData.put("id_tmdb", String.valueOf(cast.person.ids.tmdb));
		castData.put("biography", cast.person.biography);
		castData.put("homepage", cast.person.homepage);

		castData.put("url_trakt", "https://trakt.tv/people/" + cast.person.ids.slug);
		

		return castData;
	}

	public static Map<String, Object> traktMovieLinks(Map<String, Object> movieData) {

		movieData.put("url_imdb", "http://www.imdb.com/title/" + movieData.get("id_imdb"));
		try {
			movieData.put("url_tmdb",
					IDParser.getRedirectURL("https://www.themoviedb.org/movie/" + movieData.get("id_tmdb")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return movieData;
	}

	public static Map<String, Object> traktCastLinks(Map<String, Object> castData) {
		castData.put("url_imdb", "http://www.imdb.com/name/" + castData.get("id_imdb"));
		try {
			castData.put("url_tmdb", IDParser
					.getRedirectURL("https://www.themoviedb.org/person/" + castData.get("id_tmdb")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return castData;
	}

	public static Map<String, Object> omdbMap(Map<String, Object> omdbResponse) {
		return omdbResponse;

	}
}
