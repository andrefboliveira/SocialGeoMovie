package com.socialgeomovie.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.socialgeomovie.clients.SPARQLClient;
import com.socialgeomovie.pojos.sparql.BindingMovie;
import com.socialgeomovie.pojos.sparql.BindingPerson;
import com.socialgeomovie.pojos.sparql.DBpediaMovieResult;
import com.socialgeomovie.pojos.sparql.DBpediaPersonResult;
import com.socialgeomovie.pojos.sparql.DirectorName;
import com.socialgeomovie.pojos.sparql.Summary;
import com.socialgeomovie.pojos.sparql.WikipediaPage;
import com.socialgeomovie.pojos.tmdb.Genre;
import com.socialgeomovie.pojos.tmdb.ProductionCompany;
import com.socialgeomovie.pojos.tmdb.ProductionCountry;
import com.socialgeomovie.pojos.tmdb.SpokenLanguage;
import com.socialgeomovie.pojos.tmdb.TMDbConfiguration;
import com.socialgeomovie.pojos.tmdb.TMDbMovie;
import com.socialgeomovie.pojos.tmdb.TMDbPerson;
import com.uwetrottmann.trakt5.entities.CastMember;
import com.uwetrottmann.trakt5.entities.Movie;

/**
 * @author André Oliveira
 *
 */
