
package com.socialgeomovie.pojos.sparql;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DBpediaMovieResult {

    @SerializedName("head")
    @Expose
    private Head head;
    @SerializedName("results")
    @Expose
    private ResultsMovie results;

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public ResultsMovie getResults() {
        return results;
    }

    public void setResults(ResultsMovie results) {
        this.results = results;
    }

}
