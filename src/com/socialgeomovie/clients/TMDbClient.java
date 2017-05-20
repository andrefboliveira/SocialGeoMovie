package com.socialgeomovie.clients;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.socialgeomovie.pojos.neo4j.GetNodeByID;
import com.socialgeomovie.pojos.tmdb.Configuration;
import com.socialgeomovie.pojos.tmdb.Movie;
import com.socialgeomovie.pojos.tmdb.Person;
import com.socialgeomovie.utils.Neo4JRequestException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;



public class TMDbClient {
	
	String ApiKey;
	private Gson gson;
	
	public TMDbClient() {
		ApiKey = "e07fa17dfe2c84d2f68be7023d9206ab";
		gson = new Gson();
		
	}
	

	public Movie getMovie(int movieTMDbId) throws UnsupportedEncodingException {
		String language = "en";
		String append_to_response = "credits, alternative_titles, images, videos";
		String queryURL = "https://api.themoviedb.org/3/movie/" + movieTMDbId + "?api_key=" + ApiKey + "&language=" + language + "&append_to_response=" +  URLEncoder.encode(append_to_response, "UTF-8");
		
		WebResource resource = Client.create().resource(queryURL);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new RuntimeException("TMDb Request Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		
		Movie movie = gson.fromJson(output, Movie.class);

		response.close();

		return movie;
	}
	
	
	public Person getPerson(int personTMDbId) throws UnsupportedEncodingException {
		String language = "en";
		String append_to_response = "images, tagged_images, external_ids";
				
		String queryURL = "https://api.themoviedb.org/3/person/" + personTMDbId + "?api_key=" + ApiKey + "&language=" + language + "&append_to_response=" +  URLEncoder.encode(append_to_response, "UTF-8");
		
		WebResource resource = Client.create().resource(queryURL);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new RuntimeException("TMDb Request Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);		
		Person person = gson.fromJson(output, Person.class);

		response.close();

		return person;
	}
	
	
	public Configuration getConfiguration() {
				
		String queryURL = "https://api.themoviedb.org/3/configuration?api_key=" + ApiKey;
		
		WebResource resource = Client.create().resource(queryURL);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new RuntimeException("TMDb Request Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);		
		Configuration config = gson.fromJson(output, Configuration.class);

		response.close();

		return config;
	}
	


//	public TmdbConfiguration getTmdbConfiguration() {
//		return tmdbConfiguration;
//	}
		

}
