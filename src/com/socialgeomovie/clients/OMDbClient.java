package com.socialgeomovie.clients;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class OMDbClient {

	public static Map<String, Object> getOMDbMovie(String id_imdb) throws IOException {
		URL url = new URL("http://www.omdbapi.com/?i=" + id_imdb);
		InputStreamReader reader = new InputStreamReader(url.openStream());
		return new Gson().fromJson(reader, new HashMap<String, Object>().getClass());

	}
}