public class Converter {
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

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
		movieData.put("released", dateFormat.format(movie.released.toDate()));
		movieData.put("runtime", movie.runtime);
		movieData.put("overview", movie.overview);
		movieData.put("genres", movie.genres);
		movieData.put("year", movie.year);
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
//		castData.put("biography", cast.person.biography);
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
			castData.put("url_tmdb",
					IDParser.getRedirectURL("https://www.themoviedb.org/person/" + castData.get("id_tmdb")));
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
			omdbData.put("released", dateFormat.format(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String runtimeString = (String) omdbResponse.get("Runtime");
		omdbData.put("runtime", runtimeString.split(" ")[0]);

		String genresString = (String) omdbResponse.get("Genre");
		omdbData.put("genres", genresString.split(", "));

		// omdbData.put("overview", omdbResponse.get("Plot"));
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

		// String countryString = (String) omdbResponse.get("Country");
		// omdbData.put("country", countryString.split(", "));
		//
		// String directorString = (String) omdbResponse.get("Director");
		// omdbData.put("director", directorString.split(", "));
		//
		// String writerString = (String) omdbResponse.get("Writer");
		// omdbData.put("writer", writerString.split(", "));
		//
		// String actorsString = (String) omdbResponse.get("Actors");
		// omdbData.put("actors", actorsString.split(", "));
		//
		// omdbData.put("year", omdbResponse.get("Year"));

		return omdbData;
	}

	public static Map<String, Object> tmdbMovie2Map(TMDbMovie movie, TMDbConfiguration config) {
		Map<String, Object> tmdbMovieData = new HashMap<String, Object>();

		String baseURL = config.getImages().getSecureBaseUrl();
		String posterPath = movie.getPosterPath();
		List<String> posterSize = config.getImages().getPosterSizes();
		List<String> poster = new ArrayList<String>();
		for (String size : posterSize) {
			if (posterPath != null && !"".equals(posterPath)) {
				poster.add(baseURL + size + posterPath);
			}
		}
		tmdbMovieData.put("poster", baseURL + posterSize.get(3) + posterPath);
		tmdbMovieData.put("poster_tmdb", poster);

		String backdropPath = movie.getBackdropPath();
		List<String> backdropSize = config.getImages().getBackdropSizes();
		List<String> backdrop = new ArrayList<String>();
		for (String size : backdropSize) {
			if (backdropPath != null && !"".equals(backdropPath)) {
				backdrop.add(baseURL + size + backdropPath);
			}
		}

		tmdbMovieData.put("backdrop_tmdb", backdrop);

		tmdbMovieData.put("homepage", movie.getHomepage());

		List<Genre> genresTMDb = movie.getGenres();
		List<String> genreList = new ArrayList<String>();
		for (Genre genre : genresTMDb) {
			genreList.add(genre.getName());
		}

		tmdbMovieData.put("genres", genreList);

		tmdbMovieData.put("title", movie.getTitle());
		tmdbMovieData.put("popularity", movie.getPopularity());

		List<ProductionCompany> prodCompanyTMDb = movie.getProductionCompanies();
		List<String> prodCompanyList = new ArrayList<String>();
		for (ProductionCompany prodCompany : prodCompanyTMDb) {
			prodCompanyList.add(prodCompany.getName());
		}
		tmdbMovieData.put("production_company", prodCompanyList);

		tmdbMovieData.put("tmdb_rating", movie.getVoteAverage());
		tmdbMovieData.put("tmdb_votes", movie.getVoteCount());

		List<SpokenLanguage> languagesTMDb = movie.getSpokenLanguages();
		List<String> languagesList = new ArrayList<String>();
		for (SpokenLanguage language : languagesTMDb) {
			languagesList.add(language.getName());
		}
		tmdbMovieData.put("language", languagesList);

		return tmdbMovieData;

	}

	public static Map<String, Object> tmdbPerson2Map(TMDbPerson person, TMDbConfiguration config) {
		Map<String, Object> tmdbPersonData = new HashMap<String, Object>();
//		tmdbPersonData.put("biography", person.getBiography());

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			String birthdayString = person.getBirthday();
			if (birthdayString != null && !("".equals(birthdayString))) {
				Date birthdayDate = format.parse(birthdayString);
				tmdbPersonData.put("birthday", dateFormat.format(birthdayDate));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			String deathdayString = person.getDeathday();
			if (deathdayString != null && !("".equals(deathdayString))) {
				Date deathdayDate = format.parse(deathdayString);
				tmdbPersonData.put("deathday", dateFormat.format(deathdayDate));
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Integer intGender = person.getGender();
		tmdbPersonData.put("gender", intGender == 2 ? "Male" : (intGender == 1 ? "Female" : ""));
		tmdbPersonData.put("homepage", person.getHomepage());
		tmdbPersonData.put("popularity", person.getPopularity());

		String profilePath = person.getProfilePath();
		String baseURL = config.getImages().getSecureBaseUrl();
		List<String> imageSize = config.getImages().getProfileSizes();
		List<String> profileImage = new ArrayList<String>();

		for (String size : imageSize) {
			if (profilePath != null && !"".equals(profilePath)) {
				profileImage.add(baseURL + size + profilePath);
			}
		}

		tmdbPersonData.put("profile_image", baseURL + imageSize.get(1) + profilePath);
		tmdbPersonData.put("profile_image_tmdb", profileImage);

		return tmdbPersonData;

	}

	public static Map<String, Object> dbpediaMovie2Map(DBpediaMovieResult movieResults) {
		Map<String, Object> dbpediaMovieData = new HashMap<String, Object>();

		List<BindingMovie> bindingMovies = movieResults.getResults().getBindings();
		
		if (bindingMovies.size() > 0) {
			BindingMovie bindingMovie = bindingMovies.get(0);
			
			WikipediaPage wikipediaList = bindingMovie.getWikipediaPage();
			if (wikipediaList != null ) {
				dbpediaMovieData.put("url_wikipedia", wikipediaList.getValue());
			}
			DirectorName directorList = bindingMovie.getDirectorName();
			if (directorList != null) {
				dbpediaMovieData.put("director", directorList.getValue());
			}
			
		}
		
		
		return dbpediaMovieData;
		
	}
	
	public static Map<String, Object> dbpediaPerson2Map(DBpediaPersonResult personResults) {
		Map<String, Object> dbpediaPersonData = new HashMap<String, Object>();

		List<BindingPerson> bindingPeople = personResults.getResults().getBindings();
		
		if (bindingPeople.size() > 0) {
			BindingPerson bindingPerson = bindingPeople.get(0);
			
			WikipediaPage wikipediaList = bindingPerson.getWikipediaPage();
			if (wikipediaList != null ) {
				dbpediaPersonData.put("url_wikipedia", wikipediaList.getValue());
			}
			Summary summaryList = bindingPerson.getSummary();
			if (summaryList != null) {
				dbpediaPersonData.put("biography", summaryList.getValue());
			}
			
		}
		return dbpediaPersonData;
		
	}
}