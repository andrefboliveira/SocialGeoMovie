
package com.socialgeomovie.pojos.neo4j.cypher;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CypherResultNormal  extends CypherResults{

    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("errors")
    @Expose
    private List<Object> errors = null;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public List<Object> getErrors() {
        return errors;
    }

    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

}
