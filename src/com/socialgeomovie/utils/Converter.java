package com.socialgeomovie.utils;

import java.util.HashMap;
import java.util.Map;

import com.uwetrottmann.trakt5.entities.CastMember;
import com.uwetrottmann.trakt5.entities.Movie;

public class Converter 
{
	public static Map<String, Object> movie2Map(Movie movie)
	{
		Map<String, Object> movieData = new HashMap<String, Object>();
		movieData.put("uri", IDParser.createURI(movie.title));
		movieData.put("id_imdb", movie.ids.imdb);
		movieData.put("id_trakt", String.valueOf(movie.ids.trakt));
		movieData.put("id_tmdb", String.valueOf(movie.ids.tmdb));
		movieData.put("rating", movie.rating);
		movieData.put("title", movie.title);
		movieData.put("tagline", movie.tagline);
		movieData.put("certification", movie.certification);
		movieData.put("homepage", movie.homepage);
		movieData.put("trailer", movie.trailer);
		movieData.put("released", movie.released.toString());
		movieData.put("runtime", movie.runtime);
		movieData.put("overview", movie.overview);
		//movieData.put(MovieProperties.GENRES.toString(), movie.genres);
	
		return movieData;
	}
	
	public static Map<String, Object> cast2Map(CastMember cast)
	{
		Map<String, Object> castData = new HashMap<String, Object>();
		castData.put("uri", IDParser.createURI(cast.person.name));
		castData.put("character", cast.character);
		castData.put("name",cast.person.name);
		castData.put("id_imdb",cast.person.ids.imdb);
		castData.put("id_trakt",String.valueOf(cast.person.ids.trakt));
		castData.put("id_tmdb",String.valueOf(cast.person.ids.tmdb));
		
		return castData;
	}
}
