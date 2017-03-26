
package com.socialgeomovie.pojos.neo4j.cypher;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Properties_ {

    @SerializedName("position")
    @Expose
    private Integer position;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

}
