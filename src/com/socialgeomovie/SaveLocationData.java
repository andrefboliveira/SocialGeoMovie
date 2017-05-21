package com.socialgeomovie;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.socialgeomovie.clients.Neo4JClient;
import com.socialgeomovie.clients.SaveDataClient;
import com.socialgeomovie.config.Neo4JConfig;
import com.socialgeomovie.pojos.neo4j.GetNodesByLabel;
import com.socialgeomovie.utils.exceptions.Neo4JRequestException;

public class SaveLocationData {
	static void saveData() throws FileNotFoundException, UnsupportedEncodingException, URISyntaxException {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		FileReader json = new FileReader("Files/geo.json");

		Neo4JConfig.setUniqueConstraints();
		LocationEntity[] locationList = gson.fromJson(json, LocationEntity[].class);
		for (LocationEntity locationEntity : locationList) {
			String id_imdb = locationEntity.getImdId();

			GetNodesByLabel[] movieNodes = Neo4JClient.getNodesByLabelAndProperty("Movie", "id_imdb", id_imdb);

			URI movieURI = null;
			if (movieNodes.length >= 1) {
				movieURI = new URI(movieNodes[0].getSelf());
			}

			if (movieURI != null) {
				String title = (String) movieNodes[0].getData().get("title");
				String entity = locationEntity.getEntityValue();

				System.out.println("Processing entity: " + entity);

				Map<String, Object> locationProperties = new HashMap<String, Object>();
				locationProperties.put("entity", entity);
				locationProperties.put("latitude", locationEntity.getLat());
				locationProperties.put("longitude", locationEntity.getLng());
				locationProperties.put("MatchContent", locationEntity.getMatchContent());
				locationProperties.put("MatchOffset", locationEntity.getMatchOffset());

				URI locationNode;
				try {
					locationNode = Neo4JClient.createNodeWithProperties("Location", locationProperties);
					System.out.println("Created location for entity: " + entity);

				} catch (Neo4JRequestException e) {
					GetNodesByLabel[] locationNodes = Neo4JClient.getNodesByLabelAndProperty("Location", "entity",
							entity);
					locationNode = new URI(locationNodes[0].getSelf());
				}

				boolean existingRelation = SaveDataClient.checkRelationExists(movieURI, locationNode, "mentions");

				if (!existingRelation) {
					URI relationship = Neo4JClient.createRelationship(movieURI, locationNode, "mentions");
					System.out.println(
							"Created relationship between location entity: " + entity + " and movie: " + title);

				}
			}

		}

	}

}
