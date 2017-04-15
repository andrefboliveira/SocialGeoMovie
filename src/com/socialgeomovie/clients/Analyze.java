/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.socialgeomovie.clients;

import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import com.google.cloud.language.spi.v1.LanguageServiceClient;
import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.socialgeomovie.pojos.EntityType;

/**
 * A sample application that uses the Natural Language API to perform entity
 * analysis.
 */
public class Analyze {

	private static Analyze app;

	/**
	 * Filter by type and return values
	 * 
	 * @throws IOException
	 */
	public ArrayList<EntityType> filterByType(String text) throws IOException {
		List<Entity> entities = analyzeEntitiesText(text);
		ArrayList<EntityType> etl = new ArrayList<>();
		if (entities == null || entities.size() == 0) {
			System.out.println("No entities found.");
			return etl;
		}
		System.out.printf("Found %d entit%s.\n", entities.size(), entities.size() == 1 ? "y" : "ies");

		for (Entity entity : entities) {
			int beginOffset = entity.getMentions(0).getText().getBeginOffset();
			if (beginOffset + 1000 < text.length()) {
				String range = text.substring(beginOffset, beginOffset + 1000);
				String match = range.substring(range.indexOf(":") - 2, range.indexOf(":") + 6);
				EntityType et = new EntityType(entity.getType(), entity.getName(), beginOffset, match);
				etl.add(et);
			}
		}
		return etl;
	}

	/**
	 * Print a list of {@code entities}.
	 */
	public void printEntities(PrintStream out, List<Entity> entities) {
		if (entities == null || entities.size() == 0) {
			out.println("No entities found.");
			return;
		}
		out.printf("Found %d entit%s.\n", entities.size(), entities.size() == 1 ? "y" : "ies");

		for (Entity entity : entities) {
			out.printf("%s\n", entity.getName());
			out.printf("\tType: %s\n", entity.getType());
		}
	}

	private static LanguageServiceClient languageApi;

	/**
	 * Constructs a {@link Analyze} which connects to the Cloud Natural Language
	 * API.
	 * @throws IOException 
	 */
	public Analyze() throws IOException {
		super();
		this.languageApi = LanguageServiceClient.create();
	}

	/**
	 * Gets {@link Entity}s from the string {@code text}.
	 */
	public static List<Entity> analyzeEntitiesText(String text) throws IOException {
		// Note: This does not work on App Engine standard.
		Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();
		AnalyzeEntitiesRequest request = AnalyzeEntitiesRequest.newBuilder().setDocument(doc)
				.setEncodingType(EncodingType.UTF16).build();
		AnalyzeEntitiesResponse response = languageApi.analyzeEntities(request);
		return response.getEntitiesList();
	}

	/**
	 * Gets {@link Entity}s from the contents of the object at the given GCS
	 * {@code path}.
	 */
	public List<Entity> analyzeEntitiesFile(String path) throws IOException {
		// Note: This does not work on App Engine standard.
		Document doc = Document.newBuilder().setGcsContentUri(path).setType(Type.PLAIN_TEXT).build();
		AnalyzeEntitiesRequest request = AnalyzeEntitiesRequest.newBuilder().setDocument(doc)
				.setEncodingType(EncodingType.UTF16).build();
		AnalyzeEntitiesResponse response = languageApi.analyzeEntities(request);
		return response.getEntitiesList();
	}

}
