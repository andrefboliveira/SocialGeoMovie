
package com.socialgeomovie.pojos.neo4j.cypher;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Row {

    @SerializedName("weight")
    @Expose
    private Integer weight;

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

}
