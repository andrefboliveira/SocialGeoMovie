package com.socialgeomovie.config;

import com.socialgeomovie.clients.Neo4JClient;
import com.socialgeomovie.utils.exceptions.Neo4JRequestException;

public abstract class Neo4JConfig {
	
	public static void setUniqueConstraints() {
		try {
			Neo4JClient.createUniquenessConstraint("Movie", "uri");
		} catch (Neo4JRequestException e) {
		} 	
		
		try {
			Neo4JClient.createUniquenessConstraint("Movie", "title");
		} catch (Neo4JRequestException e) {
		} 		
		
		try {
			Neo4JClient.createUniquenessConstraint("Movie", "id_trakt");
		}  catch (Neo4JRequestException e) {
		}
		
		try {
			Neo4JClient.createUniquenessConstraint("Movie", "id_imdb");
		}  catch (Neo4JRequestException e) {
		}
		
		try {
			Neo4JClient.createUniquenessConstraint("Movie", "id_tmdb");
		}  catch (Neo4JRequestException e) {
		}
		
		try {
			Neo4JClient.createUniquenessConstraint("Cast", "uri");
		} catch (Neo4JRequestException e) {
		} 	
		
		try {
			Neo4JClient.createUniquenessConstraint("Cast", "name");
		}  catch (Neo4JRequestException e) {
		}
		
		try {
			Neo4JClient.createUniquenessConstraint("Cast", "id_trakt");
		}  catch (Neo4JRequestException e) {
		}
		
		try {
			Neo4JClient.createUniquenessConstraint("Cast", "id_imdb");
		} catch (Neo4JRequestException e) {
		}
		
		try {
			Neo4JClient.createUniquenessConstraint("Cast", "id_tmdb");
		}  catch (Neo4JRequestException e) {
		}
		
		try {
			Neo4JClient.createUniquenessConstraint("Person", "uri");
		} catch (Neo4JRequestException e) {
		} 	
		
		try {
			Neo4JClient.createUniquenessConstraint("Person", "name");
		}  catch (Neo4JRequestException e) {
		}
		
//		try {
//			Neo4JClient.createUniquenessConstraint("Location", "uri");
//		}  catch (Neo4JRequestException e) {
//		}
		
		try {
			Neo4JClient.createUniquenessConstraint("Location", "entity");
		}  catch (Neo4JRequestException e) {
		}
		
		try {
			Neo4JClient.createUniquenessConstraint("Country", "iso_3166_1");
		}  catch (Neo4JRequestException e) {
		}
		
		try {
			Neo4JClient.createUniquenessConstraint("Tweet", "url");
		}  catch (Neo4JRequestException e) {
		}
		
		try {
			Neo4JClient.createUniquenessConstraint("Year", "year");
		}  catch (Neo4JRequestException e) {
		}
		
		try {
			Neo4JClient.createUniquenessConstraint("Month", "month");
		}  catch (Neo4JRequestException e) {
		}
		
		try {
			Neo4JClient.createUniquenessConstraint("Day", "day");
		}  catch (Neo4JRequestException e) {
		}
			
		
	}

	
	public static void cleanDB() {
		Neo4JClient.sendTransactionalCypherQuery("MATCH (n) WHERE size(labels(n)) = 0 DELETE n");
	}
	
	public static void deleteAll() {
		Neo4JClient.sendTransactionalCypherQuery("MATCH (n) DETACH DELETE n");
	}

}
