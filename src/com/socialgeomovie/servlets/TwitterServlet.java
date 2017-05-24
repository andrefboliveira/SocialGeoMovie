package com.socialgeomovie.servlets;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.socialgeomovie.clients.Neo4JClient;
import com.socialgeomovie.pojos.neo4j.GetNodeRelationship;
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;

@Path("/twitter")
public class TwitterServlet 
{
	// http://localhost:8080/aw2017/rest/twitter
	
	/**
	 * Get tweets. 
	 */
	@GET
	public Response getTweets() 
	{
		
		
		
		
		
		return null;
		
	}
	
}
