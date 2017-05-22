
package com.socialgeomovie.pojos.sparql;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DBpediaPersonResult {

    @SerializedName("head")
    @Expose
    private Head head;
    @SerializedName("results")
    @Expose
    private ResultsPerson results;

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public ResultsPerson getResults() {
        return results;
    }

    public void setResults(ResultsPerson results) {
        this.results = results;
    }

}
