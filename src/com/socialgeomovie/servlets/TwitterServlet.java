package com.socialgeomovie.servlets;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.socialgeomovie.clients.TwitterClient;

@Path("/twitter")
public class TwitterServlet {
	// http://localhost:8080/aw2017/rest/twitter
	
	/**
	 * Get tweets. 
	 */
	@GET
	public Response getTweets() {
		TwitterClient twitterClient = new TwitterClient();
		
		
		
		return null;
		
	}
	
}
