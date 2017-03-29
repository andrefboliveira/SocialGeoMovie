package com.socialgeomovie.clients;


import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.json.impl.provider.entity.JSONObjectProvider;


import com.socialgeomovie.pojos.neo4j.*;
import com.socialgeomovie.pojos.neo4j.cypher.*;
import com.socialgeomovie.servlets.DeprecatedMoviesServlet;
import com.socialgeomovie.utils.Neo4JRequestException;

public abstract class Neo4JClient {
	private static final String SERVER_ROOT_URI = "http://localhost:7474/db/data/";
	private static final String username = "neo4j";
	private static final String password = "aw";

	private static Gson gson = new Gson();

	private static WebResource createWebResource(String Uri) {
		Client client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter(username, password));
		WebResource resource = client.resource(Uri);

		return resource;
	}



	private static void checkDatabaseIsRunning() {
		WebResource resource = createWebResource(SERVER_ROOT_URI);
		ClientResponse response = resource.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		response.close();
	}
	
	public static GetNodeByID getNode(String nodeUri) {

		WebResource resource = createWebResource(nodeUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		GetNodeByID node = gson.fromJson(output, GetNodeByID.class);

		response.close();

		return node;
	}

	private static GetNodeByID getNodeByID(String ID) {
		final String nodeUri = SERVER_ROOT_URI + "node/" + ID;

		WebResource resource = createWebResource(nodeUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		GetNodeByID node = gson.fromJson(output, GetNodeByID.class);

		response.close();

		return node;
	}

	public static GetNodesByLabel[] getNodesByLabel(String label) {
		final String labelUri = SERVER_ROOT_URI + "label/" + label + "/nodes";

		WebResource resource = createWebResource(labelUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		GetNodesByLabel[] nodeList = gson.fromJson(output, GetNodesByLabel[].class);

		response.close();

		return nodeList;
	}

	public static GetNodesByLabel[] getNodesByLabelAndProperty(String label, String parameter, String param_value)
			throws UnsupportedEncodingException {
		String safe_param_value = URLEncoder.encode("\"" + param_value + "\"", "UTF-8");
		final String labelParamUri = SERVER_ROOT_URI + "label/" + label + "/nodes?" + parameter + "=" + safe_param_value;
		
		WebResource resource = createWebResource(labelParamUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		GetNodesByLabel[] nodeList = gson.fromJson(output, GetNodesByLabel[].class);
		
		response.close();

		return nodeList;
	}
	
	public static GetNodesByLabel[] getNodesByLabelAndProperty(String label, String parameter, Number param_value)
			throws UnsupportedEncodingException {
		final String labelParamUri = SERVER_ROOT_URI + "label/" + label + "/nodes?" + parameter + "=" + param_value;
		
		WebResource resource = createWebResource(labelParamUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		GetNodesByLabel[] nodeList = gson.fromJson(output, GetNodesByLabel[].class);
		
		response.close();

		return nodeList;
	}

	private static URI createSimpleNode() {
		final String nodeEntryPointUri = SERVER_ROOT_URI + "node";
		// http://localhost:7474/db/data/node

		WebResource resource = createWebResource(nodeEntryPointUri);

		// POST {} to the node entry point URI
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.entity("{}").post(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		final URI location = response.getLocation();

		response.close();

		return location;
	}

	private static URI createSimpleNodeWithProperties(Map<String, Object> properties) {
		final String nodeEntryPointUri = SERVER_ROOT_URI + "node";
		// http://localhost:7474/db/data/node

		WebResource resource = createWebResource(nodeEntryPointUri);

		// POST {} to the node entry point URI
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.entity(gson.toJson(properties)).post(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		final URI location = response.getLocation();

		response.close();

		return location;
	}

	private static URI createSimpleNodeWithProperty(String propertyName, String propertyValue) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(propertyName, propertyValue);

		return createSimpleNodeWithProperties(properties);
	}
	
	public static ArrayList<String> getAllLabels(String ID) {
		final String labelsUri = SERVER_ROOT_URI + "labels/";

		WebResource resource = createWebResource(labelsUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		ArrayList<String> labels = gson.fromJson(output, ArrayList.class);

		response.close();

		return labels;
	}

	private static URI addNodeLabel(URI nodeUri, String label) {
		final String nodeLabelsUri = nodeUri.toString() + "/labels";
		// http://localhost:7474/db/data/node/{nodeID}/labels

		WebResource resource = createWebResource(nodeLabelsUri);

		// POST {} to the node entry point URI
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.entity(gson.toJson(label)).post(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.NO_CONTENT.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		response.close();

		return nodeUri;
	}

	private static URI addNodeLabels(URI nodeUri, List<String> labels) {
		final String nodeLabelsUri = nodeUri.toString() + "/labels";
		// http://localhost:7474/db/data/node/{nodeID}/labels

		WebResource resource = createWebResource(nodeLabelsUri);

		// POST {} to the node entry point URI
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.entity(gson.toJson(labels)).post(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.NO_CONTENT.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		response.close();

		return nodeUri;
	}
	
	
	
	private static URI updateNodeLabels(URI nodeUri, List<String> labels) {
		final String nodeLabelsUri = nodeUri.toString() + "/labels";
		// http://localhost:7474/db/data/node/{nodeID}/labels

		WebResource resource = createWebResource(nodeLabelsUri);

		// POST {} to the node entry point URI
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.entity(gson.toJson(labels)).put(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.NO_CONTENT.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		response.close();

		return nodeUri;
	}

	public static URI createNode(String label) {
		URI nodeUri = createSimpleNode();

		return addNodeLabel(nodeUri, label);
	}

	public static URI createNodeWithProperty(String label, String propertyName, String propertyValue) {
	
		URI nodeUri = createSimpleNodeWithProperty(propertyName, propertyValue);
	
		return addNodeLabel(nodeUri, label);
	}
	
	public static URI createNodeWithProperty(List <String> labels, String propertyName, String propertyValue) {
		
		URI nodeUri = createSimpleNodeWithProperty(propertyName, propertyValue);
	
		return addNodeLabels(nodeUri, labels);
	}

	public static URI createNodeWithProperties(String label, Map<String, Object> properties) {
		URI nodeUri = createSimpleNodeWithProperties(properties);

		return addNodeLabel(nodeUri, label);
	}

	public static URI createNodeWithProperties(List <String> labels, Map<String, Object> properties) {
		URI nodeUri = createSimpleNodeWithProperties(properties);

		return addNodeLabels(nodeUri, labels);
	}
	
	
	public static void deleteNode(String nodeUri) {
		// IMPORTANT: Usar "DETACH DELETE" em
		// sendTransactionalCypherQuery(query) para apagar nós com relações
		/*
		 * MATCH (n) DETACH DELETE n
		 */
		
		WebResource resource = createWebResource(nodeUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);
	
		if (response.getStatus() != ClientResponse.Status.NO_CONTENT.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}
	
		response.close();
	}

	private static void deleteNodeByID(String ID) {
		// IMPORTANT: Usar "DETACH DELETE" em
		// sendTransactionalCypherQuery(query) para apagar nós com relações
		/*
		 * MATCH (n) DETACH DELETE n
		 */
	
		final String nodeUri = SERVER_ROOT_URI + "node/" + ID;
	
		WebResource resource = createWebResource(nodeUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);
	
		if (response.getStatus() != ClientResponse.Status.NO_CONTENT.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}
	
		response.close();
	}
	
	public static Object getNodeProperty(URI nodeUri, String propertyName) {
		String propertiesUri = nodeUri.toString() + "/properties/" + propertyName;

		WebResource resource = createWebResource(propertiesUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		
		Object property = gson.fromJson(output, Object.class);
		

		response.close();

		return property;
	}
	
	public static Map<String, Object> getNodeProperties(String nodePropertiesUri) {
		WebResource resource = createWebResource(nodePropertiesUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		Type type = new TypeToken<Map<String, Object>>(){}.getType();
		Map<String, Object> properties = gson.fromJson(output, type);

		response.close();

		return properties;
	}
	
	public static Map<String, Object> getNodeProperties(URI nodeUri) {
		String propertiesUri = nodeUri.toString() + "/properties";

		WebResource resource = createWebResource(propertiesUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		Type type = new TypeToken<Map<String, Object>>(){}.getType();
		Map<String, Object> properties = gson.fromJson(output, type);

		response.close();

		return properties;
	}

	private static void addNodeProperty(URI nodeUri, String propertyName, String propertyValue) {
		String propertyUri = nodeUri.toString() + "/properties/" + propertyName;
		// http://localhost:7474/db/data/node/{node_id}/properties/{property_name}

		WebResource resource = createWebResource(propertyUri);

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.entity(gson.toJson(propertyValue)).put(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.NO_CONTENT.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		response.close();
	}
	
	private static void updateNodeProperties(URI nodeUri, String propertyName, String propertyValue) {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(propertyName, propertyValue);
		updateNodeProperties(nodeUri, properties);
	}
	
	private static void updateNodeProperties(URI nodeUri, Map<String, String> properties) {
		String propertyUri = nodeUri.toString() + "/properties";

		WebResource resource = createWebResource(propertyUri);

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.entity(gson.toJson(properties)).put(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.NO_CONTENT.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		response.close();
	}
	
	private static void deleteNodeProperty(URI nodeUri, String propertyName) {
		String propertiesUri = nodeUri.toString() + "/properties/" + propertyName;

		WebResource resource = createWebResource(propertiesUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}
		
		response.close();
	}
	
	private static void deleteNodeProperties(URI nodeUri) {
		String propertiesUri = nodeUri.toString() + "/properties";

		WebResource resource = createWebResource(propertiesUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		response.close();

	}
	
	
	
	public static GetNodeRelationship[] getNodeRelationships(URI nodeUri) {
		String relationsUri = nodeUri.toString() + "/relationships/all";

		WebResource resource = createWebResource(relationsUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		GetNodeRelationship[] relationships = gson.fromJson(output, GetNodeRelationship[].class);

		response.close();

		return relationships;
	}
	
	public static GetNodeRelationship[] getNodeRelationships(String relationsUri) {

		WebResource resource = createWebResource(relationsUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		GetNodeRelationship[] relationships = gson.fromJson(output, GetNodeRelationship[].class);

		response.close();

		return relationships;
	}
	
	public static GetNodeRelationship[] getNodeRelationshipsByType(URI nodeUri, List<String> types) throws UnsupportedEncodingException {
		String safe_types = URLEncoder.encode(String.join("&", types), "UTF-8");
		String relationsUri = nodeUri.toString() + "/relationships/all/" + safe_types;

		WebResource resource = createWebResource(relationsUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		GetNodeRelationship[] relationships = gson.fromJson(output, GetNodeRelationship[].class);

		response.close();

		return relationships;
	}
	
	private static GetRelationshipByID getRelationshipByID(String ID) {
		final String relationUri = SERVER_ROOT_URI + "relationship/" + ID;

		WebResource resource = createWebResource(relationUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		GetRelationshipByID relationship = gson.fromJson(output, GetRelationshipByID.class);

		response.close();

		return relationship;
	}

	public static URI createRelationship(URI startNode, URI endNode, String relationshipType)
			throws URISyntaxException {
		return createRelationshipWithProperties(startNode, endNode, relationshipType, null);
	}

	public static URI createRelationshipWithProperties(URI startNode, URI endNode, String relationshipType,
			Map<String, Object> relationAtributes) throws URISyntaxException {
		String fromUri = startNode.toString() + "/relationships";

		Map<String, Object> relationship = new HashMap<String, Object>();
		relationship.put("to", endNode.toString());
		relationship.put("type", relationshipType);
		
		if (!(relationAtributes == null && relationAtributes.isEmpty())) {
			relationship.put("data", relationAtributes);
		}
		

		String relationshipJson = gson.toJson(relationship);

		WebResource resource = createWebResource(fromUri);

		// POST JSON to the relationships URI
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.entity(relationshipJson).post(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		final URI location = response.getLocation();

		response.close();
		return location;
	}

	private static void deleteRelationshipByID(String ID) {
	
		final String relationUri = SERVER_ROOT_URI + "relationship/" + ID;
	
		WebResource resource = createWebResource(relationUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);
	
		if (response.getStatus() != ClientResponse.Status.NO_CONTENT.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}
	
		response.close();
	}
	
	public static GetRelationshipProperties getRelationshipProperty(String ID, String propertyName) {
		final String relationPropertiesUri = SERVER_ROOT_URI + "relationship/" + ID + "/properties/" + propertyName;

		WebResource resource = createWebResource(relationPropertiesUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		GetRelationshipProperties relationshipProperties = gson.fromJson(output, GetRelationshipProperties.class);

		response.close();

		return relationshipProperties;
	}
	
	public static GetRelationshipProperties[] getRelationshipProperties(String ID) {
		final String relationPropertiesUri = SERVER_ROOT_URI + "relationship/" + ID + "/properties";

		WebResource resource = createWebResource(relationPropertiesUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		GetRelationshipProperties[] relationshipProperties = gson.fromJson(output, GetRelationshipProperties[].class);

		response.close();

		return relationshipProperties;
	}
	
	private static void addRelationshipProperty(URI relationshipUri, String propertyName, String propertyValue)
			throws URISyntaxException {
		String propertyUri = relationshipUri.toString() + "/properties/" + propertyName;

		WebResource resource = createWebResource(propertyUri);

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.entity(gson.toJson(propertyValue)).put(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.NO_CONTENT.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		response.close();
		
	}

	private static void addRelationshipProperties(URI relationshipUri, Map<String, String> relationshipProperties)
			throws URISyntaxException {
		String propertiesUri = relationshipUri.toString() + "/properties";
		
		String relationshipPropertiesJSON = gson.toJson(relationshipProperties);

		WebResource resource = createWebResource(propertiesUri);

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.entity(relationshipPropertiesJSON).put(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.NO_CONTENT.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		response.close();
	}
	
	private static void addRelationshipProperty(URI relationshipUri, String propertyName)
			throws URISyntaxException {
		String propertyUri = relationshipUri.toString() + "/properties/" + propertyName;

		WebResource resource = createWebResource(propertyUri);

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.NO_CONTENT.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		response.close();
		
	}
	
	
	private static void deleteRelationshipProperties(URI relationshipUri)
			throws URISyntaxException {
		String propertiesUri = relationshipUri.toString() + "/properties";
		

		WebResource resource = createWebResource(propertiesUri);

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.NO_CONTENT.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		response.close();
	}
	

	public static CypherResultNormal sendTransactionalCypherQuery(String query) {
		final String txUri = SERVER_ROOT_URI + "transaction/commit";

		WebResource resource = createWebResource(txUri);

		Map<String, Object> payloadRaw = new HashMap<String, Object>();
		ArrayList<Map<String, String>> list_statements = new ArrayList<Map<String, String>>();
		Map<String, String> statement = new HashMap<String, String>();

		statement.put("statement", query);
		list_statements.add(statement);
		payloadRaw.put("statements", list_statements);

		String payload = gson.toJson(payloadRaw);

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.entity(payload).post(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		CypherResultNormal queryResult = gson.fromJson(output, CypherResultNormal.class);
		
		response.close();
		
		return queryResult;
	}
	
	private static CypherResults sendTransactionalCypherQueries(List<Object> statements, Boolean graphResult) {
		final String txUri = SERVER_ROOT_URI + "transaction/commit";

		WebResource resource = createWebResource(txUri);

		Map<String, Object> payloadRaw = new HashMap<String, Object>();
		ArrayList<Map<String, Object>> list_statements = new ArrayList<Map<String, Object>>();
		Map<String, Object> statement = null;
		for (Object object : statements) {

			if (object instanceof String) {
				statement = new HashMap<String, Object>();
				statement.put("statement", (String) object);
				if (graphResult) {
					String[] list = { "row", "graph"};
					statement.put("resultDataContents", list);
				}
				list_statements.add(statement);
			} else if (object instanceof Map<?, ?>) {
				statement.put("parameters", (Map<String, Object>) object);
			}			
		}
		
		payloadRaw.put("statements", list_statements);

		String payload = gson.toJson(payloadRaw);
		

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.entity(payload).post(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}

		String output = response.getEntity(String.class);
		
		CypherResults queryResult;
		
		if (graphResult) {
			queryResult = gson.fromJson(output, CypherResultGraph.class);
		} else {
			queryResult = gson.fromJson(output, CypherResultNormal.class);

		}
		
		response.close();
		
		return queryResult;
	}
	
	public static void safeDeleteNode(int nodeID) {
		String query = "MATCH (n {id_trakt:" + nodeID + "}) DETACH DELETE n";
		System.out.println(query);
		sendTransactionalCypherQuery(query);
	}
	
	public static void createConstraint(String label, String type, String atributo) {
		final String labelConstraintUri = SERVER_ROOT_URI + "schema/constraint/" + label + "/" + type + "/";
		
		Map<String, String> property_keys = new HashMap<String, String>();
		property_keys.put("property_keys", atributo);
		
		WebResource resource = createWebResource(labelConstraintUri);

		// POST {} to the node entry point URI
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.entity(gson.toJson(property_keys)).post(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}
	}
	
	public static void createUniquenessConstraint(String label, String atributo) {
		createConstraint(label, "uniqueness", atributo);		
	}
	
	
	
	public static void deleteConstraint(String label, String type, String atributo) {
		final String labelConstraintUri = SERVER_ROOT_URI + "schema/constraint/" + label + "/" + type + "/" + atributo;
		
		
		WebResource resource = createWebResource(labelConstraintUri);

		// POST {} to the node entry point URI
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);

		if (response.getStatus() != ClientResponse.Status.NO_CONTENT.getStatusCode()) {
			throw new Neo4JRequestException("Failed! " + response.toString());
		}
	}
	
	public static void deleteUniquenessConstraint(String label, String atributo) {
		deleteConstraint(label, "uniqueness", atributo);		

	}
	
	

}
