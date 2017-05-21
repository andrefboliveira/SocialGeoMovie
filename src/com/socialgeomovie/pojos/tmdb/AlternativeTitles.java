
package com.socialgeomovie.pojos.tmdb;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AlternativeTitles {

    @SerializedName("titles")
    @Expose
    private List<Title> titles = null;

    public List<Title> getTitles() {
        return titles;
    }

    public void setTitles(List<Title> titles) {
        this.titles = titles;
    }

}
