
package com.socialgeomovie.pojos.neo4j.cypher;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Properties {

    @SerializedName("weight")
    @Expose
    private Integer weight;
    @SerializedName("spokes")
    @Expose
    private Integer spokes;

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getSpokes() {
        return spokes;
    }

    public void setSpokes(Integer spokes) {
        this.spokes = spokes;
    }

}
