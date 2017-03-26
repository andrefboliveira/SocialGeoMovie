
package com.socialgeomovie.pojos.neo4j.cypher;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CypherResultGraph extends CypherResults {

    @SerializedName("results")
    @Expose
    private List<ResultGraph> results = null;
    @SerializedName("errors")
    @Expose
    private List<Object> errors = null;

    public List<ResultGraph> getResults() {
        return results;
    }

    public void setResults(List<ResultGraph> results) {
        this.results = results;
    }

    public List<Object> getErrors() {
        return errors;
    }

    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

}
