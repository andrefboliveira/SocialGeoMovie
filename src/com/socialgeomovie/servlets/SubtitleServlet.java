package com.socialgeomovie.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.language.v1.Entity;
import com.google.gson.Gson;
import com.socialgeomovie.clients.Analyze;
import com.socialgeomovie.clients.OpenSubsClient;
import com.socialgeomovie.pojos.Subtitle;

@Path("/subtitle")
public class SubtitleServlet {
	// http://localhost:8080/aw2017/rest/subtitle
	
	private static final Logger logger = LoggerFactory.getLogger(SubtitleServlet.class);

	/**
	 * Get subtitle of given movie by IMDB id
	 * Example(Volcano): http://localhost:8080/aw2017/rest/subtitle/123456
	 * @throws Exception 
	 */
	@GET
	@Path("/{movie_id}")
	@Produces("text/plain")
	public Response getMovie(@PathParam("movie_id") String movie_id) throws Exception {
		logger.info("Entering SubtitleServlet");
		Map<String, String> retMap = new HashMap<String, String>();
		OpenSubsClient opsc = new OpenSubsClient();
//		Analyze app = new Analyze();

		Subtitle subtitle = opsc.getSubtitle(movie_id);
		retMap.put("subtitleId", ""+subtitle.getSubtitleId());
		retMap.put("downloadLink", subtitle.getDownloadLink());
		retMap.put("fileName", subtitle.getFileName());
		retMap.put("movieId", subtitle.getMovieId());
		retMap.put("subtitleLanguage", subtitle.getLanguage());
		retMap.put("subtitleBody", subtitle.getSubtitleAsString());
		
//		logger.info("\n Before Processing \n");
//		String googleEntities = app.filterByType("My name is Ricardo").toString();
//		for(Entity a : app.analyzeEntitiesText("Ricardo"))
//			logger.info("1");
//		String ret = "Google Entities \n"+ googleEntities + "\nSubtitle as text: \n" + subtitle;
//		logger.info("\n Processing \n");
//		logger.info(googleEntities);
//		logger.info("\n Processed \n");
		Gson gson = new Gson();
		logger.info("Showing subitle of movie " + movie_id);
		return Response.status(Status.OK).entity(gson.toJson(retMap)).build();
	}
}
