package com.socialgeomovie.clients;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;

import com.github.wtekiela.opensub4j.impl.OpenSubtitlesImpl;

public class OpenSubsClient {
	
	private static String URL = "http://api.opensubtitles.org:80/xml-rpc"; 
	private static String UA = "OSTestUserAgentTemp";
	private static String LANG = "en";
	
	public static void main(String [] args) throws MalformedURLException, XmlRpcException{
		
		OpenSubtitlesImpl os = new OpenSubtitlesImpl(new URL(URL));
		os.login(LANG, UA);
		System.out.println(os.searchSubtitles(LANG, "76759").get(0));
		System.out.println(os.downloadSubtitles(os.searchSubtitles("en", "76759").get(0).getId()).get(0).getContentAsString("UTF-8"));
		
	}
}
