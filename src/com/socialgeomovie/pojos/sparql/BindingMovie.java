
package com.socialgeomovie.pojos.sparql;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BindingMovie {

    @SerializedName("movieResource")
    @Expose
    private MovieResource movieResource;
    @SerializedName("wikipediaPage")
    @Expose
    private WikipediaPage wikipediaPage;
    @SerializedName("directorName")
    @Expose
    private DirectorName directorName;

    public MovieResource getMovieResource() {
        return movieResource;
    }

    public void setMovieResource(MovieResource movieResource) {
        this.movieResource = movieResource;
    }

    public WikipediaPage getWikipediaPage() {
        return wikipediaPage;
    }

    public void setWikipediaPage(WikipediaPage wikipediaPage) {
        this.wikipediaPage = wikipediaPage;
    }

    public DirectorName getDirectorName() {
        return directorName;
    }

    public void setDirectorName(DirectorName directorName) {
        this.directorName = directorName;
    }

}
