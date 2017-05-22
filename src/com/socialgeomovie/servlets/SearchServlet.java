package com.socialgeomovie.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.text.WordUtils;

import com.google.gson.Gson;
import com.socialgeomovie.clients.Neo4JClient;
import com.socialgeomovie.pojos.neo4j.cypher.Datum;

@Path("/search")
public class SearchServlet {
	// http://localhost:8080/aw2017/rest/search
	
	/**
	 * Autocomplete
	 * @param propertyName 
	 * @param propertyValue 
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getResourceByProperty(
			@DefaultValue("") @QueryParam("type") final String nodeLabel, 
			@QueryParam("property") final String propertyName, 
			@QueryParam("value") final String propertyValue,
			@DefaultValue("-1") @QueryParam("limit") final int limit,
			@DefaultValue("true") @QueryParam("compact") final boolean compact_mode,
			@DefaultValue("false") @QueryParam("include_details") final boolean details) {
		List<Object> nodeList = new ArrayList<Object>();
		Gson gson = new Gson();

		String query = "MATCH (n"+ (nodeLabel != null  && !("".equals(nodeLabel)) ? ":" + WordUtils.capitalize(nodeLabel) : "") + ") WHERE n."  + propertyName + " =~ '(?i).*" + propertyValue + ".*' RETURN n";
		query = limit > -1 ? (query + " LIMIT " + limit ): query;
		
		List<Datum> results = Neo4JClient.sendTransactionalCypherQuery(query).getResults().get(0).getData();
		
		
		for (Datum line : results) {
			Map<String, Object> resultMap = (Map<String, Object>) line.getRow().get(0);
			
			Map<String, Object> nodeInfo = new HashMap<String, Object>();
			
			if (compact_mode) {
				nodeList.add(resultMap.get(propertyName));
			} else {
				if (details) {
					nodeInfo.putAll(resultMap);
				} else {
					nodeInfo.put(propertyName, resultMap.get(propertyName));
					nodeInfo.put("uri", resultMap.get("uri"));
				}
				nodeList.add(nodeInfo);
			}

		
		}
		return Response.status(Status.OK).entity(gson.toJson(nodeList)).build();
	}

}
