package com.socialgeomovie.clients;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;

import com.github.wtekiela.opensub4j.impl.OpenSubtitlesImpl;

public class OpenSubsClient {
	
	private String URL = "http://api.opensubtitles.org:80/xml-rpc"; 
	private String UA = "OSTestUserAgentTemp";
	private String LANG = "en";
	private String FORMAT = "UTF-8";
	private OpenSubtitlesImpl os;
	
	public OpenSubsClient(){
		try {
			os = new OpenSubtitlesImpl(new URL(URL));
			os.login(LANG, UA);
		} catch (XmlRpcException | MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public String getSubtitle(String imdb){
		
		try {
			return os.downloadSubtitles(os.searchSubtitles(LANG, imdb).get(0).getId()).get(0).getContentAsString(FORMAT);
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
		return "";
	}
	
}
