package com.socialgeomovie.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
		movieData.put("released", new SimpleDateFormat("yyyy-MM-dd").format(movie.released.toDate()));
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
		Map<String, Object> omdbData = new HashMap<String, Object>();
		omdbData.put("title", omdbResponse.get("Title"));
		omdbData.put("poster", omdbResponse.get("Poster"));
		omdbData.put("certification", omdbResponse.get("Rated"));
		
		DateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
		try {
			String releasedString = (String) omdbResponse.get("Released");
			Date date = format.parse(releasedString);
			omdbData.put("released", new SimpleDateFormat("yyyy-MM-dd").format(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String runtimeString = (String) omdbResponse.get("Runtime");
		omdbData.put("runtime", runtimeString.split(" ")[0]);
		
		String genresString = (String) omdbResponse.get("Genre");
		omdbData.put("genres", genresString.split(", "));
		
//		omdbData.put("overview", omdbResponse.get("Plot"));
		omdbData.put("homepage", omdbResponse.get("Website"));
		omdbData.put("imdb_rating", omdbResponse.get("imdbRating"));
		omdbData.put("imdb_votes", omdbResponse.get("imdbVotes"));
		omdbData.put("metacritic_rating", omdbResponse.get("Metascore"));
		
		List<Map<String, String>> ratingsList = (List<Map<String, String>>) omdbResponse.get("Ratings");
		Map<String, String> RottenTomatoesMap = ratingsList.get(1);
		if (RottenTomatoesMap.get("Source").equals("Rotten Tomatoes")) {
			String RTRatingString = (String) RottenTomatoesMap.get("Value");
			omdbData.put("rotten_tomatoes_rating", RTRatingString.split("%")[0]);
		}
		
		omdbData.put("production", omdbResponse.get("Production"));
		omdbData.put("box_office", omdbResponse.get("BoxOffice"));
		omdbData.put("awards", omdbResponse.get("Awards"));
		omdbData.put("language", omdbResponse.get("Language"));
		
//		String countryString = (String) omdbResponse.get("Country");
//		omdbData.put("country", countryString.split(", "));
//		
//		String directorString = (String) omdbResponse.get("Director");
//		omdbData.put("director", directorString.split(", "));
//		
//		String writerString = (String) omdbResponse.get("Writer");
//		omdbData.put("writer", writerString.split(", "));
//		
//		String actorsString = (String) omdbResponse.get("Actors");
//		omdbData.put("actors", actorsString.split(", "));
//		
//		omdbData.put("year", omdbResponse.get("Year"));

		
		return omdbData;
	}
	
//	public static Map<String, Object> tmdbMovie2Map(MovieDb movie, TmdbConfiguration config) {
//		return null;
//		
//	}
//	
//	public static Map<String, Object> tmdbPerson2Map(PersonPeople person, TmdbConfiguration config) {
//		person.
//		return null;
//		
//	}
}
