package com.socialgeomovie.clients;

import java.io.IOException;
import java.util.List;

import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.CastMember;
import com.uwetrottmann.trakt5.entities.Credits;
import com.uwetrottmann.trakt5.entities.Movie;
import com.uwetrottmann.trakt5.enums.Extended;
import com.uwetrottmann.trakt5.services.Movies;

import retrofit2.Response;

public class TraktClient 
{
	private TraktV2 trakt;
	private Movies traktMovies;
	
	public TraktClient()
	{
		trakt = new TraktV2("f3e23412c0e637f237f1929f1d7c4424b2f3a091d726bb285a5b3ae067e6c934");
		traktMovies = trakt.movies();
	}
	
	/**
	 * Fetches the most popular movies
	 * @param page
	 * @param limit
	 * @return null if the request was not successful
	 * @throws IOException
	 */
	public List<Movie> getPopularMovies(int page, int limit) throws IOException
	{
		Response<List<Movie>> responseMovies = traktMovies.popular(page, limit, Extended.FULL).execute();
		List<Movie> movies = null;
	    if (responseMovies.isSuccessful())
	    {
	    	 movies = responseMovies.body();
	    }
	    
	    return movies;
	}
	
	/**
	 * Get specific movie information
	 * @param traktMovieId
	 * @return
	 * @throws IOException
	 */
	public Movie getMovie(String traktMovieId) throws IOException
	{
		Response<Movie> responseMovie = traktMovies.summary(traktMovieId, Extended.FULL).execute();
		Movie movie = null;
		if (responseMovie.isSuccessful())
	    {
			movie = responseMovie.body();
	    }
		
		return movie;
	}
	
	
	/**
	 * Fetches the cast involved on a movie
	 * @param traktMovieId
	 * @return null if the request was not successful
	 * @throws IOException
	 */
	public List<CastMember> getCast(String traktMovieId) throws IOException
	{
		Response<Credits> responsePeople = traktMovies.people(traktMovieId).execute();
		
		List<CastMember> cast = null;
		if (responsePeople.isSuccessful())
		{
			Credits credits = responsePeople.body();
			cast = credits.cast;
		}
		
		return cast;
	}
}
