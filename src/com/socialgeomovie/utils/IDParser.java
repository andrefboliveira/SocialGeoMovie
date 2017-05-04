package com.socialgeomovie.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.Normalizer;

import org.apache.commons.lang3.text.WordUtils;

public class IDParser {

	public static String createURI(String unformatted) {
		return Normalizer
				.normalize(WordUtils.capitalizeFully(unformatted.replaceAll("[.,\"']", " ")), Normalizer.Form.NFD)
				.replaceAll("\\W", "");

	}

	public static String getRedirectURL(String url) throws IOException {
		URLConnection con = new URL(url).openConnection();
		con.connect();
		InputStream is = con.getInputStream();
		String new_url = con.getURL().toString();
		is.close();
		return new_url;
	}
}
