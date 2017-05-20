package com.socialgeomovie.clients;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.socialgeomovie.pojos.neo4j.GetNodeByID;
import com.socialgeomovie.utils.Neo4JRequestException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class OMDbClient {

	public static Map<String, Object> getOMDbMovie(String id_imdb) throws IOException {
		String url = "http://www.omdbapi.com/?i=" + id_imdb;
		
		WebResource resource = Client.create().resource(url);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new RuntimeException("OMDb Request Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		Map json = new Gson().fromJson(output, new HashMap<String, Object>().getClass());
		response.close();

		if ("True".equals(json.get("Response"))) {
			return json;
		} else {
			return null;
		}


	}
}
