package com.socialgeomovie.servlets;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.socialgeomovie.clients.OpenSubsClient;
import com.socialgeomovie.clients.TraktClient;
import com.uwetrottmann.trakt5.entities.CastMember;
import com.uwetrottmann.trakt5.entities.Movie;

// http://localhost:8080/aw2017/rest/movies
@Path("movies")
public class MoviesServlet 
{
	@GET
	@Produces("application/json")
	public Response testGet()
	{
		
		return Response.status(Status.OK).entity("{\"status\":\"OK\"}").build();
	}
	
	@GET
	@Path("import")
	@Produces("application/json")
	public Response importData()
	{
		TraktClient trakt = new TraktClient();
		OpenSubsClient openSubs = new OpenSubsClient();
		List<Movie> movies;
		try 
		{
			movies = trakt.getPopularMovies(1, 10);
			
			int count = movies.size();
			for(int i=0; i<count; i++)
			{
				Movie movie = movies.get(i);
				
				// TODO store movie data
				
				//System.out.println(movie.ids.imdb);
				List<CastMember> cast = trakt.getCast(movie.ids.trakt+"");
				int castCount = cast.size();
				for(int j=0; j<castCount; j++)
				{
					CastMember castMember = cast.get(j);
					// TODO store cast information
				}

				// TODO get subtitles
				String subtitle = openSubs.getSubtitle(movie.ids.imdb);
				if(!subtitle.isEmpty())
				{
				
					// TODO process subtitles
					// TODO store subtitle information
				}
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Response.status(Status.OK).entity("{\"status\":\"OK\"}").build();
	}
}