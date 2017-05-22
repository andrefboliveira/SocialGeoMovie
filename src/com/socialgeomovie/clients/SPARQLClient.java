package com.socialgeomovie.clients;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.helpers.IOUtils;

import com.google.gson.Gson;
import com.socialgeomovie.pojos.sparql.DBpediaMovieResult;
import com.socialgeomovie.pojos.sparql.DBpediaPersonResult;
import com.socialgeomovie.utils.exceptions.OMDbRequestException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class SPARQLClient {
	
public static String queryDBPediaSPARQL(String query) throws IOException  {
		
		String graphURI =  URLEncoder.encode("http://dbpedia.org", "UTF-8");
		String format = URLEncoder.encode("application/sparql-results+json", "UTF-8");
		String timeout = URLEncoder.encode("100000", "UTF-8");

		
		query = URLEncoder.encode(query, "UTF-8");

		String resultURL =  "https://dbpedia.org/sparql?default-graph-uri=" + graphURI + "&query=" + query + "&format=" + format + "&timeout=" + timeout;
		System.out.println(resultURL);
		
		URL url = new URL(resultURL);
		InputStreamReader reader = new InputStreamReader(url.openStream());
		return IOUtils.toString(reader);
	}
	
	public static DBpediaMovieResult getDBpediaMovie(String movieName) throws IOException, URISyntaxException {
		
		String uriName = movieName.replaceAll(" ", "_");
	
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>                                 " +
				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>                                                        " +
				"PREFIX dbo: <http://dbpedia.org/ontology/>                                                       " +
				"                                                                                                 " +
				"SELECT DISTINCT ?movieResource ?wikipediaPage ?directorName WHERE {                              " +
				"  {                                                                                              " +
				"    ?movieResource rdfs:label \"" + movieName + "\"@en.                                              " +
				"                                                                                                 " +
				"  }                                                                                              " +
				"  UNION                                                                                          " +
				"  {                                                                                              " +
				"    ?altName rdfs:label \"" + movieName + "\"@en ;                                                   " +
				"             dbo:wikiPageRedirects ?movieResource .                                              " +
				"  }                                                                                              " +
				"  UNION                                                                                          " +
				"  {                                                                                              " +
				" <http://dbpedia.org/resource/" + uriName + ">	dbo:wikiPageRedirects	?movieResource .  " +
				"  }                                                                                              " +
				"	?movieResource a dbo:Film;                                                                    " +
				"		foaf:isPrimaryTopicOf ?wikipediaPage.                                                     " +
				"	OPTIONAL {                                                                                    " +
				"		?movieResource dbo:director	?directorResource.                                            " +
				"		?directorResource foaf:name ?directorName.                                                " +
				"	}                                                                                             " +
				"		                                                                                          " +
				"}                                                                                                " +
				"LIMIT 1";

		String resultString = queryDBPediaSPARQL(query);
		return new Gson().fromJson(resultString, DBpediaMovieResult.class);
	}
	
public static DBpediaPersonResult getDBpediaPerson(String personName) throws IOException, URISyntaxException {
		
		String uriName = personName.replaceAll(" ", "_");
	
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>                                   " +
		"PREFIX foaf: <http://xmlns.com/foaf/0.1/>                                                   " +
		"PREFIX dbo: <http://dbpedia.org/ontology/>                                                  " +
		"PREFIX umbel-rc: <http://umbel.org/umbel/rc/>                                               " +
        "                                                                                            " +
		"SELECT DISTINCT ?personResource ?wikipediaPage ?summary WHERE {                             " +
		"  {                                                                                         " +
		"    ?personResource rdfs:label \"" + personName + "\"@en.                                          " +
		"                                                                                            " +
		"  }                                                                                         " +
        "                                                                                            " +
		"  UNION                                                                                     " +
		"  {                                                                                         " +
		"    ?personResource foaf:name \"" + personName + "\"@en.                                           " +
		"  }                                                                                         " +
		"  UNION                                                                                     " +
		"  {                                                                                         " +
		" <http://dbpedia.org/resource/" + uriName + ">	dbo:wikiPageRedirects	?personResource .    " +
		"  }                                                                                         " +
		"                                                                                            " +
		"  ?personResource a umbel-rc:Actor;                                                         " +
		"	foaf:isPrimaryTopicOf ?wikipediaPage.                                                    " +
		"  OPTIONAL {                                                                                " +
		"	?personResource dbo:abstract ?summary.                                                   " +
		"	FILTER (lang(?summary) = 'en')                                                           " +
		"  }                                                                                         " +
		" }                                                                                          " +
		"LIMIT 1";

		String resultString = queryDBPediaSPARQL(query);
		return new Gson().fromJson(resultString, DBpediaPersonResult.class);
	}

}
