package com.socialgeomovie.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.socialgeomovie.clients.Neo4JClient;
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;

public class Servlet {
	
	public static Map<String, Object> updateResource(String resourceType, String resourceID, String requestJSON) {
		Gson gson = new Gson();
		
		Type type = new TypeToken<Map<String, Object>>() {
		}.getType();
		Map<String, Object> request = gson.fromJson(requestJSON, type);

		Map<String, Object> nodeInfo = new HashMap<String, Object>();

		try {
			GetNodesByLabel[] resourceNodes = Neo4JClient.getNodesByLabelAndProperty(resourceType, "uri", resourceID);
			GetNodesByLabel resourceNode = resourceNodes[0];
			
			nodeInfo = Merge.mergeMapOverwrite(resourceNode.getData(), request);
			Neo4JClient.updateNodeProperties(new URI(resourceNode.getSelf()), nodeInfo);

		} catch (UnsupportedEncodingException | URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return nodeInfo;
	}
}
